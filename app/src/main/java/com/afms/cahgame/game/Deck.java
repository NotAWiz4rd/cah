package com.afms.cahgame.game;

import java.io.Serializable;
import java.util.List;

public class Deck implements Serializable {
    private String name;
    private List<Card> whiteCards;
    private List<Card> blackCards;

    public Deck() {
    }

    public Deck(String name, List<Card> whiteCards, List<Card> blackCards) {
        this.name = name;
        this.whiteCards = whiteCards;
        this.blackCards = blackCards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWhiteCards(List<Card> whiteCards) {
        this.whiteCards = whiteCards;
    }

    public void setBlackCards(List<Card> blackCards) {
        this.blackCards = blackCards;
    }

    public List<Card> getWhiteCards() {
        return whiteCards;
    }

    public List<Card> getBlackCards() {
        return blackCards;
    }
}
