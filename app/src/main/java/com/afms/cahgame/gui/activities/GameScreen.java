package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Gamestate;
import com.afms.cahgame.game.Player;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.SwipeResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreen extends AppCompatActivity {
    private Game game;
    private Player player;

    private Gamestate lastGamestate;

    private String lobbyId;
    private DatabaseReference gameReference;

    private SharedPreferences settings;

    private ImageButton playerOverview;
    private ConstraintLayout gameScreenLayout;
    private ConstraintLayout playedBlackCard;
    private FullSizeCard playedWhiteCard;
    private FrameLayout lowerFrameLayout;
    private FrameLayout completeFrameLayout;

    private ConstraintLayout userSelectionLayout;
    private ListView userSelectionListView;
    private CardListAdapter userSelectionListAdapter;
    private List<FullSizeCard> playedWhiteCardList;
    private List<FullSizeCard> fullSizeCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Preferences", MODE_PRIVATE);

        setContentView(R.layout.activity_game_screen);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
        showHandCardList();

        lobbyId = (String) getIntent().getSerializableExtra("lobbyId");
        game = (Game) getIntent().getSerializableExtra("game");
        String playerName = (String) getIntent().getSerializableExtra("name");
        player = new Player(playerName); // todo implement namechange when when multiple players have the same name and game changes the name

        saveInfo();
    }
