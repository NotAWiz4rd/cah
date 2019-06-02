package com.afms.cahgame.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.data.Deck;
import com.afms.cahgame.data.Message;
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
    private static Map<String, Deck> decks = new HashMap<>();
    private static ArrayList<Card> cards = new ArrayList<>();
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
                GenericTypeIndicator<Map<String, Deck>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Deck>>() {
                };
                decks = dataSnapshot.getValue(genericTypeIndicator);
                if (decks == null) {
                    decks = new HashMap<>();
                } else if (decks.size() < 3) {
                    Util.createStandardDeck();
                    Util.createGerman1();
                    Util.createGerman2();
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

    /**
     * Adds a message to the current list of lobby messages.
     *
     * @param lobbyId       The LobbyId.
     * @param playername    The name of the writing player.
     * @param messageString The message (important!).
     */
    public static void sendMessageInLobby(String lobbyId, String playername, String messageString) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            Message message = new Message(lobby.getMessages().size(), playername, messageString);
            lobby.addMessage(message);
            lobbies.put(lobbyId, lobby);
            lobbiesReference.setValue(lobbies);
        }
    }

    //............................Cards and Decks..................................................

    /**
     * Adds the specified card to the deck.
     *
     * @param cardId   CardId.
     * @param deckName Name of the deck.
     */
    public static void addCardToDeck(Integer cardId, String deckName) {
        Deck deck = decks.get(deckName);
        if (deck != null) {
            deck.addCard(cardId);
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
    public static int createNewCard(String text, Colour colour) {
        int id = cards.size() == 0 ? 0 : cards.get(cards.size() - 1).getId() + 1;
        Card card = new Card(id, colour, text);
        cards.add(card);
        cardsReference.setValue(cards);
        return id;
    }

    /**
     * Adds a list of cards to the database, giving them proper IDs first.
     * This is done without checking whether cards like these already exist in the database.
     *
     * @param newCards A list of cards without proper IDs.
     */
    public static ArrayList<Integer> addNewCards(List<Card> newCards) {
        int id = cards.size() == 0 ? 0 : cards.get(cards.size() - 1).getId() + 1;
        ArrayList<Integer> ids = new ArrayList<>();
        for (Card card : newCards) {
            card.setId(id);
            ids.add(id);
            id++;
        }

        cards.addAll(newCards);
        cardsReference.setValue(cards);
        return ids;
    }

    /**
     * Removes the given card from the given deck.
     *
     * @param deckName Name of the deck.
     * @param cardId   CardId of the card which is to be removed.
     */
    public static void removeCardFromDeck(String deckName, Integer cardId) {
        Deck deck = decks.get(deckName);
        if (deck != null) {
            deck.removeCard(cardId);
            decksReference.setValue(decks);
        }
    }

    /**
     * Removes the given deck.
     *
     * @param deckName The name of the deck.
     */
    public static void removeDeck(String deckName) {
        decks.remove(deckName);
        decksReference.setValue(decks);
    }

    /**
     * Adds a new deck to the list of decks.
     *
     * @param deckName The name of the new deck.
     */
    public static void addDeck(String deckName) {
        decks.put(deckName, new Deck(deckName));
        decksReference.setValue(decks);
    }

    /**
     * Adds a new deck to the list of decks.
     *
     * @param deck The new deck.
     */
    public static void addDeck(Deck deck) {
        decks.put(deck.getName(), deck);
        decksReference.setValue(decks);
    }

    /**
     * Gets the gameDeck from the deckName.
     *
     * @param deckname Deckname.
     * @return GameDeck of the given deckname.
     */
    public static com.afms.cahgame.game.Deck getDeck(String deckname) {
        return Util.convertDataDeckToPlayDeck(decks.get(deckname));
    }

    //....................................Getters and Setters......................................

    /**
     * List of all decks.
     *
     * @return Current list of all decks.
     */
    public static Map<String, Deck> getDecks() {
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
    public static ArrayList<Card> getCards() {
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
    public static void setDecks(Map<String, Deck> decks) {
        Database.decks = decks;
        decksReference.setValue(Database.decks);
    }

    /**
     * Setter card.
     *
     * @param cards Cards.
     */
    public static void setCards(ArrayList<Card> cards) {
        Database.cards = cards;
        cardsReference.setValue(Database.cards);
    }
}
