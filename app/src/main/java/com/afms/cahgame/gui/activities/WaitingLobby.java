package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Message;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.game.Player;
import com.afms.cahgame.gui.components.ChatBottomSheet;
import com.afms.cahgame.gui.components.MessageDialog;
import com.afms.cahgame.gui.components.ResultListener;
import com.afms.cahgame.gui.components.WaitingListAdapter;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.TaskService;
import com.afms.cahgame.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WaitingLobby extends AppCompatActivity {
    private SharedPreferences settings;

    private ListView listView;
    private ImageButton btn_waiting_lobby_back;
    private Button btn_waiting_lobby_ready;
    private ImageButton btn_waiting_lobby_chat;
    private TextView label_waiting_lobby_name;
    private TextView label_waiting_lobby_count_maxplayer;
    private TextView label_waiting_lobby_count_handcard;
    private WaitingListAdapter waitingListAdapter;

    private ValueEventListener valueEventListener;
    private ChatBottomSheet chatBottomSheet;

    private String lobbyId = "";
    private String playerName;
    private Lobby currentLobby;
    public DatabaseReference lobbyReference;

    public static boolean newChatMessages = false;
    private List<Message> lastMessages = new ArrayList<>();

    private Context context;
    private MessageDialog messageDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        context = this;

        setContentView(R.layout.activity_waiting_lobby);

        hideUI();
        initializeUIElements();
        initializeDatabaseConnection();
        initializeUIEvents();

        lobbyId = (String) getIntent().getSerializableExtra(getString(R.string.lobbyId));
        TaskService.setLobbyId(lobbyId);

        if (Database.getLobby(lobbyId) != null) {
            playerName = settings.getString("player", Util.getRandomName());
            saveRealname(playerName);
            playerName = Database.joinLobby(lobbyId, playerName);
            Util.playerName = playerName;

            if (playerName.equals("")) {
                Intent intent = new Intent(context, Main.class);
                intent.putExtra("message", getString(R.string.cantFindLobby));
                startActivity(intent);
                lobbyReference.removeEventListener(valueEventListener);
                finish();
                return;
            }
            Util.saveName(settings, playerName);
        }
        hideUI();
        initializeDatabaseConnection();
        initializeUIElements();
        initializeUIEvents();
    }

    private void saveRealname(String realname) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("realname", realname);
        editor.apply();
    }

    private void initializeDatabaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbyReference = database.getReference("lobbies/" + lobbyId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lobby tempLobby = dataSnapshot.getValue(Lobby.class);

                if (tempLobby != null && tempLobby.getId() != null) {
                    currentLobby = tempLobby;
                    updatePlayerList();
                    updateLobbyMetadata();

                    if (currentLobby.getMessages() != lastMessages) {
                        newChatMessages = true;
                        (findViewById(R.id.circle_btn_waiting_lobby_chat)).setVisibility(View.VISIBLE);
                        lastMessages = currentLobby.getMessages();
                    }

                    if (!currentLobby.getPlayers().contains(playerName)) {
                        quit(getString(R.string.playerKicked));
                    } else {
                        if (currentLobby.isGameInProgress() && !currentLobby.getHost().equals(playerName)) {
                            Intent intent = new Intent(context, GameScreen.class);
                            intent.putExtra(getString(R.string.lobbyId), lobbyId);
                            intent.putExtra("host", currentLobby.getHost());
                            currentLobby = null;
                            lobbyReference.removeEventListener(valueEventListener);
                            startActivity(intent);
                            finish();
                        } else if (tempLobby == null) {
                            if (currentLobby != null && currentLobby.getPlayers().contains(playerName)) {
                                quit(getString(R.string.lobbyNotAvailable));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(getString(R.string.errorLog), getString(R.string.failedToGetLobby), error.toException());
            }
        };

        lobbyReference.addValueEventListener(valueEventListener);
    }

    private void updateLobbyMetadata() {
        label_waiting_lobby_name.setText(String.valueOf(currentLobby.getId()));
        label_waiting_lobby_count_maxplayer.setText(String.format("%s / %s", currentLobby.getPlayers().size(), currentLobby.getMaxPlayers()));
        label_waiting_lobby_count_handcard.setText(String.valueOf(currentLobby.getHandcardCount()));

        if (currentLobby.getHost().equals(playerName)) {
            btn_waiting_lobby_ready.setText(getString(R.string.label_hoststartgame));
            btn_waiting_lobby_ready.setBackgroundResource(R.drawable.bg_black_radius_10dp);
        } else {
            btn_waiting_lobby_ready.setText(getString(R.string.label_nothost));
            btn_waiting_lobby_ready.setBackgroundResource(R.drawable.bg_grey_radius_10dp);
        }
    }

    private void updatePlayerList() {
        waitingListAdapter = new WaitingListAdapter(this, currentLobby.getPlayers(), currentLobby);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(currentLobby.getHost().equals(playerName) || Util.godMode){
                if(messageDialog == null){
                    String selectedPlayer = (String) parent.getItemAtPosition(position);
                    messageDialog = MessageDialog.create(getString(R.string.label_choose_action), getString(R.string.kick_player_message), new ArrayList<>(Arrays.asList("Kick", "Cancel")));
                    messageDialog.setResultListener(new ResultListener() {
                        @Override
                        public void onItemClick(String result) {
                            if(result.equals("Kick")){
                                Database.removePlayerFromLobby(currentLobby.getId(), selectedPlayer);
                            }
                        }

                        @Override
                        public void clearReference() {
                            messageDialog = null;
                        }
                    });
                    messageDialog.show(getSupportFragmentManager(), "kickPlayer");
                }
            }
        });
        listView.setAdapter(waitingListAdapter);
    }

    private void initializeUIElements() {
        listView = findViewById(R.id.list_waiting_lobby_players);
        btn_waiting_lobby_back = findViewById(R.id.btn_waiting_lobby_back);
        btn_waiting_lobby_ready = findViewById(R.id.btn_waiting_lobby_ready);
        label_waiting_lobby_name = findViewById(R.id.label_waiting_lobby_name);
        label_waiting_lobby_count_maxplayer = findViewById(R.id.label_waiting_lobby_count_maxplayer);
        label_waiting_lobby_count_handcard = findViewById(R.id.label_waiting_lobby_count_handcard);
        btn_waiting_lobby_chat = findViewById(R.id.btn_waiting_lobby_chat);
    }

    private void initializeUIEvents() {
        btn_waiting_lobby_back.setOnClickListener(v -> {
            onBack();
            disableUserInterface();
        });

        btn_waiting_lobby_ready.setOnClickListener(event -> {
            if (currentLobby != null && currentLobby.getHost().equals(playerName)) {
                if (currentLobby.getPlayers().size() < Game.MIN_PLAYERS) {
                    Toast.makeText(context, getString(R.string.notEnoughPlayers), Toast.LENGTH_LONG).show();
                    return;
                }
                currentLobby.setGameInProgress(true);
                lobbyReference.setValue(currentLobby);

                Intent intent = new Intent(context, GameScreen.class);
                intent.putExtra("game", new Game(Database.getDeck(currentLobby.getDeckName()), Collections.singletonList(playerName), currentLobby.getHandcardCount()));
                intent.putExtra(getString(R.string.lobbyId), lobbyId);
                intent.putExtra("host", currentLobby.getHost());
                startActivity(intent);
                finish();
                disableUserInterface();
            }
        });

        btn_waiting_lobby_chat.setOnClickListener(event -> {
            if (chatBottomSheet == null) {
                newChatMessages = false;
                (findViewById(R.id.circle_btn_waiting_lobby_chat)).setVisibility(View.INVISIBLE);
                chatBottomSheet = ChatBottomSheet.create(currentLobby);
                chatBottomSheet.setResultListener(new ResultListener() {
                    @Override
                    public void onItemClick(String result) {
                        Database.sendMessageInLobby(currentLobby.getId(), playerName, result);
                    }

                    @Override
                    public void clearReference() {
                        chatBottomSheet = null;
                    }
                });
                chatBottomSheet.show(getSupportFragmentManager(), "chatWaitingLobby");
            }
            disableUserInterface();
        });
    }


    private void disableUserInterface() {
        btn_waiting_lobby_ready.setEnabled(false);
        btn_waiting_lobby_back.setEnabled(false);
        btn_waiting_lobby_chat.setEnabled(false);
        new Handler().postDelayed(() -> {
            btn_waiting_lobby_ready.setEnabled(true);
            btn_waiting_lobby_back.setEnabled(true);
            btn_waiting_lobby_chat.setEnabled(true);
        }, 250);
    }


    private void hideUI() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+

        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        super.onStop();
        lobbyReference.removeEventListener(valueEventListener);
        currentLobby = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    private void onBack() {
        lobbyReference.removeEventListener(valueEventListener);
        currentLobby = null;
        Database.removePlayerFromLobby(lobbyId, playerName);
        finish();
    }

    private void quit(String message) {
        currentLobby = null;
        lobbyReference.removeEventListener(valueEventListener);
        Intent intent = new Intent(context, Main.class);
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
    }
}
