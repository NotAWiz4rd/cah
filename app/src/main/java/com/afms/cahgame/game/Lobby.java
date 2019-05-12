package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lobby implements Serializable {
    private String _id;
    private String host;
    private List<String> players;
    private String password;


    public Lobby() {
    }

    public Lobby(String id, String host, String password) {
        this._id = id;
        this.host = host;
        this.players = new ArrayList<>();
        addPlayer(host);
        this.password = password;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
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
        this.players.add(player);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
