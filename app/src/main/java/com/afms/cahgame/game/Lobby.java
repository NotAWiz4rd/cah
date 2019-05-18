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
    private String password = "";


    public Lobby() {
    }

    public Lobby(String id, String host, String password, int handcardCount, int maxPlayers) {
        this.id = id;
        this.host = host;
        addPlayer(host);
        this.handcardCount = handcardCount;
        this.maxPlayers = maxPlayers;
        this.password = password;
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

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String player) {
        if (players == null) {
            players = new ArrayList<>();
        }

        if (this.players.size() < maxPlayers) {
            this.players.add(player);
        }
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
}