//<------ GUI LOGIC --------------------------------------------------------------------------------------------------------------->
    private void initializeUIElements() {
        playerOverview = findViewById(R.id.player_overview);
        gameScreenLayout = findViewById(R.id.game_screen_layout);
        playedBlackCard = findViewById(R.id.layout_game_screen_playedBlackCard);
        lowerFrameLayout = findViewById(R.id.layout_game_screen_lower);
        completeFrameLayout = findViewById(R.id.game_screen_frameLayout);

        playedWhiteCard = new FullSizeCard(this, new Card(Colour.WHITE, "test"));
        userSelectionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.list_card_select, gameScreenLayout, false);
        userSelectionListView = userSelectionLayout.findViewById(R.id.cardSelectList);

    }

    private void initializeUIEvents() {
        playerOverview.setOnClickListener(event -> {
            Toast.makeText(this, "clicked on players overview", Toast.LENGTH_SHORT).show();
            //TODO show players and their score
        });
        //for testing
        playedBlackCard.setOnClickListener(event -> {
            showPlayedCards();
        });
    }

    private void deleteAllViewsFromLowerFrameLayout(){
        lowerFrameLayout.removeAllViews();
    }

    private void showHandCardList(){
        deleteAllViewsFromLowerFrameLayout();
        lowerFrameLayout.addView(userSelectionLayout);
        userSelectionListAdapter = new CardListAdapter(this, new ArrayList<Card>());
        userSelectionListView.setAdapter(userSelectionListAdapter);

        userSelectionListAdapter.addAll(getHandCards());

        userSelectionListView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
            Card card = (Card) parent.getItemAtPosition(position);
            completeFrameLayout.addView(getFullSizeCardInstance(card, position));
        });
    }

    private void showPlayedCards(){
        deleteAllViewsFromLowerFrameLayout();
        playedWhiteCardList = getPlayedCards();
        playedWhiteCard = playedWhiteCardList.get(0);
        lowerFrameLayout.addView(playedWhiteCard);

    }

    public FullSizeCard getFullSizeCardInstance(Card card, int selectedPosition) {
        if (fullSizeCardList.stream().anyMatch(f -> f.getCard().equals(card))) {
            return fullSizeCardList.stream().filter(f -> f.getCard().equals(card)).findFirst().get();
        } else {
            FullSizeCard fullCard = new FullSizeCard(this, card);
            fullCard.addOptionButton("Close", v -> {
                completeFrameLayout.removeView(fullCard);
            });
            fullCard.setDimBackground(true);
            fullCard.setSwipeResultListener(new SwipeResultListener() {
                @Override
                public void onSwipeLeft() {
                    int nextPos = (selectedPosition + 1) % userSelectionListView.getCount();
                    Toast.makeText(getApplicationContext(), String.valueOf(nextPos), Toast.LENGTH_SHORT).show();
                    completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos));
                }

                @Override
                public void onSwipeRight() {
                    int nextPos = selectedPosition - 1;
                    if(nextPos < 0){
                        nextPos = userSelectionListView.getCount() - 1;
                    }
                    Toast.makeText(getApplicationContext(), String.valueOf(nextPos), Toast.LENGTH_SHORT).show();
                    completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos));
                }

                @Override
                public void onSwipeUp() {
                    //TODO logic for playing this card
                }

                @Override
                public void onSwipeDown() {

                }
            });
            fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS, FullSizeCard.SWIPE_UP);
            fullSizeCardList.add(fullCard);
            return fullCard;
        }
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

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
    }
    //<--------------------------------------------------------------------------------------------------------------------------->

    private List<Card> getHandCards(){
        //TODO here we need to get the cards from the player
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card(
                    Colour.values()[(int) (Math.random() * Colour.values().length)],
                    Arrays.stream(getResources().getStringArray(R.array.sample_card_texts))
                            .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                            .findAny()
                            .get()));
        }
        return cards;
    }

    private List<FullSizeCard> getPlayedCards(){
        //TODO here we need to get the played cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card(
                    Colour.WHITE,
                    Arrays.stream(getResources().getStringArray(R.array.sample_card_texts))
                            .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                            .findAny()
                            .get()));
        }
        List<FullSizeCard> playedCards = new ArrayList<>();
        for (Card card : cards) {
            FullSizeCard fullCard = new FullSizeCard(this, card);
            fullCard.setSwipeResultListener(new SwipeResultListener() {
                @Override
                public void onSwipeLeft() {
                    int nextPos = (playedWhiteCardList.indexOf(fullCard) + 1) % playedWhiteCardList.size();
                    lowerFrameLayout.addView(playedWhiteCardList.get(nextPos));
                }

                @Override
                public void onSwipeRight() {
                    int nextPos = playedWhiteCardList.indexOf(fullCard) - 1;
                    if(nextPos < 0){
                        nextPos = playedWhiteCardList.size() - 1;
                    }
                    lowerFrameLayout.addView(playedWhiteCardList.get(nextPos));
                }

                @Override
                public void onSwipeUp() {
                }

                @Override
                public void onSwipeDown() {
                }
            });
            fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
            playedCards.add(fullCard);
        }

        return playedCards;
    }

    private void updatePlayer() {
        Optional<Player> playerOptional = game.players.stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        playerOptional.ifPresent(player1 -> player = player1);
    }

    /**
     * Executes things based on the current gamestate of the lobby
     */
    private void gameStateLoop() {
        if (game == null) {
            quitGame("Something went terribly wrong...");
        }
        switch (game.getGamestate()) {
            case START:
                onStartGamestate();
                break;
            case ROUNDSTART:
                onRoundStartGamestate();
                break;
            case SUBMIT:
                onSubmitGamestate();
                break;
            case WAITING:
                onWaitingGamestate();
                break;
            case ROUNDEND:
                onRoundEndGamestate();
                break;
            default:
                onGamestateError();
                break;
        }
    }

    private void onStartGamestate() {
        if (currentPlayerIsCardSzar()) {
            onRoundStartGamestate();
            advanceGamestate();
        }
        setPlayerReady();
    }

    private void onRoundStartGamestate() {
        if (currentPlayerIsCardSzar()) {
            game.startNewRound();
            submitGame();
            advanceGamestate();
        }
        setPlayerReady();
    }

    private void onSubmitGamestate() {
        // todo show new cards and black card

        // todo enable user to submit a card from his hand

        if (!currentPlayerIsCardSzar()) {
            submitCard(player.getHand().remove(0)); // todo for testing only
            setPlayerReady();
        } else {
            setPlayerReady();
            advanceGamestate();
        }

        // then wait for input -> do nothing here
    }

    private void onWaitingGamestate() {
        // todo display "waiting for cardszar to choose winning card"
        setPlayerReady();

        if (currentPlayerIsCardSzar()) {
            // todo somehow at some point there are no playedCards...
            game.submitWinningCard(game.getPlayedCards().get(0)); // todo cardszar chooses winning card from played Cards
            submitGame();
            advanceGamestate();
        }
    }

    private void onRoundEndGamestate() {
        if (currentPlayerIsCardSzar()) {
            game.nextCardSzar();
            submitGame();
            advanceGamestate();
        }
        // todo notify player of winning card (only if player is not cardszar), show updated scores
        setPlayerReady();
    }

    private void onGamestateError() {
        quitGame("Something went horribly wrong...");
    }

    private boolean currentPlayerIsCardSzar() {
        if (game == null || game.getCardCzar() == null) {
            return false;
        }
        return game.getCardCzar().equals(player.getName());
    }

    /**
     * For host only!
     * Initializes lobby and pushes it to server.
     */
    private void startGame() {
        game.startNewRound();

        game.setGamestate(Gamestate.START);

        submitGame();
    }

    private void setPlayerReady() {
        this.player.setReady(true);
        submitLobbyPlayer();
    }

    /**
     * Advances Gamestate if all players in lobby are ready, submits lobby after advancing.
     */
    private void advanceGamestate() {
        if (!game.allPlayersReady()) {
            Handler handler = new Handler();
            handler.postDelayed(this::advanceGamestate, 500);
        } else {
            switch (game.getGamestate()) {
                case START:
                    game.setGamestate(Gamestate.ROUNDSTART);
                    break;
                case ROUNDSTART:
                    game.setGamestate(Gamestate.SUBMIT);
                    break;
                case SUBMIT:
                    game.setGamestate(Gamestate.WAITING);
                    break;
                case WAITING:
                    game.setGamestate(Gamestate.ROUNDEND);
                    break;
                case ROUNDEND:
                    game.setGamestate(Gamestate.ROUNDSTART);
                    break;
                default:
                    game.setGamestate(Gamestate.ROUNDSTART);
                    break;
            }
            submitGame();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        super.onPause();

        saveInfo();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        super.onResume();

        String hostName = (String) getIntent().getSerializableExtra("host");

        player = new Player(settings.getString("player", ""));
        lobbyId = settings.getString("lobbyId", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameReference = database.getReference(lobbyId + "-game");

        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                if (game != null) {
                    updatePlayer();
                    if (game.getGamestate() != lastGamestate) {
                        lastGamestate = game.getGamestate();
                        gameStateLoop();
                    }
                } else {
                    quitGame("");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });

        if (hostName != null && hostName.equals(player.getName())) {
            startGame();
        }

        if (game == null) {
            quitGame("");
        }
    }

    private void quitGame(String message) {
        Intent intent = new Intent(this, Main.class);
        intent.putExtra("message", message.length() > 0 ? message : "Your lobby couldn't be found.");
        startActivity(intent);
    }

    private void saveInfo() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("player", player.getName());
        editor.putString("lobbyId", lobbyId);

        editor.apply();
    }

    /**
     * Submits a card.
     *
     * @param card Card to sumbit.
     */
    private void submitCard(Card card) {
        card.setOwner(player);
        game.submitCard(card);
        submitGame();
    }

    private void submitGame() {
        gameReference.setValue(game);
    }

    /**
     * Submits the updated game/player to the server.
     */
    private void submitLobbyPlayer() {
        for (int i = 0; i < game.players.size(); i++) {
            if (game.players.get(i).getName().equals(player.getName())) {
                game.players.set(i, player);
                submitGame();
                return;
            }
        }
    }
}
