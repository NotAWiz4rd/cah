package com.afms.cahgame.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.data.Deck;
import com.afms.cahgame.game.Lobby;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Database {
    private static List<Deck> decks = new ArrayList<>();
    private static List<Card> cards = new ArrayList<>();
    private static Map<String, Lobby> lobbies = new HashMap<>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference decksReference;
    private static DatabaseReference cardsReference;
    public static DatabaseReference lobbiesReference;

    //.......................Initialization........................................................

    /**
     * Initializes the database connections for lobbies, cards and decks.
     */
    public static void initializeDatabaseConnections() {
        initializeLobbiesDatabaseConnection();
        initializeCardsDatabaseConnection();
        initializeDecksDatabaseConnection();
    }

    private static void initializeDecksDatabaseConnection() {
        decksReference = database.getReference("decks");

        decksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<ArrayList<Deck>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Deck>>() {
                };
                decks = dataSnapshot.getValue(genericTypeIndicator);
                if (decks == null) {
                    decks = new ArrayList<>();
                } else if (decks.size() == 0) {
                    Util.createAllCardsDeck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to get decks from server.", error.toException());
            }
        });
    }

    private static void initializeLobbiesDatabaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lobbiesReference = database.getReference("lobbies");

        lobbiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<Map<String, Lobby>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Lobby>>() {
                };
                lobbies = dataSnapshot.getValue(genericTypeIndicator);
                if (lobbies == null) {
                    lobbies = new HashMap<>();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to get lobbies from server.", error.toException());
            }
        });
    }

    private static void initializeCardsDatabaseConnection() {
        cardsReference = database.getReference("cards");

        cardsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<ArrayList<Card>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Card>>() {
                };
                cards = dataSnapshot.getValue(genericTypeIndicator);
                if (cards == null) {
                    cards = new ArrayList<>();
                } else if (cards.size() < 400) {
                    Util.createStandardCards();
                    Util.createAllCardsDeck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to get cards from server.", error.toException());
            }
        });
    }

    //...............................Lobbies.......................................................

    /**
     * Adds a new lobby.
     *
     * @param lobbyId LobbyId.
     * @param lobby   The lobby itself.
     */
    public static void addLobby(String lobbyId, Lobby lobby) {
        lobbies.put(lobbyId, lobby);
        lobbiesReference.setValue(lobbies);
    }

    /**
     * Gets a Lobby.
     *
     * @param lobbyId The lobbyId.
     * @return The lobby corresponding to the lobbyId.
     */
    public static Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    /**
     * Removes a player from the lobby-object.
     * Also deletes the lobby if the leaving player was the host.
     *
     * @param lobbyId    LobbyId.
     * @param playername Name of the player.
     */
    public static void removePlayerFromLobby(String lobbyId, String playername) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.removePlayer(playername);
            lobbies.put(lobbyId, lobby);
            lobbiesReference.setValue(lobbies);

            if (lobby.getHost().equals(playername)) {
                removeLobby(lobbyId);
            }
        }
    }

    /**
     * Removes a lobby from the list of lobbies.
     *
     * @param lobbyId The lobbyId.
     */
    public static void removeLobby(String lobbyId) {
        lobbies.remove(lobbyId);
        lobbiesReference.setValue(lobbies);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gameReference = database.getReference(lobbyId + "-game");
        gameReference.removeValue();
    }

    /**
     * Joins the given lobby.
     *
     * @param lobbyId    The lobbyId.
     * @param playername The playername.
     * @return playername the player joined with, "" if lobby couldn't be joined.
     */
    public static String joinLobby(String lobbyId, String playername) {
        if (lobbies.get(lobbyId) != null) {
            String playerName = Objects.requireNonNull(lobbies.get(lobbyId)).addPlayer(playername);
            lobbiesReference.setValue(lobbies);
            return playerName;
        }
        return "";
    }

    //............................Cards and Decks..................................................

    /**
     * Adds the specified card to the deck.
     *
     * @param cardId   CardId.
     * @param deckName Name of the deck.
     */
    public static void addCardToDeck(Integer cardId, String deckName) {
        Deck deck = Util.getDataDeckFromName(deckName);
        if (deck != null) {
            int deckIndex = Database.getDecks().indexOf(deck);
            decks.get(deckIndex).addCard(cardId);
            decksReference.setValue(decks);
        }
    }

    /**
     * Updates text of the specified card.
     *
     * @param cardId      CardId.
     * @param updatedText The new text of the card.
     */
    public static void updateCard(Integer cardId, String updatedText) {
        updateCard(cardId, updatedText, null);
    }

    /**
     * Updates colour of the specified card.
     *
     * @param cardId        CardId.
     * @param updatedColour The new colour of the card.
     */
    public static void updateCard(Integer cardId, Colour updatedColour) {
        updateCard(cardId, null, updatedColour);
    }

    /**
     * Updates a card with new values. Adds a new card with the given values if the cardId doesnt
     * exist.
     *
     * @param cardId        CardId.
     * @param updatedText   The new text for the card.
     * @param updatedColour The new colour of the card.
     */
    public static void updateCard(Integer cardId, String updatedText, Colour updatedColour) {
        Card card = Util.getDataCardFromId(cardId);
        if (card != null) {
            int cardIndex = cards.indexOf(card);
            if (updatedText != null) {
                cards.get(cardIndex).setText(updatedText);
            }
            if (updatedColour != null) {
                cards.get(cardIndex).setColour(updatedColour);
            }
            cardsReference.setValue(cards);
        } else {
            if (updatedText != null && updatedColour != null) {
                createNewCard(updatedText, updatedColour);
            }
        }
    }

    /**
     * Deletes a card.
     *
     * @param cardId CardId of the card which is to be deleted.
     */
    public static void deleteCard(Integer cardId) {
        Card card = Util.getDataCardFromId(cardId);
        if (card != null) {
            cards.remove(card);
            cardsReference.setValue(cards);
        }
    }

    /**
     * Creates a new card.
     *
     * @param text   Text of the card.
     * @param colour Colour of the card.
     */
    public static Card createNewCard(String text, Colour colour) {
        boolean cardExists = cards.stream().anyMatch(card -> card.getText().equals(text));
        if (cardExists) {
            return cards.stream().filter(card -> card.getText().equals(text)).findAny().get();
        }
        int id = cards.size() == 0 ? 0 : cards.get(cards.size() - 1).getId() + 1;
        Card card = new Card(id, colour, text);
        cards.add(card);
        cardsReference.setValue(cards);
        return card;
    }

    /**
     * Removes the given card from the given deck.
     *
     * @param deckName Name of the deck.
     * @param cardId   CardId of the card which is to be removed.
     */
    public static void removeCardFromDeck(String deckName, Integer cardId) {
        Deck deck = Util.getDataDeckFromName(deckName);
        if (deck != null) {
            int deckIndex = decks.indexOf(deck);
            decks.get(deckIndex).removeCard(cardId);
            decksReference.setValue(decks);
        }
    }

    /**
     * Removes the given deck.
     *
     * @param deckName The name of the deck.
     */
    public static void removeDeck(String deckName) {
        Deck deck = Util.getDataDeckFromName(deckName);
        if (deck != null) {
            decks.remove(deck);
            decksReference.setValue(decks);
        }
    }

    /**
     * Adds a new deck to the list of decks.
     *
     * @param deckName The name of the new deck.
     */
    public static void addDeck(String deckName) {
        decks.add(new Deck(deckName));
        decksReference.setValue(decks);
    }

    /**
     * Adds a new deck to the list of decks.
     *
     * @param deck The new deck.
     */
    public static void addDeck(Deck deck) {
        decks.add(deck);
        decksReference.setValue(decks);
    }

    /**
     * Gets the gameDeck from the deckName.
     *
     * @param deckname Deckname.
     * @return GameDeck of the given deckname.
     */
    public static com.afms.cahgame.game.Deck getDeck(String deckname) {
        return Util.convertDataDeckToPlayDeck(Util.getDataDeckFromName(deckname));
    }

    //....................................Getters and Setters......................................

    /**
     * List of all decks.
     *
     * @return Current list of all decks.
     */
    public static List<Deck> getDecks() {
        if (decks == null) {
            initializeDecksDatabaseConnection();
        }
        return decks;
    }

    /**
     * List of all cards.
     *
     * @return Current list of all cards.
     */
    public static List<Card> getCards() {
        if (cards == null) {
            initializeCardsDatabaseConnection();
        }
        return cards;
    }

    /**
     * Map of all lobbies.
     *
     * @return Current Map (lobbyname to lobby) of all lobbies
     */
    public static Map<String, Lobby> getLobbies() {
        if (lobbies == null) {
            initializeLobbiesDatabaseConnection();
        }
        return lobbies;
    }

    /**
     * Setter decks.
     *
     * @param decks Decks.
     */
    public static void setDecks(List<Deck> decks) {
        Database.decks = decks;
        decksReference.setValue(Database.decks);
    }

    /**
     * Setter card.
     *
     * @param cards Cards.
     */
    public static void setCards(List<Card> cards) {
        Database.cards = cards;
        cardsReference.setValue(Database.cards);
    }
}
