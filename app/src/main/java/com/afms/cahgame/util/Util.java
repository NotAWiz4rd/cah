package com.afms.cahgame.util;

import android.util.Log;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Deck;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
    private static DatabaseReference cardsReference;
    private static List<Card> cards = new ArrayList<>();

    public static Deck convertDataDeckToPlayDeck(com.afms.cahgame.data.Deck dataDeck) {
        initializeDatabase();

        Deck gameDeck = new Deck();
        gameDeck.setName(dataDeck.getName());

        List<com.afms.cahgame.game.Card> blackCards = new ArrayList<>();
        List<com.afms.cahgame.game.Card> whiteCards = new ArrayList<>();

        for (int cardId : dataDeck.getCardIds()) {
            Card dataCard = getDataCardFromId(cardId, cards);
            if (dataCard != null) {
                if (dataCard.getColour().equals(Colour.WHITE)) {
                    whiteCards.add(new com.afms.cahgame.game.Card(dataCard.getId(), dataCard.getColour(), dataCard.getText()));
                } else {
                    blackCards.add(new com.afms.cahgame.game.Card(dataCard.getId(), dataCard.getColour(), dataCard.getText()));
                }
            }
        }
        gameDeck.setWhiteCards(whiteCards);
        gameDeck.setBlackCards(blackCards);

        return gameDeck;
    }

    private static void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cardsReference = database.getReference("cards");
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

    public static Card getDataCardFromId(int cardId, List<Card> cards) {
        Optional<Card> cardOptional = cards.stream().filter(card -> card.getId() == cardId).findFirst();
        return cardOptional.orElse(null);
    }

    public static com.afms.cahgame.data.Deck getDataDeckFromName(String deckName, List<com.afms.cahgame.data.Deck> decks) {
        Optional<com.afms.cahgame.data.Deck> deckOptional = decks.stream().filter(deck -> deck.getName().equals(deckName)).findFirst();
        return deckOptional.orElse(null);
    }
}
