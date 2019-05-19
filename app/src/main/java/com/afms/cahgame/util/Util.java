package com.afms.cahgame.util;

import android.content.SharedPreferences;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
    private static List<String> randomNames = new ArrayList<String>() {{
        add("Giesela");
        add("Hans-Werner");
        add("TwilightSparkle");
        add("FluffyUnicorn");
        add("Ezio Auditore");
        add("Geralt von Rivia");
        add("Gordon Freeman");
        add("Duke Nukem");
        add("Zelda");
        add("Link");
        add("Lara Croft");
        add("Max Payne");
        add("Aiden Pearce");
        add("Nico Bellic");
        add("Solid Snake");
        add("Kratos");
        add("Nathan Drake");
        add("Dante");
        add("Commander Shepard");
        add("Sephiroth");
        add("Mario");
        add("John Marston");
        add("GLaDOS");
        add("Agent 47");
        add("Guybrush Threepwood");
        add("Sora");
        add("Sam Fisher");
        add("Handsome Jack");
    }};

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

    /**
     * Gets a random name from the namelist.
     *
     * @return Random name from the namelist.
     */
    public static String getRandomName(SharedPreferences settings) {
        String newName = randomNames.stream().skip((int) (randomNames.size() * Math.random())).findAny().get();
        saveName(settings, newName);
        return newName;
    }

    /**
     * Saves the given name.
     *
     * @param settings SharedPreferences to save the name in.
     * @param newName  The players name.
     */
    public static void saveName(SharedPreferences settings, String newName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("player", newName);
        editor.apply();
    }
}
