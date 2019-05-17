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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.List;
import java.util.Optional;

public class GameScreen extends AppCompatActivity {
    private Game game;
    private Player player;

    private Gamestate lastGamestate;
    private boolean showPlayedCardsAllowed;
    private boolean allowCardSubmitting;

    private String lobbyId;
    private DatabaseReference gameReference;

    private SharedPreferences settings;

    private ImageButton playerOverview;
    private ConstraintLayout gameScreenLayout;
    private ConstraintLayout playedBlackCard;
    private TextView playedBlackCardText;
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
        playedBlackCardText = findViewById(R.id.blackCardText);
        lowerFrameLayout = findViewById(R.id.layout_game_screen_lower);
        completeFrameLayout = findViewById(R.id.game_screen_frameLayout);

        playedBlackCardText.setText("");

        playedWhiteCard = new FullSizeCard(this, new Card(Colour.WHITE, "test"));
        userSelectionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.list_card_select, gameScreenLayout, false);
        userSelectionListView = userSelectionLayout.findViewById(R.id.cardSelectList);

    }

    private void initializeUIEvents() {
        playerOverview.setOnClickListener(event -> {
            Toast.makeText(this, "clicked on players overview", Toast.LENGTH_SHORT).show();
            //TODO show players and their score
        });

        playedBlackCard.setOnClickListener(event -> {
            if (showPlayedCardsAllowed) {
                showPlayedCards(false);
            }
        });
    }

    private void deleteAllViewsFromLowerFrameLayout() {
        lowerFrameLayout.removeAllViews();
    }

    private void showHandCardList() {
        if (player != null) {
            deleteAllViewsFromLowerFrameLayout();
            lowerFrameLayout.addView(userSelectionLayout);
            userSelectionListAdapter = new CardListAdapter(this, new ArrayList<Card>());
            userSelectionListView.setAdapter(userSelectionListAdapter);

            userSelectionListAdapter.addAll(player.getHand());

            userSelectionListView.setOnItemClickListener((parent, view, position, id) -> {
                Card card = (Card) parent.getItemAtPosition(position);
                completeFrameLayout.addView(getFullSizeCardInstance(card, position));
            });
        }
    }

    private void showPlayedCards(boolean allowCardSzarSubmit) {
        deleteAllViewsFromLowerFrameLayout();
        playedWhiteCardList = getPlayedCards(allowCardSzarSubmit);
        if (playedWhiteCardList.size() > 0) {
            playedWhiteCard = playedWhiteCardList.get(0);
            lowerFrameLayout.addView(playedWhiteCard);
        }
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
                    completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos));
                }

                @Override
                public void onSwipeRight() {
                    int nextPos = selectedPosition - 1;
                    if (nextPos < 0) {
                        nextPos = userSelectionListView.getCount() - 1;
                    }
                    completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos));
                }

                @Override
                public void onSwipeUp() {
                    if (!currentPlayerIsCardSzar()) {
                        if (allowCardSubmitting) {
                            submitCard(card);
                            setPlayerReady();
                            allowCardSubmitting = false;
                            showHandCardList();
                        }
                    }
                }

                @Override
                public void onSwipeDown() {
                    // do nothing
                }
            });
            if (allowCardSubmitting) {
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS, FullSizeCard.SWIPE_UP);
            } else {
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
            }
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

    private List<FullSizeCard> getPlayedCards(boolean allowCardSzarSubmit) {
        List<FullSizeCard> playedCards = new ArrayList<>();

        for (Card card : game.getPlayedCards()) {
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
                    if (nextPos < 0) {
                        nextPos = playedWhiteCardList.size() - 1;
                    }
                    lowerFrameLayout.addView(playedWhiteCardList.get(nextPos));
                }

                @Override
                public void onSwipeUp() {
                    if (lastGamestate.equals(Gamestate.WAITING)) {
                        game.submitWinningCard(card);
                        setPlayerReady();
                        advanceGamestate();
                    }
                }

                @Override
                public void onSwipeDown() {
                    // do nothing
                }
            });
            if (allowCardSzarSubmit) {
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS, FullSizeCard.SWIPE_UP);
            } else {
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
            }
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

    private void onRoundStartGamestate() {
        showHandCardList();
        setPlayerReady();

        if (currentPlayerIsCardSzar()) {
            game.startNewRound();
            submitGame();
            advanceGamestate();
        }
    }

    private void onSubmitGamestate() {
        changeBlackCardText(game.getCurrentBlackCard().getText());

        if (!currentPlayerIsCardSzar()) {
            allowCardSubmitting = true;
        } else {
            setPlayerReady();
            advanceGamestate();
        }

        showHandCardList();
        // then wait for input -> do nothing here
    }

    private void onWaitingGamestate() {
        showPlayedCardsAllowed = true;
        allowCardSubmitting = false;
        // todo display "waiting for cardszar to choose winning card"

        if (currentPlayerIsCardSzar()) {
            showPlayedCards(true);
        } else {
            showHandCardList();
            setPlayerReady();
        }
    }

    private void onRoundEndGamestate() {
        showPlayedCardsAllowed = false;
        setPlayerReady();
        // todo notify player of winning card (only if player is not cardszar), show updated scores

        if (currentPlayerIsCardSzar()) {
            game.nextCardSzar();
            submitGame();
            advanceGamestate();
        }
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
        game.setGamestate(Gamestate.ROUNDSTART);

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
        if (game == null) {
            quitGame("Something went terribly wrong...");
        }
        if (!game.allPlayersReady()) {
            Handler handler = new Handler();
            handler.postDelayed(this::advanceGamestate, 500);
        } else {
            game.setAllPlayersNotReady();
            switch (game.getGamestate()) {
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
                    if (game.getGamestate() != lastGamestate || (!player.isReady() && !currentPlayerIsCardSzar())) {
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
        player.getHand().remove(card);
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

    private void changeBlackCardText(String text) {
        playedBlackCardText.setText(text);
    }
}
