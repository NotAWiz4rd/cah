package com.afms.cahgame.gui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.WaitingListAdapter;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingLobby extends AppCompatActivity {
    private SharedPreferences settings;

    private ListView listView;
    private ImageButton btn_waiting_lobby_back;
    private Button btn_waiting_lobby_ready;
    private TextView label_waiting_lobby_name;
    private TextView label_waiting_lobby_count_maxplayer;
    private TextView label_waiting_lobby_count_handcard;
    private WaitingListAdapter waitingListAdapter;

    private ValueEventListener valueEventListener;

    private String lobbyId = "";
    private String playerName;
    private Lobby currentLobby;
    public DatabaseReference lobbyReference;

    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        context = this;

        setContentView(R.layout.activity_waiting_lobby);

        hideUI();
        initializeUIElements();
        initializeDatabaseConnection();
        initializeUIEvents();

        lobbyId = (String) getIntent().getSerializableExtra("lobbyId");
        if (Database.getLobby(lobbyId) != null) {
            playerName = settings.getString("player", Util.getRandomName());
            playerName = Database.joinLobby(lobbyId, playerName);
            if (playerName.equals("")) {
                Intent intent = new Intent(context, Main.class);
                intent.putExtra("message", "Couldn't join the lobby.");
                startActivity(intent);
                return;
            }
            Util.saveName(settings, playerName);
        }
        hideUI();
        initializeDatabaseConnection();
        initializeUIElements();
        initializeUIEvents();
    }

    private void initializeDatabaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbyReference = database.getReference("lobbies/" + lobbyId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lobby tempLobby = dataSnapshot.getValue(Lobby.class);

                if (tempLobby != null) {
                    currentLobby = tempLobby;
                    updatePlayerList();
                    updateLobbyMetadata();
                    // todo change GUI

                    if (currentLobby.isGameInProgress()) {
                        Intent intent = new Intent(context, GameScreen.class);
                        intent.putExtra("lobbyId", lobbyId);
                        startActivity(intent);
                    }
                } else {
                    if (currentLobby.getPlayers().contains(playerName)) {
                        Intent intent = new Intent(context, Main.class);
                        intent.putExtra("message", "The lobby you were trying to reach is not available anymore.");
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to get lobby from server.", error.toException());
            }
        };

        lobbyReference.addValueEventListener(valueEventListener);
    }

    private void updateLobbyMetadata() {
        label_waiting_lobby_name.setText(currentLobby.getId());
        label_waiting_lobby_count_maxplayer.setText(String.format("%s / %s", currentLobby.getPlayers().size(), currentLobby.getMaxPlayers()));
        label_waiting_lobby_count_handcard.setText(String.valueOf(currentLobby.getHandcardCount()));
    }

    private void updatePlayerList() {
        waitingListAdapter = new WaitingListAdapter(this, currentLobby.getPlayers(), currentLobby);
        listView.setAdapter(waitingListAdapter);
    }

    private void initializeUIElements() {
        listView = findViewById(R.id.list_waiting_lobby_players);
        btn_waiting_lobby_back = findViewById(R.id.btn_waiting_lobby_back);
        btn_waiting_lobby_ready = findViewById(R.id.btn_waiting_lobby_ready);
        label_waiting_lobby_name = findViewById(R.id.label_waiting_lobby_name);
        label_waiting_lobby_count_maxplayer = findViewById(R.id.label_waiting_lobby_count_maxplayer);
        label_waiting_lobby_count_handcard = findViewById(R.id.label_waiting_lobby_count_handcard);
    }

    private void initializeUIEvents() {
        btn_waiting_lobby_back.setOnClickListener(event -> {
            lobbyReference.removeEventListener(valueEventListener);
            Database.removePlayerFromLobby(lobbyId, playerName);
            finish();
        });

        btn_waiting_lobby_ready.setOnClickListener(event -> {
            // todo make this only visible for the host and start the game from here
        });
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

}
