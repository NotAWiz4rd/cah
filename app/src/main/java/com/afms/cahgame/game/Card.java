package com.afms.cahgame.game;

public class Card {
    private Colour colour;
    private String text;

    private Player owner;

    public Card(Colour colour, String text) {
        this.colour = colour;
        this.text = text;
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
