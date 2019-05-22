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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.LobbyListAdapter;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchLobby extends AppCompatActivity {

    private ListView listView;
    private ImageButton btn_search_lobby_back;
    private LobbyListAdapter lobbyListAdapter;
    private RelativeLayout layout_search_lobby_no_lobbies;
    private RelativeLayout layout_search_lobby_lobbies;
    private Button btn_search_lobby_create;

    private SharedPreferences settings;
    Context context = this;

    private static Map<String, Lobby> lobbies = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lobby);
        hideUI();
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        updateLobbyList();
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        Database.lobbiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Lobby>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Lobby>>() {
                };

                lobbies = dataSnapshot.getValue(genericTypeIndicator);
                updateLobbyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERROR", getString(R.string.cantGetLobbies), databaseError.toException());
            }
        });
    }

    private void updateLobbyList() {
        lobbyListAdapter = new LobbyListAdapter(context, new ArrayList<>());
        if(lobbies != null){
            lobbyListAdapter.addAll(lobbies.values());
        }
        listView.setAdapter(lobbyListAdapter);

        if(lobbyListAdapter.getCount() > 0){
            layout_search_lobby_lobbies.setVisibility(View.VISIBLE);
            layout_search_lobby_no_lobbies.setVisibility(View.INVISIBLE);
            layout_search_lobby_lobbies.bringToFront();
            layout_search_lobby_lobbies.getParent().requestLayout();
        } else {
            layout_search_lobby_lobbies.setVisibility(View.INVISIBLE);
            layout_search_lobby_no_lobbies.setVisibility(View.VISIBLE);
            layout_search_lobby_no_lobbies.bringToFront();
            layout_search_lobby_no_lobbies.getParent().requestLayout();
        }
    }

    private void initializeUIElements() {
        listView = findViewById(R.id.list_search_lobby);
        layout_search_lobby_no_lobbies = findViewById(R.id.layout_search_lobby_no_lobbies);
        layout_search_lobby_lobbies = findViewById(R.id.layout_search_lobby_lobbies);
        btn_search_lobby_create = findViewById(R.id.btn_search_lobby_create);
        btn_search_lobby_back = findViewById(R.id.btn_search_lobby_back);
        layout_search_lobby_lobbies.getParent().requestLayout();
    }

    private void initializeUIEvents() {
        btn_search_lobby_back.setOnClickListener(event -> finish());
        btn_search_lobby_create.setOnClickListener(event -> {
            Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateLobby.class);
            intent.putExtra("player", settings.getString("player", Util.getRandomName()));
            startActivity(intent);
        });
        layout_search_lobby_lobbies.setOnClickListener(event -> Toast.makeText(this, "clicked lobbies", Toast.LENGTH_SHORT).show());
        layout_search_lobby_no_lobbies.setOnClickListener(event -> Toast.makeText(this, "clicked no lobbies", Toast.LENGTH_SHORT).show());
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
