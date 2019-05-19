package com.afms.cahgame.gui.activities;

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
    private WaitingListAdapter waitingListAdapter;

    private String lobbyId = "";
    private Lobby currentLobby;
    public DatabaseReference lobbiesReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);

        setContentView(R.layout.activity_waiting_lobby);

        lobbyId = (String) getIntent().getSerializableExtra("lobbyId");
        if (Database.getLobby(lobbyId) != null) {
            String playerName = settings.getString("player", Util.getRandomName());
            playerName = Database.joinLobby(lobbyId, playerName);
            if (playerName.equals("")) {
                Intent intent = new Intent(this, Main.class);
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
        lobbiesReference = database.getReference("lobbies/" + lobbyId);

        lobbiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lobby tempLobby = dataSnapshot.getValue(Lobby.class);

                if (tempLobby != null) {
                    currentLobby = tempLobby;
                    updatePlayerList();
                    // todo change GUI
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to get lobby from server.", error.toException());
            }
        });
    }

    private void updatePlayerList() {
        waitingListAdapter = new WaitingListAdapter(this, currentLobby.getPlayers(), currentLobby);
        listView.setAdapter(waitingListAdapter);
    }

    private void initializeUIElements() {
        listView = findViewById(R.id.list_waiting_lobby_players);
        btn_waiting_lobby_back = findViewById(R.id.btn_waiting_lobby_back);
        btn_waiting_lobby_ready = findViewById(R.id.btn_waiting_lobby_ready);
    }

    private void initializeUIEvents() {
        btn_waiting_lobby_back.setOnClickListener(event -> finish());
        btn_waiting_lobby_ready.setOnClickListener(event -> {
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
