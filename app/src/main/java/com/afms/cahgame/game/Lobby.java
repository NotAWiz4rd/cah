package com.afms.cahgame.game;

import java.util.List;

public class Lobby {
    private String id;
    private Player host;
    private List<Player> players;
    private String name;
    private String password;
    private Game game;

    public Lobby(String id, Player host, List<Player> players, String name, String password, Game game) {
        this.id = id;
        this.host = host;
        this.players = players;
        this.name = name;
        this.password = password;
        this.game = game;
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
