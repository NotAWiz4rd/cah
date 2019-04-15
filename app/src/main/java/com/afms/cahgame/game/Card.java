package com.afms.cahgame.game;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class Card extends ViewModel {
    private MutableLiveData<Colour> colour = new MutableLiveData<>();
    private MutableLiveData<String> text = new MutableLiveData<>();
    private MutableLiveData<Player> owner = new MutableLiveData<>();

    public Card(Colour colour, String text) {
        this.colour.setValue(colour);
        this.text.setValue(text);
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
        return colour.getValue();
    }

    public void setColour(Colour colour) {
        this.colour.setValue(colour);
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setValue(text);
    }

    public Player getOwner() {
        return owner.getValue();
    }

    public void setOwner(Player owner) {
        this.owner.setValue(owner);
    }
}
