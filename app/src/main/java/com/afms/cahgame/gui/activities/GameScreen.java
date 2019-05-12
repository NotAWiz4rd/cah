package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreen extends AppCompatActivity {
    private static final String BACKEND_URL_GAMES = "https://api.mlab.com/api/1/databases/cah/collections/games?apiKey=06Yem6JpYP8TSlm48U-Ze0Tb49Gnu0NA";
    private Game game;
    private Player player;
    private Lobby lobby;
    private Gamestate gamestate = Gamestate.START;

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

        if (hostName != null && hostName.equals(playerName)) {
            Deck deck = (Deck) getIntent().getSerializableExtra("deck");
            int handCardCount = (int) getIntent().getSerializableExtra("handcardcount");
            startGame(deck, handCardCount);
        } else {
            gameStartClient();
        }
    }

    private void gameStartClient() {
        updateLobby(lobby.getId());
    }

    /**
     * Gets lobby from server if current gamestate is the same as the lobbyGamestate,
     * updates Player, then waits half a second before calling itself again.
     * Calls gamestateLoop if lobby has a different gamestate than the current one.
     *
     * @param lobbyId Current lobbyId
     */
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
                Handler handler = new Handler();
                handler.postDelayed(() -> updateLobby(lobbyId), 500);
            }
        } else {
            gamestate = lobby.getGamestate();
            gameStateLoop();
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
            updatePlayer();
            onRoundStartGamestate();
        } else {
            updateLobby(lobby.getId());
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

    private boolean currentPlayerIsCardSzar() {
        if (game == null || game.getCardCzar() == null) {
            return false;
        }
        return game.getCardCzar().getName().equals(player.getName());
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
        try {
            lobby = new GetCurrentLobby().execute(lobby.getId()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!lobby.allPlayersReady()) {
                Handler handler = new Handler();
                handler.postDelayed(this::waitForPlayers, 1000);
            } else {
                return true;
            }
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
        try {
            lobby = new GetCurrentLobby().execute(settings.getString("lobbyId", "")).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lobby == null) {
                // todo throw player into lobby browser with message that lobby couldnt be found
            } else {
                gameStateLoop();
            }
        }
    }

    private void saveInfo() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("player", player.getName());
        editor.putString("lobbyId", lobby.getId());

        editor.apply();
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

    private void submitLobby() {
        new SubmitGameToServer().execute(convertToJson(lobby));
    }

    private String convertToJson(Lobby lobby) {
        Gson gson = new Gson();
        return gson.toJson(lobby);
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

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(strings[0]);
                writer.flush();
                writer.close();

                con.connect();

                con.getResponseMessage();
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
