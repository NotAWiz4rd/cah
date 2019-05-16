package com.afms.cahgame.gui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.SwipeResultListener;
import com.afms.cahgame.util.Database;

public class Main extends AppCompatActivity {

    // ui elements
    private FrameLayout contentView;
    private Button btn_create_lobby;
    private Button btn_search_lobby;
    private Button btn_explore_decks;
    private ImageButton btn_settings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentView = findViewById(R.id.layout_main);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        Database.initializeDatabaseConnections();

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

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_create_lobby.toString(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CreateLobby.class));
        });
        btn_search_lobby.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_search_lobby.toString(), Toast.LENGTH_SHORT).show();
        });
        btn_explore_decks.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_explore_decks.toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ExploreDecks.class);
            startActivity(intent);
        });
        btn_settings.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_settings.toString(), Toast.LENGTH_SHORT).show();

            FullSizeCard fullSizeCard = new FullSizeCard(this, new Card(Colour.WHITE, "Test"));
            fullSizeCard.setSwipeGestures(FullSizeCard.SWIPE_ALL_DIRECTION);
            fullSizeCard.setSwipeResultListener(new SwipeResultListener() {
                @Override
                public void onSwipeLeft() {
                    Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeRight() {
                    Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeUp() {
                    Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeDown() {
                    Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT).show();
                }
            });

            fullSizeCard.addOptionButton("Close", v -> {
                contentView.removeView(fullSizeCard);
            });
            fullSizeCard.addOptionButton("Test", v -> {
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            });
            fullSizeCard.setDimBackground(true);
            contentView.addView(fullSizeCard);
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
