package com.afms.cahgame.game;

import com.afms.cahgame.data.Colour;

import java.io.Serializable;

public class Card extends com.afms.cahgame.data.Card implements Serializable {
    private Player owner;

    public Card() {
    }

    public Card(Colour colour, String text) {
        super(colour, text);
    }

    public Card(com.afms.cahgame.data.Card dataCard) {
        super(dataCard.getColour(), dataCard.getText());
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
