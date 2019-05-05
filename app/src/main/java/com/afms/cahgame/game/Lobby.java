package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.List;

public class Lobby implements Serializable {
    private String _id;
    private List<Player> players;
    private String name;
    private String password;
    private Gamestate gamestate;

    public Lobby(String id, List<Player> players, String name, String password, Gamestate state) {
        this._id = id;
        this.players = players;
        this.name = name;
        this.password = password;
        this.gamestate = state;
    }

    public boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) return false;
        }
        return true;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
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
