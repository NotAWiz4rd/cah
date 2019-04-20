package com.afms.cahgame.game;

import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private int score;
    private boolean isReady;

    public Player(String name, List<Card> hand) {
        this.name = name;
        this.hand = hand;
        this.score = 0;
    }


    @Override
    public String toString() {
        // this should only be used for in-lobby player identification
        return name;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
