package com.afms.cahgame.gui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.gui.components.MessageDialog;
import com.afms.cahgame.gui.components.ResultListener;
import com.afms.cahgame.gui.components.SettingsDialog;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class Main extends AppCompatActivity {

    // ui elements
    private FrameLayout contentView;
    private Button btn_create_lobby;
    private Button btn_search_lobby;
    private Button btn_explore_decks;
    private ImageButton btn_settings;

    private SharedPreferences settings;

    private SettingsDialog settingsDialog;
    private MessageDialog messageDialog;

    private String playerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        contentView = findViewById(R.id.layout_main);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        Database.initializeDatabaseConnections();

        playerName = settings.getString("realname", "");
        if (playerName == null || playerName.equals("")) {
            playerName = settings.getString("player", Util.getRandomName());
        }
        Util.saveName(settings, playerName);

        String message = (String) getIntent().getSerializableExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void initializeUIElements() {
        btn_create_lobby = findViewById(R.id.btn_main_createLobby);
        btn_search_lobby = findViewById(R.id.btn_main_searchLobby);
        btn_explore_decks = findViewById(R.id.btn_main_exploreDecks);
        btn_settings = findViewById(R.id.btn_main_settings);

    }

    @Override
    public void onBackPressed() {
        if(messageDialog == null){
            messageDialog = MessageDialog.create(getResources().getString(R.string.title_quit), new ArrayList<>(Arrays.asList(getString(R.string.ok), getString(R.string.cancel))));
            messageDialog.setResultListener(new ResultListener() {
                @Override
                public void onItemClick(String result) {
                    if (result.equals(getString(R.string.ok))) {
                        ((AppCompatActivity) getApplicationContext()).onBackPressed();
                        System.exit(0);
                    }
                }

                @Override
                public void clearReference() {
                    messageDialog = null;
                }
            });
            messageDialog.show(getSupportFragmentManager(), "quitMessageDialog");
        }
    }

    private void disableUserInterface(){
        btn_create_lobby.setEnabled(false);
        btn_explore_decks.setEnabled(false);
        btn_search_lobby.setEnabled(false);
        btn_settings.setEnabled(false);
        new Handler().postDelayed(() -> {
            btn_create_lobby.setEnabled(true);
            btn_explore_decks.setEnabled(true);
            btn_search_lobby.setEnabled(true);
            btn_settings.setEnabled(true);
        }, 250);
    }

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            Intent intent = new Intent(this, CreateLobby.class);
            intent.putExtra("player", playerName);
            startActivity(intent);
            disableUserInterface();
        });
        btn_search_lobby.setOnClickListener(event -> {
            Intent intent = new Intent(this, SearchLobby.class);
            startActivity(intent);
            disableUserInterface();
        });

        btn_explore_decks.setOnClickListener(event -> {
            Intent intent = new Intent(this, ExploreDecks.class);
            startActivity(intent);
            disableUserInterface();
        });
        btn_settings.setOnClickListener(event -> {
            if(settingsDialog == null){

                settingsDialog = new SettingsDialog();
                settingsDialog.show(getSupportFragmentManager(), "settingsDialog");

                settingsDialog.setResultListener(new ResultListener() {
                    @Override
                    public void onItemClick(String result) {
                        if(result.equals("save")) {
                            EditText playerNameView = settingsDialog.getPlayerNameView();
                            if (playerNameView == null) {
                                return;
                            }
                            if (playerNameView.getText().toString().equals("")) {
                                playerName = Util.getRandomName();
                                Util.saveName(settings, playerName);
                            } else if (playerNameView.getText().toString().equals(getString(R.string.godmodeCommand))) {
                                if (Util.godMode) {
                                    Util.setGodMode(false);
                                    Toast.makeText(getApplicationContext(), getString(R.string.disabledGodmode), Toast.LENGTH_SHORT).show();
                                } else {
                                    Util.setGodMode(true);
                                    Toast.makeText(getApplicationContext(), getString(R.string.enabledGodmode), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                playerName = playerNameView.getText().toString();
                                Util.saveName(settings, playerName);
                            }
                            playerNameView.setText(settings.getString("player", Util.getRandomName()));
                        }
                    }

                    @Override
                    public void clearReference() {
                        settingsDialog = null;
                    }
                });
            }
            disableUserInterface();
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
