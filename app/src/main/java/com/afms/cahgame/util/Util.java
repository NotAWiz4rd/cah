package com.afms.cahgame.util;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
    /**
     * Converts a dataDeck to a GameDeck by getting all the card data from the database.
     *
     * @param dataDeck The dataDeck.
     * @return The newly created GameDeck.
     */
    public static Deck convertDataDeckToPlayDeck(com.afms.cahgame.data.Deck dataDeck) {
        Deck gameDeck = new Deck();
        gameDeck.setName(dataDeck.getName());

        List<com.afms.cahgame.game.Card> blackCards = new ArrayList<>();
        List<com.afms.cahgame.game.Card> whiteCards = new ArrayList<>();

        for (int cardId : dataDeck.getCardIds()) {
            Card dataCard = getDataCardFromId(cardId);
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

    /**
     * Gets a DataCard from an id.
     *
     * @param cardId The cardId.
     * @return The corresponding DataCard.
     */
    public static Card getDataCardFromId(int cardId) {
        Optional<Card> cardOptional = Database.getCards().stream().filter(card -> card.getId() == cardId).findFirst();
        return cardOptional.orElse(null);
    }

    /**
     * Gets a DataDeck from a deckName.
     *
     * @param deckName The deckName.
     * @return The corresponding DataDeck.
     */
    public static com.afms.cahgame.data.Deck getDataDeckFromName(String deckName) {
        Optional<com.afms.cahgame.data.Deck> deckOptional = Database.getDecks().stream().filter(deck -> deck.getName().equals(deckName)).findFirst();
        return deckOptional.orElse(null);
    }
}
