package com.afms.cahgame.game;


import android.arch.lifecycle.ViewModel;

import java.io.Serializable;

public class Card extends ViewModel implements Serializable {
    private Colour colour;
    private String text;
    private Player owner;

    public Card(Colour colour, String text) {
        this.colour = colour;
        this.text = text;
    }

    @Override
    public String toString() {
        if (owner != null) {
            return "{\"text\": \"" + text + "\", \"color\":\"" + colour.toString() + "\"," +
                    "\"owner\": \"" + owner.toString() + "\"}";
        }
        return "{\"text\": \"" + text + "\", \"color\":\"" + colour.toString() + "\"}";
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
