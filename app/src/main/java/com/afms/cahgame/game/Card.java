package com.afms.cahgame.game;

import com.afms.cahgame.data.Colour;

import java.io.Serializable;

public class Card extends com.afms.cahgame.data.Card implements Serializable {
    private Player owner;

    public Card() {
    }

    public Card(int id, Colour colour, String text) {
        super(id, colour, text);
    }

    public Card(Colour colour, String text) {
        super();
        setColour(colour);
        setText(text);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
