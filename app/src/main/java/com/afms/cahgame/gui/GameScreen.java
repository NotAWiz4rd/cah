package com.afms.cahgame.gui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Gamestate;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.game.Player;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreen extends AppCompatActivity {
    private static final String BACKEND_URL_GAMES = "https://api.mlab.com/api/1/databases/cah/collections/games?apiKey=06Yem6JpYP8TSlm48U-Ze0Tb49Gnu0NA";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };

    private Game game;
    private Player player;
    private boolean isHost = false;
    private Lobby lobby;
    private Gamestate gamestate = Gamestate.START;

    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_screen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");
        String playerName = (String) getIntent().getSerializableExtra("name");
        this.isHost = lobby.getHost().getName().equals(playerName);
        player = new Player(playerName);

        if (isHost) {
            Deck deck = (Deck) getIntent().getSerializableExtra("deck");
            int handCardCount = (int) getIntent().getSerializableExtra("handcardcount");
            startGame(deck, handCardCount);
        } else {
            gameStartClient();
        }

    }

    private void gameStartClient() {
        updateLobby(lobby.getId());
        gameStateLoop();
    }

    private void updateLobby(String lobbyId) {
        if (lobby.getGamestate() == gamestate) {
            try {
                lobby = new GetCurrentLobby().execute(lobbyId).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                updatePlayer();
                updateLobby(lobbyId);
            }
        } else {
            gamestate = lobby.getGamestate();
            gameStateLoop();
        }
    }

    private void updatePlayer() {
        Optional<Player> playerOptional = lobby.getPlayers().stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        playerOptional.ifPresent(player1 -> player = player1);
    }

    /**
     * Executes things based on the current gamestate of the lobby - for clients only!
     */
    private void gameStateLoop() {
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
        updateLobby(lobby.getId());
    }

    private void onRoundStartGamestate() {
        // todo get lobby
        // todo show new cards and black card
        // todo put player on ready
    }

    private void onSubmitGamestate() {
        // todo enable user to submit a card from his hand


        // then wait for input -> do nothing here
        // todo put player on ready
    }

    private void onWaitingGamestate() {
        // todo display "waiting for other players"
        // todo put player on ready
    }

    private void onRoundEndGamestate() {
        // todo implement me
        // todo notify player of winning card, show updated scores
        // todo put player on ready
    }

    private void onGamestateError() {
        // todo What should happen here? Display error and quit lobby?
    }

    /**
     * For host only!
     * Initializes lobby and pushes it to server.
     *
     * @param deck          The deck to be used.
     * @param handCardCount Amount of initial handcards.
     */
    private void startGame(Deck deck, int handCardCount) {
        game = new Game(deck, lobby.getPlayers(), handCardCount);
        game.startNewRound();

        lobby.setPlayers(game.getPlayers());
        Gson gson = new Gson();
        String lobbyJson = gson.toJson(lobby);

        new SubmitGameToServer().execute(lobbyJson);
        gameStateLoop();
    }

    /**
     * For clients only!
     *
     * @param card Card to sumbit.
     */
    private void submitCard(Card card) {
        List<Player> lobbyPlayers = lobby.getPlayers();
        card.setOwner(player);
        Player lobbyPlayer = null;
        Optional<Player> lobbyPlayerOptional = lobbyPlayers.stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst();
        if (lobbyPlayerOptional.isPresent()) {
            lobbyPlayer = lobbyPlayerOptional.get();
        }

        // todo test if this changes the lobby's player
        if (lobbyPlayer != null) {
            new SubmitGameToServer().execute(convertToJson(lobby));
        }
    }

    private String convertToJson(Lobby lobby) {
        Gson gson = new Gson();
        return gson.toJson(lobby);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Input lobby as String (?)
     */
    static class SubmitGameToServer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpURLConnection con = null;
            try {
                URL url = new URL(BACKEND_URL_GAMES);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoOutput(true);

                OutputStream outputPost = new BufferedOutputStream(con.getOutputStream());
                outputPost.write(strings[0].getBytes());
                outputPost.flush();
                outputPost.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }
    }

    /**
     * Input lobbyId into doInBackground()
     */
    static class GetCurrentLobby extends AsyncTask<String, Void, Lobby> {
        @Override
        protected Lobby doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            HttpURLConnection con = null;
            try {
                URL url = new URL(BACKEND_URL_GAMES);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            return buildLobbyFromString(content.toString(), strings[0]);
        }

        private Lobby buildLobbyFromString(String lobbyJson, String lobbyId) {
            Gson gson = new Gson();
            Lobby[] lobbies = gson.fromJson(lobbyJson, Lobby[].class);
            Optional<Lobby> lobby = Arrays.stream(lobbies).filter(internalLobby -> internalLobby.getId().equals(lobbyId)).findFirst();
            return lobby.orElse(null);
        }
    }
}
