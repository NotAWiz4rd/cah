package com.afms.cahgame.game;


import com.afms.cahgame.exceptions.MissingOwnerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    private Deck deck;
    private List<Player> players;
    private int handCardCount;

    private List<Card> blackCardsPile;
    private List<Card> discardPile;
    private List<Card> newCardsPile;
    private Player cardCzar;
    private Card currentBlackCard;
    private List<Card> playedCards;

    public Game(Deck deck, List<Player> players, int handCardCount) {
        this.deck = deck;
        this.players = removeDuplicates(players);
        this.cardCzar = players.get(0);
        this.discardPile = new ArrayList<>();
        this.newCardsPile = new ArrayList<>();
        newCardsPile.addAll(Arrays.asList(deck.getWhiteCards()));
        this.blackCardsPile = new ArrayList<>();
        blackCardsPile.addAll(Arrays.asList(deck.getBlackCards()));
        this.playedCards = new ArrayList<>();
        this.handCardCount = handCardCount;

        drawInitialCards();
    }

    private List<Player> removeDuplicates(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.size(); j++) {
                if (i != j && players.get(i).getName().equals(players.get(j).getName())) {
                    renameDuplicateNames(players, players.get(i).getName());
                }
            }
        }
        return players;
    }

    private void renameDuplicateNames(List<Player> players, String name) {
        int count = 1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                players.get(i).setName(name + "(" + count + ")");
            }
        }
    }

    public void startNewRound() {
        discardPile.addAll(playedCards);
        playedCards = new ArrayList<>();
        currentBlackCard = blackCardsPile.remove(blackCardsPile.size() - 1);
    }

    public void submitCard(Card card) {
        if (card.getOwner() == null) {
            throw new MissingOwnerException();
        }

        playedCards.add(card);
    }

    public void submitWinningCard(Card card) {
        card.getOwner().setScore(card.getOwner().getScore() + 1);
    }

    private void drawCards() {
        for (Player player : players) {
            if (!player.equals(cardCzar)) {
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    private void drawInitialCards() {
        for (int i = 0; i < handCardCount; i++) {
            for (Player player : players) {
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    public Player nextCardSzar() {
        int cardCzarIndex = this.players.indexOf(cardCzar);

        if (cardCzarIndex + 1 < players.size() - 1) {
            cardCzar = players.get(cardCzarIndex + 1);
        } else {
            cardCzar = players.get(0);
        }
        return cardCzar;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getHandCardCount() {
        return handCardCount;
    }

    public void setHandCardCount(int handCardCount) {
        this.handCardCount = handCardCount;
    }

    public List<Card> getBlackCardsPile() {
        return blackCardsPile;
    }

    public void setBlackCardsPile(List<Card> blackCardsPile) {
        this.blackCardsPile = blackCardsPile;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(List<Card> discardPile) {
        this.discardPile = discardPile;
    }

    public List<Card> getNewCardsPile() {
        return newCardsPile;
    }

    public void setNewCardsPile(List<Card> newCardsPile) {
        this.newCardsPile = newCardsPile;
    }

    public Player getCardCzar() {
        return cardCzar;
    }

    public void setCardCzar(Player cardCzar) {
        this.cardCzar = cardCzar;
    }

    public Card getCurrentBlackCard() {
        return currentBlackCard;
    }

    public void setCurrentBlackCard(Card currentBlackCard) {
        this.currentBlackCard = currentBlackCard;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<Card> playedCards) {
        this.playedCards = playedCards;
    }
}
