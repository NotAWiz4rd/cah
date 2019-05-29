package com.afms.cahgame.data;

import java.io.Serializable;

public class Card implements Serializable {
    private int id;
    private Colour colour;
    private String text;

    public Card() {
    }

    public Card(String text, Colour colour) {
        this.id = 0;
        this.colour = colour;
        this.text = text;
    }

    public Card(int id, Colour colour, String text) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}