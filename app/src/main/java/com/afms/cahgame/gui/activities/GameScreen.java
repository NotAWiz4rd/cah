package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Gamestate;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.game.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreen extends AppCompatActivity {
    private Game game;
    private Player player;
    private Lobby lobby;

    private DatabaseReference lobbyReference;
    private DatabaseReference gameReference;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Preferences", MODE_PRIVATE);

        setContentView(R.layout.activity_game_screen);
        hideUI();

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");
        String playerName = (String) getIntent().getSerializableExtra("name");
        player = new Player(playerName);
        String hostName = (String) getIntent().getSerializableExtra("host");

        saveInfo();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbyReference = database.getReference(lobby.getId());
        gameReference = database.getReference(lobby.getId() + "-game");

        lobbyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                lobby = dataSnapshot.getValue(Lobby.class);
                updatePlayer();
                gameStateLoop();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });

        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });

        if (hostName != null && hostName.equals(playerName)) {
            Deck deck = (Deck) getIntent().getSerializableExtra("deck");
            int handCardCount = (int) getIntent().getSerializableExtra("handcardcount");
            startGame(deck, handCardCount);
        }
    }

    private void updatePlayer() {
        Optional<Player> playerOptional = lobby.players.stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        playerOptional.ifPresent(player1 -> player = player1);
    }

    /**
     * Executes things based on the current gamestate of the lobby
     */
    private void gameStateLoop() {
        if (lobby == null) {
            try {
                throw new Exception("Something went terribly wrong...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (lobby.getGamestate()) {
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
            submitLobby();
        }
    }

    private void onRoundStartGamestate() {
        if (currentPlayerIsCardSzar()) {
            game.startNewRound();
            this.player.setReady(true);
            submitLobbyPlayer();
            advanceGamestate();
        } else {
            this.player.setReady(true);
            submitLobbyPlayer();
        }
    }

    private void onSubmitGamestate() {
        // todo show new cards and black card

        // todo enable user to submit a card from his hand
        this.player.setReady(true);

        if (!currentPlayerIsCardSzar()) {
            submitCard(player.getHand().remove(0)); // todo for testing only
        }

        // then wait for input -> do nothing here
        // todo start gamestateLoop again after submitting card
    }

    private void onWaitingGamestate() {
        // todo display "waiting for cardszar to choose winning card"
        this.player.setReady(true);
        submitLobbyPlayer();
    }

    private void onRoundEndGamestate() {
        if (currentPlayerIsCardSzar()) {
            game.nextCardSzar();
        }
        // todo notify player of winning card (only if player is not cardszar), show updated scores
        this.player.setReady(true);
        submitLobbyPlayer();
    }

    private void onGamestateError() {
        quitGame("Something went horribly wrong...");
    }

    private boolean currentPlayerIsCardSzar() {
        if (game == null || game.getCardCzar() == null) {
            return false;
        }
        return game.getCardCzar().getName().equals(player.getName());
    }

    /**
     * For host only!
     * Initializes lobby and pushes it to server.
     *
     * @param deck          The deck to be used.
     * @param handCardCount Amount of initial handcards.
     */
    private void startGame(Deck deck, int handCardCount) {
        game = new Game(deck, lobby.players, handCardCount);
        game.startNewRound();

        lobby.players = game.players;
        lobby.setGamestate(Gamestate.START);

        submitLobby();
        gameStateLoop();
    }

    /**
     * Updates lobby, then checks if all players are ready.
     * If players ready: Executes Gamestateloop
     * if not: waits 1000ms, then calls itself again
     */
    private boolean waitForPlayers() {
        if (!lobby.allPlayersReady()) {
            Handler handler = new Handler();
            handler.postDelayed(this::waitForPlayers, 500);
        } else {
            return true;
        }
        return false;
    }

    /**
     * Submits a card.
     *
     * @param card Card to sumbit.
     */
    private void submitCard(Card card) {
        card.setOwner(player);
        game.submitCard(card);
        submitLobbyPlayer();
    }

    private void advanceGamestate() {
        if (!waitForPlayers()) {
            advanceGamestate();
        } else {
            switch (lobby.getGamestate()) {
                case START:
                    lobby.setGamestate(Gamestate.ROUNDSTART);
                    break;
                case ROUNDSTART:
                    lobby.setGamestate(Gamestate.SUBMIT);
                    break;
                case SUBMIT:
                    lobby.setGamestate(Gamestate.WAITING);
                    break;
                case WAITING:
                    lobby.setGamestate(Gamestate.ROUNDEND);
                    break;
                case ROUNDEND:
                    lobby.setGamestate(Gamestate.ROUNDSTART);
                    break;
                default:
                    lobby.setGamestate(Gamestate.ROUNDSTART);
                    break;
            }
            submitLobby();
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

        player = new Player(settings.getString("player", ""));
        if (lobby == null) {
            quitGame("Lobby couldn't be found.");
        } else {
            gameStateLoop();
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
        editor.putString("lobbyId", lobby.getId());

        editor.apply();
    }

    private void submitLobby() {
        lobbyReference.setValue(lobby);
    }

    /**
     * Submits the updated game/player to the server.
     */
    private void submitLobbyPlayer() {
        for (int i = 0; i < lobby.players.size(); i++) {
            if (lobby.players.get(i).getName().equals(player.getName())) {
                lobby.players.set(i, player);
                submitLobby();
                return;
            }
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
