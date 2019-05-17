package com.afms.cahgame.game;


import com.afms.cahgame.exceptions.MissingOwnerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game implements Serializable {
    public List<Player> players;
    private Deck deck;
    private int handCardCount;

    private Gamestate gamestate;

    private List<Card> blackCardsPile;
    private List<Card> discardPile;
    private List<Card> newCardsPile;
    private String cardCzar;
    private Card currentBlackCard;
    private List<Card> playedCards;

    public Game() {
        this.players = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.newCardsPile = new ArrayList<>();
        this.blackCardsPile = new ArrayList<>();
        this.playedCards = new ArrayList<>();
    }

    public Game(Deck deck, List<String> playerNames, int handCardCount) {
        this.gamestate = Gamestate.START;
        this.deck = deck;
        createPlayers(playerNames);
        this.cardCzar = players.get(0).getName();
        this.discardPile = new ArrayList<>();
        this.newCardsPile = new ArrayList<>();
        newCardsPile.addAll(deck.getWhiteCards());
        this.blackCardsPile = new ArrayList<>();
        blackCardsPile.addAll(deck.getBlackCards());
        this.playedCards = new ArrayList<>();
        this.handCardCount = handCardCount;

        drawInitialCards();
    }

    private void createPlayers(List<String> playerNames) {
        this.players = new ArrayList<>();
        for (String playerName : playerNames) {
            this.players.add(new Player(playerName));
        }
        this.players = removeDuplicates(players);
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

        if (blackCardsPile != null && blackCardsPile.size() == 0) {
            blackCardsPile = deck.getBlackCards();
            currentBlackCard = blackCardsPile.remove(blackCardsPile.size() - 1);
        }

        drawCards();
    }

    public boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) return false;
        }
        return true;
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

    /**
     * Draws cards for every player except the cardCzar
     */
    private void drawCards() {
        for (Player player : players) {
            if (!player.getName().equals(cardCzar) && player.getHand().size() < handCardCount) {
                if (newCardsPile.size() == 0) {
                    reshuffleCards();
                }
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    private void reshuffleCards() {
        // todo shuffle discrd pile
        newCardsPile = discardPile;
    }

    private void drawInitialCards() {
        // todo check that deck has enough cards for all players
        for (int i = 0; i < handCardCount; i++) {
            for (Player player : players) {
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    public void nextCardSzar() {
        int cardCzarIndex = players.indexOf(getCardCzarPlayer());

        if (cardCzarIndex + 1 < players.size() - 1) {
            cardCzar = players.get(cardCzarIndex + 1).getName();
        } else {
            cardCzar = players.get(0).getName();
        }
    }

    /**
     * Gets the current cardCzar, or otherwise the first player.
     *
     * @return If existent the cardCzar, otherwise the first player.
     */
    private Player getCardCzarPlayer() {
        Optional<Player> playerOptional = players.stream().filter(player -> player.getName().equals(cardCzar)).findFirst();
        return playerOptional.orElse(players.get(0));
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
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

    public String getCardCzar() {
        return cardCzar;
    }

    public void setCardCzar(String cardCzar) {
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Gamestate getGamestate() {
        return gamestate;
    }

    public void setGamestate(Gamestate gamestate) {
        this.gamestate = gamestate;
    }
}
