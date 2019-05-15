package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.ValueSelector;
import com.afms.cahgame.util.Database;

import java.util.ArrayList;
import java.util.List;

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
    private Button btn_start_game_test;
    private Button btn_select_deck;
    private ImageButton btn_back;

    private EditText input_lobby_name;
    private EditText input_handcard_count;
    private EditText input_player_count;
    private EditText input_select_deck;

    // variables
    private ValueSelector value_selector_player_count;
    private ValueSelector value_selector_handcard_count;

    private MutableLiveData<Integer> value_player_count = new MutableLiveData<>();
    private MutableLiveData<Integer> value_handcard_count = new MutableLiveData<>();

    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
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
        btn_start_game_test = findViewById(R.id.btn_create_lobby_start_game_test);
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
        value_selector_player_count = ValueSelector.showValueSelector(this, getString(R.string.select_player_count), player_count_values);
        value_selector_player_count.setResultListener(result -> value_player_count.setValue(Integer.valueOf(result)));


        ArrayList<String> handcard_count_values = new ArrayList<>();
        for (int i = MIN_HANDCARD_COUNT; i <= MAX_HANDCARD_COUNT; i++) {
            handcard_count_values.add(String.valueOf(i));
        }
        value_selector_handcard_count = ValueSelector.showValueSelector(this, getString(R.string.select_handcard_count), handcard_count_values);
        value_selector_handcard_count.setResultListener(result -> value_handcard_count.setValue(Integer.valueOf(result)));
    }

    private void initializeUIEvents() {
        btn_start_game_test.setOnClickListener(event -> createLobby());
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

            playerName = "Player1"; // todo for testing purposes only
            Database.addLobby(lobbyId, new Lobby(
                    lobbyId,
                    playerName,
                    "", // todo add password
                    Integer.parseInt(input_handcard_count.getText().toString()),
                    Integer.parseInt(input_player_count.getText().toString())));
        });
        btn_select_deck.setOnClickListener(event -> Toast.makeText(this, "clicked " + btn_select_deck.toString(), Toast.LENGTH_SHORT).show());
        btn_back.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_back.toString(), Toast.LENGTH_SHORT).show();
            finish();
        });
        input_player_count.setOnClickListener(event -> value_selector_player_count.show(getSupportFragmentManager(), "value_selector_player_count"));
        input_handcard_count.setOnClickListener(event -> value_selector_handcard_count.show(getSupportFragmentManager(), "value_selector_handcard_count"));
    }

    private void createLobby() {
        // todo check that deck has enough cards for all players
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("game", new Game(Database.getDeck("testdeck"), createSamplePlayers(), Integer.parseInt(input_handcard_count.getText().toString())));
        intent.putExtra("lobbyId", "01");
        // todo make current playername configurable to improve testing
        intent.putExtra("name", "Player1");
        intent.putExtra("host", "Player1");
        startActivity(intent);
    }

    private void createSampleDeck() {
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
    }

    private List<String> createSamplePlayers() {
        List<String> players = new ArrayList<>();
        players.add("Player1");
        players.add("Player2");
        players.add("Player3");
        return players;
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
