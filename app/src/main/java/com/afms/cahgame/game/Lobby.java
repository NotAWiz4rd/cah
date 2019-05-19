package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lobby implements Serializable {
    private String id;
    private String host;
    private List<String> players = new ArrayList<>();
    private int handcardCount;
    private int maxPlayers;
    private String deckName = "";
    private String password = "";
    private boolean gameInProgress = false;


    public Lobby() {
    }

    public Lobby(String id, String host, String password, String deckName, int handcardCount, int maxPlayers) {
        this.id = id;
        this.host = host;
        this.handcardCount = handcardCount;
        this.maxPlayers = maxPlayers;
        this.password = password;
        this.deckName = deckName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String addPlayer(String player) {
        if (players == null) {
            players = new ArrayList<>();
        }

        if (players.size() < maxPlayers) {
            if (!players.contains(player)) {
                players.add(player);
                return player;
            } else {
                String newPlayerName = player + "2";
                players.add(newPlayerName);
                return newPlayerName;
            }
        }
        return "";
    }

    public void removePlayer(String player) {
        this.players.remove(player);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getHandcardCount() {
        return handcardCount;
    }

    public void setHandcardCount(int handcardCount) {
        this.handcardCount = handcardCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}
