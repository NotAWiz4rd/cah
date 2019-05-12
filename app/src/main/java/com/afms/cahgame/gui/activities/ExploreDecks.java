package com.afms.cahgame.gui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.data.Deck;
import com.afms.cahgame.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreDecks extends AppCompatActivity {
    private List<Deck> decks = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();

    private DatabaseReference decksReference;
    private DatabaseReference cardsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_decks);

        initializeDatabase();
    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        decksReference = database.getReference("decks");
        cardsReference = database.getReference("cards");

        decksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<ArrayList<Deck>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Deck>>() {
                };
                decks = dataSnapshot.getValue(genericTypeIndicator);
                if (decks == null) {
                    decks = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("ERROR", "Failed to get decks from server.", error.toException());
            }
        });

        cardsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // this interesting construct is here to suffice Firebases need for type safety
                GenericTypeIndicator<ArrayList<Card>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Card>>() {
                };
                cards = dataSnapshot.getValue(genericTypeIndicator);
                if (cards == null) {
                    cards = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("ERROR", "Failed to get cards from server.", error.toException());
            }
        });
    }

    private void createDeck(String deckName) {
        Deck deck = new Deck(deckName);
        decks.add(deck);
        decksReference.setValue(decks);
    }

    private void addCardToDeck(String deckName, Integer cardId) {
        Deck deck = Util.getDataDeckFromName(deckName, decks);
        if (deck != null) {
            int deckIndex = decks.indexOf(deck);
            decks.get(deckIndex).addCard(cardId);
            decksReference.setValue(decks);
        }
    }

    private void deleteCard(Integer cardId) {
        Card card = Util.getDataCardFromId(cardId, cards);
        if (card != null) {
            cards.remove(card);
            cardsReference.setValue(cards);
        }
    }

    private void updateCard(Integer cardId, String updatedText, Colour updatedColour) {
        Card card = Util.getDataCardFromId(cardId, cards);
        if (card != null) {
            int cardIndex = cards.indexOf(card);
            cards.get(cardIndex).setText(updatedText);
            cards.get(cardIndex).setColour(updatedColour);
            cardsReference.setValue(cards);
        } else {
            createNewCard(updatedText, updatedColour);
        }
    }

    private void createNewCard(String text, Colour colour) {
        int id = cards.size() == 0 ? 0 : cards.get(cards.size() - 1).getId() + 1;
        Card card = new Card(id, colour, text);
        cards.add(card);
        cardsReference.setValue(cards);
    }

    private void removeCardFromDeck(String deckName, Integer cardId) {
        Deck deck = Util.getDataDeckFromName(deckName, decks);
        if (deck != null) {
            int deckIndex = decks.indexOf(deck);
            decks.get(deckIndex).removeCard(cardId);
            decksReference.setValue(decks);
        }
    }
}
