package com.afms.cahgame.game;

import java.io.Serializable;

public class Deck implements Serializable {
   private String name;
   private Card[] whiteCards;
   private Card[] blackCards;

    public Deck(String name, Card[] whiteCards, Card[] blackCards) {
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

    public Card[] getWhiteCards() {
        return whiteCards;
    }

    public void setWhiteCards(Card[] whiteCards) {
        this.whiteCards = whiteCards;
    }

    public Card[] getBlackCards() {
        return blackCards;
    }

    public void setBlackCards(Card[] blackCards) {
        this.blackCards = blackCards;
    }
}
