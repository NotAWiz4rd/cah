package com.afms.cahgame.game;


import com.afms.cahgame.exceptions.MissingOwnerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Game implements Serializable {
    public static final int MIN_PLAYERS = 3;

    public Map<String, Player> players;
    private Deck deck;
    private int handCardCount;

    private int lastCardSzarSwipe = 0;

    private Gamestate gamestate;

    private List<Card> blackCardsPile;
    private List<Card> discardPile;
    private List<Card> newCardsPile;
    private String cardCzar = "";
    private Card currentBlackCard = new Card();
    private Card winningCard;
    private List<Card> playedCards;

    private String lastCommitter = "";

    public Game() {
        this.players = new HashMap<>();
        this.discardPile = new ArrayList<>();
        this.newCardsPile = new ArrayList<>();
        this.blackCardsPile = new ArrayList<>();
        this.playedCards = new ArrayList<>();
    }

    public Game(Deck deck, List<String> playerNames, int handCardCount) {
        this.gamestate = Gamestate.ROUNDSTART;
        this.deck = deck;
        createPlayers(playerNames);
        nextCardSzar();
        this.discardPile = new ArrayList<>();
        this.newCardsPile = new ArrayList<>();
        newCardsPile.addAll(deck.getWhiteCards());
        this.blackCardsPile = new ArrayList<>();
        blackCardsPile.addAll(deck.getBlackCards());
        this.playedCards = new ArrayList<>();
        this.handCardCount = handCardCount;

        shuffleCards();

        drawInitialCards();
    }

    private void createPlayers(List<String> playerNames) {
        this.players = new HashMap<>();
        for (String playerName : playerNames) {
            this.players.put(playerName, new Player(playerName));
        }
    }

    public void startNewRound() {
        discardPile.addAll(playedCards);
        playedCards = new ArrayList<>();

        if (blackCardsPile != null && !(blackCardsPile.size() == 0)) {
            currentBlackCard = blackCardsPile.remove(blackCardsPile.size() - 1);
        } else if (blackCardsPile != null) {
            blackCardsPile = new ArrayList<>();
            blackCardsPile.addAll(deck.getBlackCards());
            currentBlackCard = blackCardsPile.remove(blackCardsPile.size() - 1);
        }

        drawCards();
    }

    public void setAllPlayersNotReady() {
        for (Player player : players.values()) {
            player.setReady(false);
            players.put(player.getName(), player);
        }
    }

    public boolean allPlayersReady() {
        for (Player player : players.values()) {
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
        Optional<Player> playerOptional = players.values().stream().filter(player -> player.getName().equals(card.getOwner().getName())).findFirst();
        playerOptional.ifPresent(player -> Objects.requireNonNull(players.get(player.getName())).setScore(Objects.requireNonNull(players.get(player.getName())).getScore() + 1));
        winningCard = card;
    }

    /**
     * Draws cards for every player except the cardCzar
     */
    private void drawCards() {
        for (Player player : players.values()) {
            if (!player.getName().equals(cardCzar) && player.getHand().size() < handCardCount) {
                if (newCardsPile.size() == 0) {
                    reshuffleCards();
                }
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    private void shuffleCards() {
        Collections.shuffle(newCardsPile);
        Collections.shuffle(blackCardsPile);
    }

    private void reshuffleCards() {
        Collections.shuffle(discardPile);
        newCardsPile = discardPile;
    }

    private void drawInitialCards() {
        // todo check that deck has enough cards for all players
        for (int i = 0; i < handCardCount; i++) {
            for (Player player : players.values()) {
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            }
        }
    }

    private void drawInitialCards(Player player) {
        // todo check that deck has enough cards for all players
        for (int i = 0; i < handCardCount; i++) {
            if (newCardsPile.size() > 0) {
                player.addCard(newCardsPile.remove(newCardsPile.size() - 1));
            } else {
                reshuffleCards();
            }
        }
    }

    public void nextCardSzar() {
        List<Player> playerList = Arrays.asList(players.values().toArray(new Player[0]));
        if (playerList.indexOf(getCardCzarPlayer()) + 1 <= playerList.size() - 1 && getCardCzarPlayer() != null) {
            int cardCzarIndex = playerList.indexOf(players.get(cardCzar));
            cardCzar = playerList.get(cardCzarIndex + 1).getName();
        } else {
            cardCzar = playerList.get(0).getName();
        }
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
        drawInitialCards(player);
    }

    public void removePlayer(Player player) {
        discardPile.addAll(player.getHand());
        players.remove(player.getName());
    }

    public boolean containsPlayerWithName(String name) {
        return players.containsKey(name);
    }

    /**
     * Gets the current cardCzar, or otherwise the first player.
     *
     * @return If existent the cardCzar, otherwise the first player.
     */
    private Player getCardCzarPlayer() {
        return players.get(cardCzar);
    }

    public String getLastCommitter() {
        return lastCommitter;
    }

    public void setLastCommitter(String lastCommitter) {
        this.lastCommitter = lastCommitter;
    }

    public Player getPlayer(String name) {
        return players.get(name);
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

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Gamestate getGamestate() {
        return gamestate;
    }

    public void setGamestate(Gamestate gamestate) {
        this.gamestate = gamestate;
    }

    public Card getWinningCard() {
        return winningCard;
    }

    public void setWinningCard(Card winningCard) {
        this.winningCard = winningCard;
    }

    public int getLastCardSzarSwipe() {
        return lastCardSzarSwipe;
    }

    public void setLastCardSzarSwipe(int lastCardSzarSwipe) {
        this.lastCardSzarSwipe = lastCardSzarSwipe;
    }
}
