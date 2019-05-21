package com.afms.cahgame.gui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
    private DatabaseReference blackCardTextReference;
    ValueEventListener gameListener;
    ValueEventListener blackCardListener;

    private SharedPreferences settings;

    private boolean playerIsWaiting = false;

    private ImageButton playerOverview;
    private ImageView blackCardIcon;
    private ImageView whiteCardIcon;
    private ConstraintLayout gameScreenLayout;
    private ConstraintLayout playedBlackCard;
    private ConstraintLayout waitingScreen;
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

    private boolean doneRoundStart = false;
    private boolean doneRoundEnd = false;

    private Gamestate lastGamestate;

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
        waitingScreen = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_waiting_screen, gameScreenLayout, false);
        blackCardIcon = waitingScreen.findViewById(R.id.waiting_screen_blackCard);
        whiteCardIcon = waitingScreen.findViewById(R.id.waiting_screen_whiteCard);

    }

    private void initializeUIEvents() {
        playerOverview.setOnClickListener(event -> {
            scoreBoard = ScoreBoardDialog.create(game);
            scoreBoard.show(getSupportFragmentManager(), "playerOverview");
        });

        playedBlackCard.setOnClickListener(event -> {
            if (showPlayedCardsAllowed) {
                showPlayedCards(currentPlayerIsCardSzar());
            }
        });
        waitingScreen.setOnClickListener(event -> {
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
                            allowCardSubmitting = false;
                            showHandCardList();
                            // todo show waiting screen until next gamestate
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
        playerIsWaiting = true;
        lowerFrameLayout.addView(waitingScreen);
        new Thread(() -> waitingScreenAnimation(whiteCardIcon, blackCardIcon));
    }

    private void waitingScreenAnimation(ImageView whiteCard, ImageView blackCard) {
        while (playerIsWaiting) {
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
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(blackCard, "scaleY", 0.5f).setDuration(700);
                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(whiteCard, "scaleX", 2f).setDuration(700);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(whiteCard, "scaleY", 2f).setDuration(700);
                    ObjectAnimator transInBlack = ObjectAnimator.ofFloat(blackCard, "translationX", 0f).setDuration(700);
                    ObjectAnimator transInWhite = ObjectAnimator.ofFloat(whiteCard, "translationX", 0f).setDuration(700);
                    ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(blackCard, "scaleX", 1f).setDuration(700);
                    ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(blackCard, "scaleY", 1f).setDuration(700);
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
                            waitingScreenAnimation(blackCard, whiteCard);
                        }
                    });
                }
            });
        }
    }

    private void removeWaitingScreen() {
        playerIsWaiting = false;
        lowerFrameLayout.removeView(waitingScreen);
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
                fullCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
            }
            playedCards.add(fullCard);
        }

        return playedCards;
    }

    private void updatePlayer() {
        Optional<Player> playerOptional = game.players.values().stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        playerOptional.ifPresent(player1 -> player = player1);
    }

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
        //removeWaitingScreen();
        doneRoundEnd = false;
        showHandCardList();
        setPlayerReady();

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            if (!doneRoundStart) {
                game.startNewRound();
                submitGame();
                doneRoundStart = true;
            }
            advanceGamestate();
        } else if (!currentPlayerIsCardSzar()) {
            doneRoundStart = true;
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
            //showWaitingScreen();
        }
        showHandCardList();

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            advanceGamestate();
            navigationBarText.setText(R.string.choose_winning_card);
            removeWaitingScreen();
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
        showPlayedCardsAllowed = false;
        navigationBarText.setText(R.string.waiting_for_others);
        setPlayerReady();
        // todo notify player of winning img_card_white (only if player is not cardszar), show updated scores

        if (currentPlayerIsCardSzar() && game.allPlayersReady()) {
            if (!doneRoundEnd) {
                game.nextCardSzar();
                submitGame();
                doneRoundEnd = true;
            }
            advanceGamestate();
        } else if (!currentPlayerIsCardSzar()) {
            doneRoundEnd = true;
        }
    }

    private void onGamestateError() {
        quitGame(getString(R.string.impossibleError));
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

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        super.onPause();

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
                        game = dataSnapshot.getValue(Game.class);
                        game.addPlayer(player);
                        updatePlayer();
                        submitGame();
                    }

                    if (!(game.getPlayers().values().size() >= Game.MIN_PLAYERS)) {
                        // todo show waiting screen
                    }

                    if ((currentPlayerIsCardSzar()
                            || (!game.getGamestate().equals(lastGamestate) && !currentPlayerIsCardSzar())
                            || !game.getPlayer(player.getName()).isReady())
                            && game.getPlayers().values().size() >= Game.MIN_PLAYERS) {
                        lastGamestate = game.getGamestate();
                        updatePlayer();
                        gameStateLoop();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERROR", getString(R.string.getGameFailure), databaseError.toException());
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
        blackCardTextReference.addValueEventListener(blackCardListener);

        // initial game submit by host
        if (game != null && game.getGamestate().equals(Gamestate.ROUNDSTART) && hostName != null
                && hostName.equals(player.getName())) {
            submitGame();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        super.onStop();
        if (currentPlayerIsCardSzar()) {
            gameReference.removeValue();
            Database.removeLobby(lobbyId);
            gameReference.removeEventListener(gameListener);
            blackCardTextReference.removeEventListener(blackCardListener);
            finish();
        }
    }

    private boolean gamestateSameOrNewer(Gamestate newGamestate) {
        return (lastGamestate == null) || ((newGamestate.compareTo(lastGamestate) >= 0) && (newGamestate.compareTo(lastGamestate) != 3))
                || (newGamestate.equals(Gamestate.ROUNDSTART) && lastGamestate.equals(Gamestate.ROUNDEND));
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
        blackCardTextReference.removeEventListener(blackCardListener);

        if (game != null) {
            game.removePlayer(player);
            submitGame();
        }
        Intent intent = new Intent(this, Main.class);
        intent.putExtra("message", message.length() > 0 ? message : getString(R.string.cantFindLobby));
        startActivity(intent);
        finish();
    }

    private void saveInfo() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.lobbyId), lobbyId);

        editor.apply();
    }

    /**
     * Submits a img_card_white.
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

    private void changeBlackCardText(String text) {
        playedBlackCardText.setText(text);
    }
}
