package com.afms.cahgame.util;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
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

    public static Card getDataCardFromId(int cardId) {
        Optional<Card> cardOptional = Database.getCards().stream().filter(card -> card.getId() == cardId).findFirst();
        return cardOptional.orElse(null);
    }

    public static com.afms.cahgame.data.Deck getDataDeckFromName(String deckName, List<com.afms.cahgame.data.Deck> decks) {
        Optional<com.afms.cahgame.data.Deck> deckOptional = decks.stream().filter(deck -> deck.getName().equals(deckName)).findFirst();
        return deckOptional.orElse(null);
    }
}
