package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.List;

public class Lobby implements Serializable {
    private String id;
    private Player host;
    private List<Player> players;
    private String name;
    private String password;
    private Gamestate gamestate;

    public Lobby(String id, Player host, List<Player> players, String name, String password, Gamestate state) {
        this.id = id;
        this.host = host;
        this.players = players;
        this.name = name;
        this.password = password;
        this.gamestate = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
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

    public Gamestate getGamestate() {
        return gamestate;
    }

    public void setGamestate(Gamestate gamestate) {
        this.gamestate = gamestate;
    }
}
