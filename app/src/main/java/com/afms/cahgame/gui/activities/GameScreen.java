package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Gamestate;
import com.afms.cahgame.game.Player;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.MessageDialog;
import com.afms.cahgame.gui.components.ScoreBoardDialog;
import com.afms.cahgame.gui.components.SwipeResultListener;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameScreen extends AppCompatActivity {
    private Game game;
    private Player player;

    private boolean showPlayedCardsAllowed;
    private boolean allowCardSubmitting;

    private String lobbyId;
    private String blackCardText = "";
    private DatabaseReference gameReference;
    private DatabaseReference lastCardSzarSwipeReference;
    private DatabaseReference blackCardTextReference;
    ValueEventListener gameListener;
    ValueEventListener lastCardSzarSwipeListener;
    ValueEventListener blackCardListener;

    private SharedPreferences settings;

    private ImageButton playerOverview;
    private ConstraintLayout gameScreenLayout;
    private LinearLayout playedBlackCard;
    private RelativeLayout waitingScreen;
    private TextView playedBlackCardText;
    private FullSizeCard playedWhiteCard;
    private FrameLayout lowerFrameLayout;
    private FrameLayout completeFrameLayout;
    private TextView navigationBarText;

    private ConstraintLayout userSelectionLayout;
    private ListView userSelectionListView;
    private CardListAdapter userSelectionListAdapter;
    private List<FullSizeCard> playedWhiteCardList;
    private List<FullSizeCard> fullSizeCardList = new ArrayList<>();
    private ScoreBoardDialog scoreBoard;

    private boolean showUpdatedScore;
    private boolean doneRoundStart = false;
    private boolean doneRoundEnd = false;

    private Gamestate lastGamestate;
    private boolean playedCardsAreShown = false;

    //<------ LIFECYCLE EVENTS --------------------------------------------------------------------------------------------------------------->

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Preferences", MODE_PRIVATE);

        setContentView(R.layout.activity_game_screen);
        hideUI();
        initializeUIElements();
        initializeUIEvents();

        lobbyId = (String) getIntent().getSerializableExtra(getString(R.string.lobbyId));
        game = (Game) getIntent().getSerializableExtra("game");

        saveInfo();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        super.onResume();

        String hostName = (String) getIntent().getSerializableExtra("host");
        String playerName = settings.getString("player", Util.getRandomName());
        Util.saveName(settings, playerName);
        if (hostName != null && hostName.equals(playerName)) {
            player = game.getPlayer(hostName);
            lastGamestate = Gamestate.ROUNDSTART;
        } else {
            player = new Player(playerName);
        }

        lobbyId = settings.getString(getString(R.string.lobbyId), "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameReference = database.getReference(lobbyId + "-game");
        lastCardSzarSwipeReference = database.getReference(lobbyId + "-game/lastCardSzarSwipe");
        blackCardTextReference = database.getReference(lobbyId + "-game/currentBlackCard/text");

        gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Game tempGame = dataSnapshot.getValue(Game.class);

                if (tempGame != null && gamestateSameOrNewer(tempGame.getGamestate())) {
                    // only get changes if this player wasnt the last committer
                    if (tempGame.getLastCommitter().equals(player.getName())) {
                        return;
                    }
                    game = tempGame;

                    // add player if it doesnt exist in game
                    if (!game.containsPlayerWithName(player.getName())) {
                        player = new Player(playerName);
                        game.addPlayer(player);
                        navigationBarText.setText(R.string.label_nothost);
                        updatePlayer();
                        submitGame();
                    }

                    if (!(game.getPlayers().values().size() >= Game.MIN_PLAYERS)) {
                        showWaitingScreen();
                    }

                    if ((currentPlayerIsCardSzar()
                            || (!game.getGamestate().equals(lastGamestate) && !currentPlayerIsCardSzar())
                            || !game.getPlayer(player.getName()).isReady())
                            && game.getPlayers().values().size() >= Game.MIN_PLAYERS) {
                        lastGamestate = game.getGamestate();
                        updatePlayer();
                        gameStateLoop();
                    }
                } else if (tempGame == null) {
                    if (Database.getLobby(lobbyId) == null) {
                        game = null;
                        quitGame("The game you were playing was deleted.");
                    }
                } else if (!gamestateSameOrNewer(tempGame.getGamestate()) && currentPlayerIsCardSzar()) {
                    submitGame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERROR", getString(R.string.getGameFailure), databaseError.toException());
            }
        };

        lastCardSzarSwipeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    return;
                }
                long lastSwipe = (long) dataSnapshot.getValue();
                if (lastSwipe != 0 && !currentPlayerIsCardSzar()) {
                    playedWhiteCard.doSwipe(Math.toIntExact(lastSwipe));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERROR", getString(R.string.failedGetLastSwipe), databaseError.toException());
            }
        };

        blackCardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blackCardText = dataSnapshot.getValue(String.class);
                if (blackCardText != null) {
                    changeBlackCardText(blackCardText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERROR", getString(R.string.getBlackCardFailure), databaseError.toException());
            }
        };

        gameReference.addValueEventListener(gameListener);
        lastCardSzarSwipeReference.addValueEventListener(lastCardSzarSwipeListener);
        blackCardTextReference.addValueEventListener(blackCardListener);

        // initial game submit by host
        if (game != null && game.getGamestate().equals(Gamestate.ROUNDSTART) && hostName != null
                && hostName.equals(player.getName())) {
            submitGame();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        super.onPause();

        saveInfo();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        super.onStop();
        gameReference.removeEventListener(gameListener);
        lastCardSzarSwipeReference.removeEventListener(lastCardSzarSwipeListener);
        blackCardTextReference.removeEventListener(blackCardListener);
        if (currentPlayerIsCardSzar()) {
            Database.removeLobby(lobbyId);
            finish();
        }
    }

    //<------ GUI LOGIC --------------------------------------------------------------------------------------------------------------->

    private void initializeUIElements() {
        playerOverview = findViewById(R.id.player_overview);
        gameScreenLayout = findViewById(R.id.game_screen_layout);
        playedBlackCard = findViewById(R.id.layout_game_screen_playedBlackCard);
        playedBlackCardText = findViewById(R.id.blackCardText);
        lowerFrameLayout = findViewById(R.id.layout_game_screen_lower);
        completeFrameLayout = findViewById(R.id.game_screen_frameLayout);
        navigationBarText = findViewById(R.id.bottom_navigation_bar_text_field);

        playedBlackCardText.setText("");

        playedWhiteCard = new FullSizeCard(this, new Card(Colour.WHITE, "test"));
        userSelectionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_list_card_select, gameScreenLayout, false);
        userSelectionListView = userSelectionLayout.findViewById(R.id.cardSelectList);
        waitingScreen = (RelativeLayout) getLayoutInflater().inflate(R.layout.element_waiting_screen_with_gif, gameScreenLayout, false);
    }

    private void initializeUIEvents() {
        playerOverview.setOnClickListener(event -> {
            scoreBoard = ScoreBoardDialog.create(game);
            scoreBoard.show(getSupportFragmentManager(), "playerOverview");
        });

        playedBlackCard.setOnClickListener(event -> {
            showPlayedCards(currentPlayerIsCardSzar());
        });
        waitingScreen.setOnClickListener(event -> {
        });
        lowerFrameLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
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
        if (playedCardsAreShown || !showPlayedCardsAllowed) {
            return;
        }
        playedCardsAreShown = true;
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
                            navigationBarText.setText(R.string.waiting_for_others);
                            submitCard(card);
                            allowCardSubmitting = false;
                            showHandCardList();
                            showWaitingScreen();
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

    private void showWaitingScreen() {
        if (!waitingScreen.isAttachedToWindow()) {
            lowerFrameLayout.addView(waitingScreen);
        }
    }

    private List<FullSizeCard> getPlayedCards(boolean allowCardSzarSubmit) {
        List<FullSizeCard> playedCards = new ArrayList<>();

        for (Card card : game.getPlayedCards()) {
            FullSizeCard fullCard = new FullSizeCard(this, card);
            fullCard.setSwipeResultListener(new SwipeResultListener() {
                @Override
                public void onSwipeLeft() {
                    if (currentPlayerIsCardSzar()) {
                        lastCardSzarSwipeReference.setValue(0);
                        lastCardSzarSwipeReference.setValue(4);
                    }

                    int nextPos = (playedWhiteCardList.indexOf(fullCard) + 1) % playedWhiteCardList.size();
                    playedWhiteCard = playedWhiteCardList.get(nextPos);
                    lowerFrameLayout.addView(playedWhiteCard);
                }

                @Override
                public void onSwipeRight() {
                    if (currentPlayerIsCardSzar()) {
                        lastCardSzarSwipeReference.setValue(0);
                        lastCardSzarSwipeReference.setValue(5);
                    }

                    int nextPos = playedWhiteCardList.indexOf(fullCard) - 1;
                    if (nextPos < 0) {
                        nextPos = playedWhiteCardList.size() - 1;
                    }
                    playedWhiteCard = playedWhiteCardList.get(nextPos);
                    lowerFrameLayout.addView(playedWhiteCard);
                }

                @Override
                public void onSwipeUp() {
                    if (game.getGamestate().equals(Gamestate.WAITING)) {
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
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_DISABLE);
            }
            playedCards.add(fullCard);
        }

        return playedCards;
    }

    private void changeBlackCardText(String text) {
        playedBlackCardText.setText(text);
    }

    //<------ GAME LOGIC --------------------------------------------------------------------------------------------------------------->

    /**
     * Executes things based on the current gamestate of the lobby
     */
    private void gameStateLoop() {
        if (currentPlayerIsCardSzar() && game == null) {
            quitGame(getString(R.string.impossibleError));
        } else if (game == null) {
            return;
        }
        switch (game.getGamestate()) {
            case ROUNDSTART:
                showUpdatedScore = true;
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
        doneRoundEnd = false;
        showHandCardList();
        setPlayerReady();

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            if (!doneRoundStart) {
                game.startNewRound();
                submitGame();
                blackCardTextReference.setValue(game.getCurrentBlackCard().getText());
                doneRoundStart = true;
            }
            advanceGamestate();
        } else if (!currentPlayerIsCardSzar()) {
            doneRoundStart = true;
            navigationBarText.setText(R.string.label_nothost);
        } else if (currentPlayerIsCardSzar()) {
            navigationBarText.setText(R.string.waiting_slogan);
        }
    }

    private void onSubmitGamestate() {
        doneRoundStart = false;
        if (!currentPlayerIsCardSzar()) {
            allowCardSubmitting = true;
            navigationBarText.setText(R.string.play_card);
        } else {
            setPlayerReady();
            navigationBarText.setText(R.string.waiting_for_others);
            showWaitingScreen();
        }
        showHandCardList();

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            advanceGamestate();
            navigationBarText.setText(R.string.choose_winning_card);
        }
        // then wait for input
    }

    private void onWaitingGamestate() {
        showPlayedCardsAllowed = true;
        allowCardSubmitting = false;
        showPlayedCards(currentPlayerIsCardSzar());

        if (!currentPlayerIsCardSzar()) {
            navigationBarText.setText(R.string.wait_for_winning_card);
            setPlayerReady();
        }
    }

    private void onRoundEndGamestate() {
        if (showUpdatedScore) {
            scoreBoard = ScoreBoardDialog.create(game, game.getWinningCard().getOwner());
            scoreBoard.show(getSupportFragmentManager(), "playerOverview");
            showUpdatedScore = false;
        }
        playedCardsAreShown = false;
        showPlayedCardsAllowed = false;
        setPlayerReady();
        navigationBarText.setText(R.string.waiting_slogan);

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            if (!doneRoundEnd) {
                game.nextCardSzar();
                submitGame();
                doneRoundEnd = true;
            }
            advanceGamestate();
        } else if (!currentPlayerIsCardSzar()) {
            doneRoundEnd = true;
            navigationBarText.setText(R.string.label_nothost);
        }
    }

    private void onGamestateError() {
        quitGame(getString(R.string.impossibleError));
    }

    /**
     * Advances Gamestate if all players in lobby are ready, submits lobby after advancing.
     */
    private void advanceGamestate() {
        if (game == null) {
            quitGame(getString(R.string.impossibleError));
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
            lastGamestate = game.getGamestate();
            submitGame();
        }
    }

    //<------ UTILITY/DATABASE --------------------------------------------------------------------------------------------------------------->

    private void saveInfo() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.lobbyId), lobbyId);

        editor.apply();
    }

    /**
     * Submits a card.
     *
     * @param card Card to sumbit.
     */
    private void submitCard(Card card) {
        player.removeCard(card);
        card.setOwner(player);
        game.submitCard(card);
        player.setReady(true);
        submitLobbyPlayer();
    }

    private void submitGame() {
        game.setLastCommitter(player.getName());
        gameReference.setValue(game);
    }

    /**
     * Submits the updated game/player to the server.
     */
    private void submitLobbyPlayer() {
        game.players.put(player.getName(), player);
        submitGame();
    }

    @Override
    public void onBackPressed() {
        MessageDialog messageDialog = MessageDialog.create(getString(R.string.message_leave_game), new ArrayList<>(Arrays.asList(
                "Leave", "Cancel"
        )));
        messageDialog.setResultListener(result -> {
            if (result.equals("Leave")) {
                quitGame(getString(R.string.leftLobby));
            }
        });
        messageDialog.show(getSupportFragmentManager(), "gameLeave");
    }

    private void quitGame(String message) {
        gameReference.removeEventListener(gameListener);
        lastCardSzarSwipeReference.removeEventListener(lastCardSzarSwipeListener);
        blackCardTextReference.removeEventListener(blackCardListener);

        if (game != null) {
            game.removePlayer(player);
            submitGame();
            Database.removePlayerFromLobby(lobbyId, player.getName());
        }

        Intent intent = new Intent(this, Main.class);
        intent.putExtra("message", message.length() > 0 ? message : getString(R.string.cantFindLobby));
        startActivity(intent);
        finish();
    }

    private boolean currentPlayerIsCardSzar() {
        if (game == null || game.getCardCzar() == null) {
            return false;
        }
        return game.getCardCzar().equals(player.getName());
    }

    private void setPlayerReady() {
        if (!game.getPlayer(player.getName()).isReady()) {
            this.player.setReady(true);
            submitLobbyPlayer();
        }
    }

    private boolean gamestateSameOrNewer(Gamestate newGamestate) {
        return (lastGamestate == null) || ((newGamestate.compareTo(lastGamestate) >= 0) && (newGamestate.compareTo(lastGamestate) != 3))
                || (newGamestate.equals(Gamestate.ROUNDSTART) && lastGamestate.equals(Gamestate.ROUNDEND));
    }

    private void updatePlayer() {
        Optional<Player> playerOptional = game.players.values().stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        playerOptional.ifPresent(player1 -> player = player1);
    }
}
