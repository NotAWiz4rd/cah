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
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.ValueSelector;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;

public class CreateLobby extends AppCompatActivity {

    // statics
    private final static int DEFAULT_HANDCARD_COUNT = 7;
    private final static int DEFAULT_PLAYER_COUNT = 5;
    private final static int MIN_PLAYER_COUNT = 3;
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
    private EditText input_select_deck;

    private SharedPreferences settings;

    // variables
    private ValueSelector value_selector_player_count;
    private ValueSelector value_selector_handcard_count;

    private MutableLiveData<Integer> value_player_count = new MutableLiveData<>();
    private MutableLiveData<Integer> value_handcard_count = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        hideUI();
        createCards();
        createSampleDeck();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        value_handcard_count.setValue(DEFAULT_HANDCARD_COUNT);
        value_handcard_count.observe(this, integer -> input_handcard_count.setText(String.valueOf(integer)));
        value_player_count.setValue(DEFAULT_PLAYER_COUNT);
        value_player_count.observe(this, integer -> input_player_count.setText(String.valueOf(integer)));
    }

    private void initializeUIElements() {
        btn_create_lobby = findViewById(R.id.btn_create_lobby_create_lobby);
        btn_select_deck = findViewById(R.id.btn_create_lobby_select_deck);
        btn_back = findViewById(R.id.btn_create_lobby_back);
        input_handcard_count = findViewById(R.id.input_create_lobby_handcard_count);
        input_lobby_name = findViewById(R.id.input_create_lobby_name);
        input_player_count = findViewById(R.id.input_create_lobby_player_count);
        input_select_deck = findViewById(R.id.input_create_lobby_select_deck);

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
    }

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            String lobbyId = input_lobby_name.getText().toString();

            if (Database.getLobbies().containsKey(lobbyId)) {
                Toast.makeText(this, "A lobby with this name already exists", Toast.LENGTH_LONG).show();
                return;
            }
            if (lobbyId.isEmpty()) {
                Toast.makeText(this, "Please enter a lobbyname to proceed.", Toast.LENGTH_LONG).show();
                return;
            }

            String playerName = settings.getString("player", Util.getRandomName());
            Util.saveName(settings, playerName);
            Database.addLobby(lobbyId, new Lobby(
                    lobbyId,
                    playerName,
                    "", // todo add password
                    Integer.parseInt(input_handcard_count.getText().toString()),
                    Integer.parseInt(input_player_count.getText().toString())));

            Intent intent = new Intent(this, WaitingLobby.class);
            intent.putExtra("lobbyId", lobbyId);
            startActivity(intent);
        });
        btn_select_deck.setOnClickListener(event -> Toast.makeText(this, "clicked " + btn_select_deck.toString(), Toast.LENGTH_SHORT).show());
        btn_back.setOnClickListener(event -> {
            finish();
        });
        input_player_count.setOnClickListener(event -> value_selector_player_count.show(getSupportFragmentManager(), "value_selector_player_count"));
        input_handcard_count.setOnClickListener(event -> value_selector_handcard_count.show(getSupportFragmentManager(), "value_selector_handcard_count"));
    }

    private void createSampleDeck() {
        if (Util.getDataDeckFromName("testdeck") != null) {
            return;
        }
        com.afms.cahgame.data.Deck deck = new com.afms.cahgame.data.Deck("testdeck");
        deck.addCard(1);
        deck.addCard(2);
        deck.addCard(3);
        deck.addCard(4);
        deck.addCard(5);
        deck.addCard(6);
        deck.addCard(7);
        deck.addCard(0);
        deck.addCard(8);
        deck.addCard(9);
        deck.addCard(10);
        deck.addCard(11);
        deck.addCard(12);
        deck.addCard(13);
        deck.addCard(14);
        deck.addCard(15);
        deck.addCard(16);
        deck.addCard(17);
        deck.addCard(18);
        deck.addCard(19);
        deck.addCard(20);
        deck.addCard(21);
        deck.addCard(22);
        deck.addCard(23);
        deck.addCard(24);
        deck.addCard(25);
        deck.addCard(26);
        deck.addCard(27);
        deck.addCard(28);
        deck.addCard(29);
        deck.addCard(30);
        deck.addCard(31);
        deck.addCard(32);
        deck.addCard(33);
        deck.addCard(34);
        deck.addCard(35);
        deck.addCard(36);
        deck.addCard(37);
        deck.addCard(38);
        deck.addCard(39);
        deck.addCard(40);
        Database.addDeck(deck);
    }

    private void createCards() {
        Database.createNewCard("Black Card 1", Colour.BLACK);
        Database.createNewCard("Black Card 2", Colour.BLACK);
        Database.createNewCard("Black Card 3", Colour.BLACK);
        Database.createNewCard("Black Card 4", Colour.BLACK);
        Database.createNewCard("Black Card 5", Colour.BLACK);
        Database.createNewCard("Black Card 6", Colour.BLACK);
        Database.createNewCard("White Card 1", Colour.WHITE);
        Database.createNewCard("White Card 2", Colour.WHITE);
        Database.createNewCard("White Card 3", Colour.WHITE);
        Database.createNewCard("White Card 4", Colour.WHITE);
        Database.createNewCard("White Card 5", Colour.WHITE);
        Database.createNewCard("White Card 6", Colour.WHITE);
        Database.createNewCard("White Card 7", Colour.WHITE);
        Database.createNewCard("White Card 8", Colour.WHITE);
        Database.createNewCard("White Card 9", Colour.WHITE);
        Database.createNewCard("White Card 10", Colour.WHITE);
        Database.createNewCard("White Card 11", Colour.WHITE);
        Database.createNewCard("White Card 12", Colour.WHITE);
        Database.createNewCard("White Card 13", Colour.WHITE);
        Database.createNewCard("White Card 14", Colour.WHITE);
        Database.createNewCard("White Card 15", Colour.WHITE);
        Database.createNewCard("White Card 16", Colour.WHITE);
        Database.createNewCard("White Card 17", Colour.WHITE);
        Database.createNewCard("White Card 18", Colour.WHITE);
        Database.createNewCard("White Card 19", Colour.WHITE);
        Database.createNewCard("White Card 20", Colour.WHITE);
        Database.createNewCard("White Card 21", Colour.WHITE);
        Database.createNewCard("White Card 22", Colour.WHITE);
        Database.createNewCard("White Card 23", Colour.WHITE);
        Database.createNewCard("White Card 24", Colour.WHITE);
        Database.createNewCard("White Card 25", Colour.WHITE);
        Database.createNewCard("White Card 26", Colour.WHITE);
        Database.createNewCard("White Card 27", Colour.WHITE);
        Database.createNewCard("White Card 28", Colour.WHITE);
        Database.createNewCard("White Card 29", Colour.WHITE);
        Database.createNewCard("White Card 30", Colour.WHITE);
        Database.createNewCard("White Card 31", Colour.WHITE);
        Database.createNewCard("White Card 32", Colour.WHITE);
        Database.createNewCard("White Card 33", Colour.WHITE);
        Database.createNewCard("White Card 34", Colour.WHITE);
        Database.createNewCard("White Card 35", Colour.WHITE);
        Database.createNewCard("White Card 36", Colour.WHITE);
        Database.createNewCard("White Card 37", Colour.WHITE);
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
