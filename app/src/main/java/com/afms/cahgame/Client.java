package com.afms.cahgame;

public class Client {
    private String clientId;
    private String lobbyId;
    private String gameState;

    public Client(String clientId, String lobbyId, String gameState) {
        this.clientId = clientId;
        this.lobbyId = lobbyId;
        this.gameState = gameState;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }
}
