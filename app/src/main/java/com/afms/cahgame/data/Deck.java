package com.afms.cahgame.data;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String name;
    private List<Integer> cardIds;

    public Deck() {
    }

    public Deck(String name) {
        this.name = name;
        this.cardIds = new ArrayList<>();
    }

    public Deck(String name, List<Integer> cardIds) {
        this.name = name;
        this.cardIds = cardIds;
    }

    public void addCard(int cardId) {
        if (!cardIds.contains(cardId)) {
            cardIds.add(cardId);
        }
    }

    public void removeCard(Integer cardId) {
        cardIds.remove(cardId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getCardIds() {
        return cardIds;
    }

    public void setCardIds(List<Integer> cardIds) {
        this.cardIds = cardIds;
    }
}
