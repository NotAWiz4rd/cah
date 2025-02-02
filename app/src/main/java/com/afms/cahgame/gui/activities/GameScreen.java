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
import com.afms.cahgame.gui.components.ChatBottomSheet;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.MessageDialog;
import com.afms.cahgame.gui.components.ResultListener;
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
import java.util.stream.Collectors;

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
    private ImageButton game_screen_chat;
    private ConstraintLayout gameScreenLayout;
    private LinearLayout playedBlackCard;
    private RelativeLayout waitingScreen;
    private TextView waitingScreenText;
    private TextView playedBlackCardText;
    private FullSizeCard playedWhiteCard;
    private FrameLayout lowerFrameLayout;
    private RelativeLayout completeFrameLayout;
    private TextView navigationBarText;
    private FrameLayout view_game_screen_disable;

    private ConstraintLayout userSelectionLayout;
    private ListView userSelectionListView;
    private CardListAdapter userSelectionListAdapter;
    private List<FullSizeCard> playedWhiteCardList;
    private ScoreBoardDialog scoreBoard;

    private boolean showUpdatedScore;
    private boolean doneRoundStart = false;
    private boolean doneRoundEnd = false;

    private Gamestate lastGamestate;
    private boolean playedCardsAreShown = false;

    private MessageDialog messageDialog;
    private FullSizeCard fullCard;

    private ChatBottomSheet chatBottomSheet;
    private static TextView newMessageIcon;
    public static boolean chatIsOpen = false;

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

                    // wait for more players if there arent enough anymore
                    if (!(game.getPlayers().values().size() >= Game.MIN_PLAYERS)) {
                        showWaitingScreen();
                        return;
                    }

                    // only do the gamestateLoop if bigger things changed
                    if ((currentPlayerIsCardSzar()
                            || (!game.getGamestate().equals(lastGamestate) && !currentPlayerIsCardSzar())
                            || !game.getPlayer(player.getName()).isReady())
                            && game.getPlayers().values().size() >= Game.MIN_PLAYERS) {
                        lastGamestate = game.getGamestate();
                        updatePlayer();
                        gameStateLoop();
                    }
                } else if (tempGame == null) {
                    // quit game if game and lobby dont exist anymore
                    if (Database.getLobby(lobbyId) == null) {
                        game = null;
                        quitGame(getString(R.string.gameDeleted));
                    }
                } else if (!gamestateSameOrNewer(tempGame.getGamestate()) && currentPlayerIsCardSzar()) {
                    submitGame(); // submit game again if someone changed it incorrectly
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(getString(R.string.errorLog), getString(R.string.getGameFailure), databaseError.toException());
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
                Log.w(getString(R.string.errorLog), getString(R.string.failedGetLastSwipe), databaseError.toException());
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
                Log.w(getString(R.string.errorLog), getString(R.string.getBlackCardFailure), databaseError.toException());
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
        playerOverview = findViewById(R.id.game_screen_score_board_button);
        gameScreenLayout = findViewById(R.id.game_screen_layout);
        playedBlackCard = findViewById(R.id.layout_game_screen_playedBlackCard);
        playedBlackCardText = findViewById(R.id.blackCardText);
        lowerFrameLayout = findViewById(R.id.layout_game_screen_lower);
        completeFrameLayout = findViewById(R.id.game_screen_frameLayout);
        navigationBarText = findViewById(R.id.game_screen_bottom_navigation_bar_text_field);
        game_screen_chat = findViewById(R.id.game_screen_chat_button);
        view_game_screen_disable = findViewById(R.id.view_game_screen_disable);
        playedBlackCardText.setText("");

        newMessageIcon = findViewById(R.id.circle_btn_game_screen_chat);
        showNewMessageIcon(false);

        playedWhiteCard = new FullSizeCard(this, new Card(Colour.WHITE, ""));
        userSelectionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_list_card_select, gameScreenLayout, false);
        userSelectionListView = userSelectionLayout.findViewById(R.id.cardSelectList);
        waitingScreen = (RelativeLayout) getLayoutInflater().inflate(R.layout.element_waiting_screen_with_gif, gameScreenLayout, false);
        waitingScreenText = waitingScreen.findViewById(R.id.waiting_screen_is_cszar);
    }

    private void initializeUIEvents() {
        playerOverview.setOnClickListener(event -> {
            if (scoreBoard == null) {
                scoreBoard = ScoreBoardDialog.create(game);
                scoreBoard.setResultListener(new ResultListener() {
                    @Override
                    public void onItemClick(String result) {
                    }

                    @Override
                    public void clearReference() {
                        scoreBoard = null;
                    }
                });
                scoreBoard.show(getSupportFragmentManager(), "playerOverview");
            }
            disableUserInterface();
        });

        game_screen_chat.setOnClickListener(event -> {
            if (chatBottomSheet == null) {
                showNewMessageIcon(false);
                chatIsOpen = true;
                chatBottomSheet = ChatBottomSheet.create(Database.getLobby(lobbyId));
                chatBottomSheet.setResultListener(new ResultListener() {
                    @Override
                    public void onItemClick(String result) {
                        Database.sendMessageInLobby(lobbyId, player.getName(), result);
                    }

                    @Override
                    public void clearReference() {
                        chatBottomSheet = null;
                        chatIsOpen = false;
                        showNewMessageIcon(false);
                    }
                });
                chatBottomSheet.show(getSupportFragmentManager(), "chatGameScreen");
            }
            disableUserInterface();
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

    public static void showNewMessageIcon(boolean show) {
        if (newMessageIcon == null) {
            return;
        }
        newMessageIcon.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void disableUserInterface() {
        playerOverview.setEnabled(false);
        game_screen_chat.setEnabled(false);
        new Handler().postDelayed(() -> {
            playerOverview.setEnabled(true);
            game_screen_chat.setEnabled(true);
        }, 250);
    }


    private void deleteAllViewsFromLowerFrameLayout() {
        lowerFrameLayout.removeAllViews();
        view_game_screen_disable.removeAllViews();
        view_game_screen_disable.setVisibility(View.INVISIBLE);
        view_game_screen_disable.setClickable(false);
    }

    private void showHandCardList() {
        if (player != null) {
            deleteAllViewsFromLowerFrameLayout();
            lowerFrameLayout.addView(userSelectionLayout);
            userSelectionListAdapter = new CardListAdapter(this, new ArrayList<>());
            userSelectionListView.setAdapter(userSelectionListAdapter);

            userSelectionListAdapter.addAll(player.getHand());

            userSelectionListView.setOnItemClickListener((parent, view, position, id) -> {
                Card card = (Card) parent.getItemAtPosition(position);
                if (fullCard == null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    completeFrameLayout.addView(getFullSizeCardInstance(card, position), params);
                }
            });
            if (currentPlayerIsCardSzar()) {
                showWaitingScreen();
                setWaitingTextCardSzar(true);
            }
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
        fullCard = new FullSizeCard(this, card);
        fullCard.addOptionButton(getString(R.string.close), v -> {
            completeFrameLayout.removeView(fullCard);
            fullCard = null;
        });
        fullCard.setDimBackground(true);
        fullCard.setSwipeResultListener(new SwipeResultListener() {
            @Override
            public void onSwipeLeft() {
                int nextPos = (selectedPosition + 1) % userSelectionListView.getCount();
                fullCard = null;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos), params);
            }

            @Override
            public void onSwipeRight() {
                int nextPos = selectedPosition - 1;
                if (nextPos < 0) {
                    nextPos = userSelectionListView.getCount() - 1;
                }
                fullCard = null;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                completeFrameLayout.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos), nextPos), params);
            }

            @Override
            public void onSwipeUp() {
                if (!currentPlayerIsCardSzar()) {
                    if (allowCardSubmitting) {
                        navigationBarText.setText(R.string.waiting_slogan);
                        submitCard(card);
                        allowCardSubmitting = false;
                        showHandCardList();
                        showWaitingScreen();
                    }
                    fullCard = null;
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
        return fullCard;
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
            view_game_screen_disable.addView(waitingScreen);
            view_game_screen_disable.setVisibility(View.VISIBLE);
            view_game_screen_disable.setClickable(true);
            setWaitingTextCardSzar(false);
        }
    }

    private void setWaitingTextCardSzar(boolean cardSzar) {
        if (cardSzar) {
            waitingScreenText.setText(getApplicationContext().getString(R.string.you_are_cardszar));
        } else {
            waitingScreenText.setText(null);
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
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_DISABLE);
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_LEFT);
                    }

                    int nextPos = (playedWhiteCardList.indexOf(fullCard) + 1) % playedWhiteCardList.size();
                    playedWhiteCard = playedWhiteCardList.get(nextPos);
                    playedWhiteCard.setFirstTime(true);
                    lowerFrameLayout.addView(playedWhiteCard);
                }

                @Override
                public void onSwipeRight() {
                    if (currentPlayerIsCardSzar()) {
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_DISABLE);
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_RIGHT);
                    }

                    int nextPos = playedWhiteCardList.indexOf(fullCard) - 1;
                    if (nextPos < 0) {
                        nextPos = playedWhiteCardList.size() - 1;
                    }
                    playedWhiteCard = playedWhiteCardList.get(nextPos);
                    playedWhiteCard.setFirstTime(true);
                    lowerFrameLayout.addView(playedWhiteCard);
                }

                @Override
                public void onSwipeUp() {
                    if (currentPlayerIsCardSzar()) {
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_DISABLE);
                        lastCardSzarSwipeReference.setValue(FullSizeCard.SWIPE_UP);

                        if (game.getGamestate().equals(Gamestate.WAITING)) {
                            game.submitWinningCard(card);
                            setPlayerReady();
                            advanceGamestate();
                        }
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
            navigationBarText.setText(R.string.waiting_slogan);
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
            if (scoreBoard == null) {
                game.setRoundEndPlayedCards(playedWhiteCardList.stream().map(FullSizeCard::getCard).collect(Collectors.toList()));
                scoreBoard = ScoreBoardDialog.create(game, game.getWinningCard().getOwner());
                scoreBoard.setResultListener(new ResultListener() {
                    @Override
                    public void onItemClick(String result) {

                    }

                    @Override
                    public void clearReference() {
                        scoreBoard = null;
                    }
                });
                scoreBoard.show(getSupportFragmentManager(), "playerOverview");
            }
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
        if (messageDialog == null) {
            messageDialog = MessageDialog.create(getString(R.string.message_leave_game), new ArrayList<>(Arrays.asList(
                    getString(R.string.leave), getString(R.string.cancel)
            )));
            messageDialog.setResultListener(new ResultListener() {
                @Override
                public void onItemClick(String result) {
                    if (result.equals(getString(R.string.leave))) {
                        quitGame(getString(R.string.leftLobby));
                    }
                }

                @Override
                public void clearReference() {
                    messageDialog = null;
                }
            });
            messageDialog.show(getSupportFragmentManager(), "gameLeave");
        }
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
