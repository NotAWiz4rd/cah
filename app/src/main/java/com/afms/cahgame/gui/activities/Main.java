package com.afms.cahgame.gui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.gui.components.SettingsDialog;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

public class Main extends AppCompatActivity {

    // ui elements
    private FrameLayout contentView;
    private Button btn_create_lobby;
    private Button btn_search_lobby;
    private Button btn_explore_decks;
    private Button btn_piatest;
    private ImageButton btn_settings;

    private SharedPreferences settings;

    private SettingsDialog settingsDialog;

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

        playerName = settings.getString("player", Util.getRandomName());

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
        btn_piatest = findViewById(R.id.btn_main_piatest);


        settingsDialog = new SettingsDialog();
    }

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            Intent intent = new Intent(this, CreateLobby.class);
            intent.putExtra("player", playerName);
            startActivity(intent);
        });
        btn_search_lobby.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_search_lobby.toString(), Toast.LENGTH_SHORT).show();
        });
        btn_piatest.setOnClickListener(event -> {
            //testing waiting screen
            FrameLayout mainlayout = findViewById(R.id.layout_main);
            ConstraintLayout waitingScreen = (ConstraintLayout) getLayoutInflater().inflate(R.layout.waiting_screen, mainlayout, false);
            ImageView blackCard = waitingScreen.findViewById(R.id.waiting_screen_blackCard);
            ImageView whiteCard = waitingScreen.findViewById(R.id.waiting_screen_whiteCard);
            mainlayout.addView(waitingScreen);
            testAnimation(whiteCard, blackCard);
        });
        btn_explore_decks.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_explore_decks.toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ExploreDecks.class);
            startActivity(intent);
        });
        btn_settings.setOnClickListener(event -> {
            settingsDialog.show(getSupportFragmentManager(), "settingsDialog");
        });

        settingsDialog.setOnClickListener(v -> {
            EditText playerNameView = settingsDialog.getPlayerNameView();
            if (playerNameView == null) {
                return;
            }
            if (playerNameView.getText().toString().equals("")) {
                playerName = Util.getRandomName();
            } else {
                playerName = playerNameView.getText().toString();
            }
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("player", playerName);
            editor.apply();
        });
    }

    private void testAnimation(ImageView whiteCard, ImageView blackCard) {
        ObjectAnimator transOutBlack = ObjectAnimator.ofFloat(blackCard, "translationX", -100f).setDuration(700);
        ObjectAnimator transOutWhite = ObjectAnimator.ofFloat(whiteCard, "translationX", 100f).setDuration(700);
        AnimatorSet transOut = new AnimatorSet();
        transOut.play(transOutBlack).with(transOutWhite);
        transOut.start();
        transOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                whiteCard.bringToFront();
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(blackCard, "scaleX", 0.5f).setDuration(700);
                ObjectAnimator scaleDownY= ObjectAnimator.ofFloat(blackCard, "scaleY", 0.5f).setDuration(700);
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(whiteCard, "scaleX", 2f).setDuration(700);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(whiteCard, "scaleY", 2f).setDuration(700);
                ObjectAnimator transInBlack = ObjectAnimator.ofFloat(blackCard, "translationX", 0f).setDuration(700);
                ObjectAnimator transInWhite = ObjectAnimator.ofFloat(whiteCard, "translationX", 0f).setDuration(700);
                ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(blackCard, "scaleX", 1f).setDuration(700);
                ObjectAnimator scaleBackY= ObjectAnimator.ofFloat(blackCard, "scaleY", 1f).setDuration(700);
                ObjectAnimator scaleBack2X = ObjectAnimator.ofFloat(whiteCard, "scaleX", 1f).setDuration(700);
                ObjectAnimator scaleBack2Y = ObjectAnimator.ofFloat(whiteCard, "scaleY", 1f).setDuration(700);
                AnimatorSet scale = new AnimatorSet();
                AnimatorSet transIn = new AnimatorSet();
                AnimatorSet scaleBack = new AnimatorSet();
                scaleBack.play(scaleBackX).with(scaleBackY).with(scaleBack2X).with(scaleBack2Y);
                transIn.play(transInBlack).with(transInWhite).before(scaleBack);
                scale.play(scaleDownX).with(scaleDownY).with(scaleUpX).with(scaleUpY).before(transIn);
                scale.start();
                scale.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        testAnimation(blackCard, whiteCard);
                    }
                });
            }
        });
        /*transOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(rectangle, "scaleX", 1f).setDuration(100);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(rectangle, "scaleY", 1f).setDuration(100);
                ObjectAnimator dropCircle = ObjectAnimator.ofFloat(circle, "translationY", -300).setDuration(700);
                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleUpX).with(scaleUpY).with(dropCircle);
                rectangle.animate().rotationBy(180f).setDuration(600);
                scaleUp.start();
                scaleUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        testAnimation(circle, rectangle);
                    }
                });
            }
        });*/
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
