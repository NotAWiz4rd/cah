package com.afms.cahgame.gui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.afms.cahgame.R;
import com.afms.cahgame.gui.components.LobbyListAdapter;
import com.afms.cahgame.util.Database;

import java.util.ArrayList;

public class SearchLobby extends AppCompatActivity {

    private ListView listView;
    private ImageButton btn_search_lobby_back;
    private LobbyListAdapter lobbyListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lobby);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        lobbyListAdapter = new LobbyListAdapter(this, new ArrayList<>());
        lobbyListAdapter.addAll(Database.getLobbies().values());
        listView.setAdapter(lobbyListAdapter);
    }

    private void initializeUIElements() {
        listView = findViewById(R.id.list_search_lobby);
        btn_search_lobby_back = findViewById(R.id.btn_search_lobby_back);
    }

    private void initializeUIEvents() {
        btn_search_lobby_back.setOnClickListener(event -> finish());
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
