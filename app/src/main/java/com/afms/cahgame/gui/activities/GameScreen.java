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
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Gamestate;
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

    private Gamestate lastGamestate;

    private String lobbyId;
    private DatabaseReference gameReference;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Preferences", MODE_PRIVATE);

        setContentView(R.layout.activity_game_screen);
        hideUI();

        lobbyId = (String) getIntent().getSerializableExtra("lobbyId");
        game = (Game) getIntent().getSerializableExtra("game");
        String playerName = (String) getIntent().getSerializableExtra("name");
        player = new Player(playerName);

        saveInfo();
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
        updatePlayer();
        gameStateLoop();
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
            quitGame("Game couldn't be found.");
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
