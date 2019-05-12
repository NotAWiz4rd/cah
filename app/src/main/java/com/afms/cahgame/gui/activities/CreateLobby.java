package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.ValueSelector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateLobby extends AppCompatActivity {

    // statics
    private final static int DEFAULT_HANDCARD_COUNT = 5;
    private final static int DEFAULT_PLAYER_COUNT = 3;
    private final static int MIN_PLAYER_COUNT = 2;
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

    private Map<String, Lobby> lobbies = new HashMap<>();
    private String playerName;
    private DatabaseReference lobbiesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        hideUI();
        initializeDatabaseConnection();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeDatabaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbiesReference = database.getReference("lobbies");

        lobbiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<Map<String, Lobby>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Lobby>>() {
                };
                lobbies = dataSnapshot.getValue(genericTypeIndicator);
                if (lobbies == null) {
                    lobbies = new HashMap<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("ERROR", "Failed to get lobbies from server.", error.toException());
            }
        });
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

            if (lobbies.containsKey(lobbyId)) {
                Toast.makeText(this, "A lobby with this name already exists", Toast.LENGTH_LONG).show();
                return;
            }
            if (lobbyId.isEmpty()) {
                Toast.makeText(this, "Please enter a lobbyname to proceed.", Toast.LENGTH_LONG).show();
                return;
            }

            playerName = "Player1"; // todo for testing purposes only
            lobbies.put(lobbyId, new Lobby(lobbyId, playerName, "")); // todo add password
            lobbiesReference.setValue(lobbies);
        });
        btn_select_deck.setOnClickListener(event -> Toast.makeText(this, "clicked " + btn_select_deck.toString(), Toast.LENGTH_SHORT).show());
        btn_back.setOnClickListener(event -> {
            Toast.makeText(this, "clicked " + btn_back.toString(), Toast.LENGTH_SHORT).show();
            finish();
        });
        input_player_count.setOnClickListener(event -> value_selector_player_count.show(getSupportFragmentManager(), "value_selector_player_count"));
        input_handcard_count.setOnClickListener(event -> value_selector_handcard_count.show(getSupportFragmentManager(), "value_selector_handcard_count"));
    }

    public void setValue_player_count(int value_player_count) {
        this.value_player_count.setValue(value_player_count);
    }

    public void setValue_handcard_count(int value_handcard_count) {
        this.value_handcard_count.setValue(value_handcard_count);
    }

    private void createLobby() {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("game", new Game(createSampleDeck(), createSamplePlayers(), 6));
        intent.putExtra("lobbyId", "01");
        // todo make current playername configurable to improve testing
        intent.putExtra("name", "Player1");
        intent.putExtra("host", "Player1");
        startActivity(intent);
    }

    private Deck createSampleDeck() {
        return new Deck("testdeck", getSampleWhiteCards(), getSampleBlackCards());
    }

    private List<Card> getSampleBlackCards() {
        return Arrays.asList(
                new Card(Colour.BLACK, "Black Lorem Ipsum0"),
                new Card(Colour.BLACK, "Black Lorem Ipsum1"),
                new Card(Colour.BLACK, "Black Lorem Ipsum2"),
                new Card(Colour.BLACK, "Black Lorem Ipsum3"),
                new Card(Colour.BLACK, "Black Lorem Ipsum4"),
                new Card(Colour.BLACK, "Black Lorem Ipsum5"),
                new Card(Colour.BLACK, "Black Lorem Ipsum6"),
                new Card(Colour.BLACK, "Black Lorem Ipsum7"),
                new Card(Colour.BLACK, "Black Lorem Ipsum8"));
    }

    private List<Card> getSampleWhiteCards() {
        return Arrays.asList(
                new Card(Colour.WHITE, "White Lorem Ipsum0"),
                new Card(Colour.WHITE, "White Lorem Ipsum1"),
                new Card(Colour.WHITE, "White Lorem Ipsum2"),
                new Card(Colour.WHITE, "White Lorem Ipsum3"),
                new Card(Colour.WHITE, "White Lorem Ipsum4"),
                new Card(Colour.WHITE, "White Lorem Ipsum5"),
                new Card(Colour.WHITE, "White Lorem Ipsum6"),
                new Card(Colour.WHITE, "White Lorem Ipsum7"),
                new Card(Colour.WHITE, "White Lorem Ipsum8"),
                new Card(Colour.WHITE, "White Lorem Ipsum9"),
                new Card(Colour.WHITE, "White Lorem Ipsum10"),
                new Card(Colour.WHITE, "White Lorem Ipsum11"),
                new Card(Colour.WHITE, "White Lorem Ipsum12"),
                new Card(Colour.WHITE, "White Lorem Ipsum13"),
                new Card(Colour.WHITE, "White Lorem Ipsum14"),
                new Card(Colour.WHITE, "White Lorem Ipsum15"),
                new Card(Colour.WHITE, "White Lorem Ipsum16"),
                new Card(Colour.WHITE, "White Lorem Ipsum18"),
                new Card(Colour.WHITE, "White Lorem Ipsum19"),
                new Card(Colour.WHITE, "White Lorem Ipsum20"),
                new Card(Colour.WHITE, "White Lorem Ipsum21"),
                new Card(Colour.WHITE, "White Lorem Ipsum22"),
                new Card(Colour.WHITE, "White Lorem Ipsum23"),
                new Card(Colour.WHITE, "White Lorem Ipsum24"),
                new Card(Colour.WHITE, "White Lorem Ipsum25"),
                new Card(Colour.WHITE, "White Lorem Ipsum26"),
                new Card(Colour.WHITE, "White Lorem Ipsum27"),
                new Card(Colour.WHITE, "White Lorem Ipsum28"),
                new Card(Colour.WHITE, "White Lorem Ipsum29"),
                new Card(Colour.WHITE, "White Lorem Ipsum30"));
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
