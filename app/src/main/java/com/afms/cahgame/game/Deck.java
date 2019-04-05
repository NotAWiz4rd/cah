package com.afms.cahgame.game;

public class Deck {
   private String name;
   private Card[] whiteCards;
   private Card[] blackCards;

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
