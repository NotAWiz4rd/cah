package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.DeckSelectorDialog;
import com.afms.cahgame.gui.components.ValueSelector;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;

public class CreateLobby extends AppCompatActivity {

    // statics
    private final static int DEFAULT_HANDCARD_COUNT = 7;
    private final static int DEFAULT_PLAYER_COUNT = 5;
    private final static int MIN_PLAYER_COUNT = Game.MIN_PLAYERS;
    private final static int MAX_PLAYER_COUNT = 8;
    private final static int MIN_HANDCARD_COUNT = 3;
    private final static int MAX_HANDCARD_COUNT = 10;

    // ui elements
    private Button btn_create_lobby;
    private Button btn_select_deck;
    private ImageButton btn_back;

    private EditText input_lobby_name;
    private EditText input_handcard_count;
    private EditText input_player_count;
    private EditText input_create_lobby_password;
    private EditText input_select_deck;

    private SharedPreferences settings;

    // variables
    private ValueSelector value_selector_player_count;
    private ValueSelector value_selector_handcard_count;

    private MutableLiveData<Integer> value_player_count = new MutableLiveData<>();
    private MutableLiveData<Integer> value_handcard_count = new MutableLiveData<>();

    private DeckSelectorDialog deckSelectorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        value_handcard_count.setValue(DEFAULT_HANDCARD_COUNT);
        value_handcard_count.observe(this, integer -> input_handcard_count.setText(String.valueOf(integer)));
        value_player_count.setValue(DEFAULT_PLAYER_COUNT);
        value_player_count.observe(this, integer -> input_player_count.setText(String.valueOf(integer)));

        input_lobby_name.setSelection(input_lobby_name.getText().length());
        input_create_lobby_password.setSelection(input_create_lobby_password.getText().length());
    }

    private void initializeUIElements() {
        btn_create_lobby = findViewById(R.id.btn_create_lobby_create_lobby);
        btn_select_deck = findViewById(R.id.btn_create_lobby_select_deck);
        btn_back = findViewById(R.id.btn_create_lobby_back);
        input_handcard_count = findViewById(R.id.input_create_lobby_handcard_count);
        input_lobby_name = findViewById(R.id.input_create_lobby_name);
        input_player_count = findViewById(R.id.input_create_lobby_player_count);
        input_select_deck = findViewById(R.id.input_create_lobby_select_deck);
        input_create_lobby_password = findViewById(R.id.input_create_lobby_password);

        ArrayList<String> player_count_values = new ArrayList<>();
        for (int i = MIN_PLAYER_COUNT; i <= MAX_PLAYER_COUNT; i++) {
            player_count_values.add(String.valueOf(i));
        }
        value_selector_player_count = ValueSelector.create(getString(R.string.select_player_count), player_count_values);
        value_selector_player_count.setResultListener(result -> value_player_count.setValue(Integer.valueOf(result)));


        ArrayList<String> handcard_count_values = new ArrayList<>();
        for (int i = MIN_HANDCARD_COUNT; i <= MAX_HANDCARD_COUNT; i++) {
            handcard_count_values.add(String.valueOf(i));
        }
        value_selector_handcard_count = ValueSelector.create(getString(R.string.select_handcard_count), handcard_count_values);
        value_selector_handcard_count.setResultListener(result -> value_handcard_count.setValue(Integer.valueOf(result)));

        deckSelectorDialog = DeckSelectorDialog.create(getString(R.string.title_deck_select));
    }

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            String lobbyId = input_lobby_name.getText().toString();
            String deckName = input_select_deck.getText().toString().equals("") ? "allcardsdeck" : input_select_deck.getText().toString();
            int playerCount = Integer.parseInt(input_player_count.getText().toString());
            int handCardCount = Integer.parseInt(input_handcard_count.getText().toString());

            if (Database.getLobbies().containsKey(lobbyId)) {
                Toast.makeText(this, getString(R.string.lobbyNameExists), Toast.LENGTH_LONG).show();
                return;
            }
            if (lobbyId.isEmpty()) {
                Toast.makeText(this, getString(R.string.missingLobbyname), Toast.LENGTH_LONG).show();
                return;
            }

            if (Database.getDeck(deckName).getBlackCards().size() < 5 || Database.getDeck(deckName).getWhiteCards().size() <= handCardCount * playerCount) {
                Toast.makeText(this, getString(R.string.notEnoughCards), Toast.LENGTH_LONG).show();
                return;
            }

            String playerName = settings.getString("player", Util.getRandomName());
            Util.saveName(settings, playerName);
            Database.addLobby(lobbyId, new Lobby(
                    lobbyId,
                    playerName,
                    input_create_lobby_password.getText().toString(),
                    deckName,
                    handCardCount,
                    playerCount));

            Intent intent = new Intent(this, WaitingLobby.class);
            intent.putExtra(getString(R.string.lobbyId), lobbyId);
            startActivity(intent);
            finish();
        });
        btn_select_deck.setOnClickListener(event -> deckSelectorDialog.show(getSupportFragmentManager(), "deck_selector"));
        btn_back.setOnClickListener(event -> {
            finish();
        });
        input_player_count.setOnClickListener(event -> value_selector_player_count.show(getSupportFragmentManager(), "value_selector_player_count"));
        input_handcard_count.setOnClickListener(event -> value_selector_handcard_count.show(getSupportFragmentManager(), "value_selector_handcard_count"));

        deckSelectorDialog.setResultListener(result -> {
            input_select_deck.setText(result);
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
