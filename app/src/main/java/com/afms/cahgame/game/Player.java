package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private String name;
    private List<Card> hand = new ArrayList<>();
    private int score;
    private boolean isReady;
    private static List<Player> playerList = new ArrayList<>();

    public Player() {
    }

    public Player(String name, ArrayList<Card> hand) {
        this.name = checkForSameName(name);
        this.hand = hand;
        this.score = 0;
        playerList.add(this);
    }

    public Player(String name) {
        this.name = checkForSameName(name);
        this.hand = new ArrayList<>();
        this.score = 0;
        playerList.add(this);
    }

    private String checkForSameName(String name) {
        int countSames = 1;
        for (Player player : playerList) {
            if (name.equals(player.getName())) {
                countSames++;
                player.setName(player.getName() + (countSames - 1));
                if (countSames > 2) {
                    name = name.substring(0, name.length() - 1);
                }
                name = name + countSames;
            }
        }
        return name;
    }

    @Override
    public String toString() {
        // this should only be used for in-lobby player identification
        return name;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCard(Card card) {
        hand.remove(card);
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

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
