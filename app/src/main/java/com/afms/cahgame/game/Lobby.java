package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.List;

public class Lobby implements Serializable {
    private String _id;
    private List<String> players;
    private String name;
    private String password;


    public Lobby() {
    }

    public Lobby(String id, List<String> players, String name, String password) {
        this._id = id;
        this.players = players;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
