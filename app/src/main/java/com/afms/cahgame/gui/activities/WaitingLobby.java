package com.afms.cahgame.gui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.game.Player;
import com.afms.cahgame.gui.components.WaitingListAdapter;

import java.util.ArrayList;

public class WaitingLobby extends AppCompatActivity {

    private ListView listView;
    private ImageButton btn_waiting_lobby_back;
    private Button btn_waiting_lobby_ready;
    private WaitingListAdapter waitingListAdapter;
    private ArrayList<Player> playerList;
    private Lobby currentLobby;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        playerList = new ArrayList<>();
        waitingListAdapter = new WaitingListAdapter(this, playerList, currentLobby);
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
