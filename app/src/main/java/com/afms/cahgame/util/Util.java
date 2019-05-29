package com.afms.cahgame.util;

import android.content.SharedPreferences;

import com.afms.cahgame.data.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Deck;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
    public static boolean godMode = false;
    public static String playerName;

    /**
     * Converts a dataDeck to a GameDeck by getting all the card data from the database.
     *
     * @param dataDeck The dataDeck.
     * @return The newly created GameDeck.
     */
    public static Deck convertDataDeckToPlayDeck(com.afms.cahgame.data.Deck dataDeck) {
        Deck gameDeck = new Deck();
        gameDeck.setName(dataDeck.getName());

        if (dataDeck.getCardIds() == null || dataDeck.getCardIds().size() == 0) {
            return gameDeck;
        }

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
        if (!Database.getDecks().isEmpty()) {
            Optional<com.afms.cahgame.data.Deck> deckOptional = Database.getDecks().stream().filter(deck -> deck.getName().equals(deckName)).findFirst();
            return deckOptional.orElse(null);
        }
        return null;
    }

    /**
     * Gets a random name from the namelist.
     *
     * @return Random name from the namelist.
     */
    public static String getRandomName() {
        return Faker.instance().pokemon().name();
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

    /**
     * Enables god-mode.
     *
     * @param godMode Whether godmode should be activated or not.
     */
    public static void setGodMode(boolean godMode) {
        Util.godMode = godMode;
    }


    /**
     * Creates a deck with all available cards.
     */
    public static void createAllCardsDeck() {
        if (getDataDeckFromName("allcardsdeck") != null) {
            return;
        }
        com.afms.cahgame.data.Deck deck = new com.afms.cahgame.data.Deck("allcardsdeck");
        List<Card> allCards = Database.getCards();
        for (int i = 0; i < allCards.size(); i++) {
            deck.addCard(allCards.get(i).getId());
        }
        Database.addDeck(deck);
    }

    /**
     * Creates the normal CaH cards in the database.
     */
    public static void createStandardCards() {
        // White Cards
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("Being on fire.", Colour.WHITE));
        cards.add(new Card("Racism.", Colour.WHITE));
        cards.add(new Card("Old-people smell.", Colour.WHITE));
        cards.add(new Card("A micropenis.", Colour.WHITE));
        cards.add(new Card("Women in yogurt commercials.", Colour.WHITE));
        cards.add(new Card("Classist undertones.", Colour.WHITE));
        cards.add(new Card("Not giving a shit about the Third World", Colour.WHITE));
        cards.add(new Card("Inserting a mason jar into my anus.", Colour.WHITE));
        cards.add(new Card("Court-ordered rehab.", Colour.WHITE));
        cards.add(new Card("A windmill full of corpses.", Colour.WHITE));
        cards.add(new Card("The gays.", Colour.WHITE));
        cards.add(new Card("An oversized lollipop.", Colour.WHITE));
        cards.add(new Card("African children.", Colour.WHITE));
        cards.add(new Card("An asymmetric boob job.", Colour.WHITE));
        cards.add(new Card("Bingeing and purging.", Colour.WHITE));
        cards.add(new Card("The hardworking Mexican.", Colour.WHITE));
        cards.add(new Card("An Oedipus complex.", Colour.WHITE));
        cards.add(new Card("A tiny horse.", Colour.WHITE));
        cards.add(new Card("Boogers.", Colour.WHITE));
        cards.add(new Card("Penis envy.", Colour.WHITE));
        cards.add(new Card("Barack Obama.", Colour.WHITE));
        cards.add(new Card("My humps.", Colour.WHITE));
        cards.add(new Card("Scientology.", Colour.WHITE));
        cards.add(new Card("Skeletor.", Colour.WHITE));
        cards.add(new Card("Darth Vader.", Colour.WHITE));
        cards.add(new Card("Figgy pudding.", Colour.WHITE));
        cards.add(new Card("Advice from a wise, old black man.", Colour.WHITE));
        cards.add(new Card("Elderly Japanese men.", Colour.WHITE));
        cards.add(new Card("Free samples.", Colour.WHITE));
        cards.add(new Card("Estrogen.", Colour.WHITE));
        cards.add(new Card("Sexual tension.", Colour.WHITE));
        cards.add(new Card("Famine.", Colour.WHITE));
        cards.add(new Card("A stray pube.", Colour.WHITE));
        cards.add(new Card("Men.", Colour.WHITE));
        cards.add(new Card("Heartwarming orphans.", Colour.WHITE));
        cards.add(new Card("Chunks of dead hitchhiker.", Colour.WHITE));
        cards.add(new Card("A bag of magic beans.", Colour.WHITE));
        cards.add(new Card("Repressions.", Colour.WHITE));
        cards.add(new Card("My relationship status.", Colour.WHITE));
        cards.add(new Card("Overcompensation.", Colour.WHITE));
        cards.add(new Card("Peeing a little bit.", Colour.WHITE));
        cards.add(new Card("Pooping back and forth. Forever.", Colour.WHITE));
        cards.add(new Card("A ball of earwax, semen and toenail clippings.", Colour.WHITE));
        cards.add(new Card("Testicular torsion.", Colour.WHITE));
        cards.add(new Card("The Devil himself.", Colour.WHITE));
        cards.add(new Card("The World of Warcraft.", Colour.WHITE));
        cards.add(new Card("MechaHitler.", Colour.WHITE));
        cards.add(new Card("Being fabulous.", Colour.WHITE));
        cards.add(new Card("Pictures of boobs.", Colour.WHITE));
        cards.add(new Card("A gentle caress of the inner thigh.", Colour.WHITE));
        cards.add(new Card("The Amish.", Colour.WHITE));
        cards.add(new Card("The rhythms of Africa.", Colour.WHITE));
        cards.add(new Card("Lance Armstrong's missing testicle.", Colour.WHITE));
        cards.add(new Card("Pedophiles.", Colour.WHITE));
        cards.add(new Card("The Pope.", Colour.WHITE));
        cards.add(new Card("Flying sex snakes.", Colour.WHITE));
        cards.add(new Card("Sexy pillow fights.", Colour.WHITE));
        cards.add(new Card("Invading Poiand.", Colour.WHITE));
        cards.add(new Card("Cybernetic enhancements.", Colour.WHITE));
        cards.add(new Card("Civilian casualties.", Colour.WHITE));
        cards.add(new Card("Jobs.", Colour.WHITE));
        cards.add(new Card("The female orgasm.", Colour.WHITE));
        cards.add(new Card("Bitches.", Colour.WHITE));
        cards.add(new Card("The Boy Scouts of America.", Colour.WHITE));
        cards.add(new Card("Auschwitz.", Colour.WHITE));
        cards.add(new Card("Finger painting.", Colour.WHITE));
        cards.add(new Card("The Care Bear Stare.", Colour.WHITE));
        cards.add(new Card("The Jews.", Colour.WHITE));
        cards.add(new Card("Being marginalized.", Colour.WHITE));
        cards.add(new Card("The Blood of Christ.", Colour.WHITE));
        cards.add(new Card("Dead parents.", Colour.WHITE));
        cards.add(new Card("The art of seduction.", Colour.WHITE));
        cards.add(new Card("Dying of dysentery.", Colour.WHITE));
        cards.add(new Card("Magnets.", Colour.WHITE));
        cards.add(new Card("Jewish fraternities.", Colour.WHITE));
        cards.add(new Card("Natalie Portman.", Colour.WHITE));
        cards.add(new Card("Agriculture.", Colour.WHITE));
        cards.add(new Card("Surprise sex!", Colour.WHITE));
        cards.add(new Card("The homosexual agenda.", Colour.WHITE));
        cards.add(new Card("Robert Downey, Jr.", Colour.WHITE));
        cards.add(new Card("The Trail of Tears.", Colour.WHITE));
        cards.add(new Card("Funky fresh rhymes.", Colour.WHITE));
        cards.add(new Card("The light of a billion suns.", Colour.WHITE));
        cards.add(new Card("Amputees.", Colour.WHITE));
        cards.add(new Card("Throwing a virgin into a volcano.", Colour.WHITE));
        cards.add(new Card("Italians.", Colour.WHITE));
        cards.add(new Card("Explosions.", Colour.WHITE));
        cards.add(new Card("A good sniff.", Colour.WHITE));
        cards.add(new Card("Destroying the evidence.", Colour.WHITE));
        cards.add(new Card("Children on leashes.", Colour.WHITE));
        cards.add(new Card("Catapults.", Colour.WHITE));
        cards.add(new Card("One trillion dollars.", Colour.WHITE));
        cards.add(new Card("Friends with benefits.", Colour.WHITE));
        cards.add(new Card("Dying.", Colour.WHITE));
        cards.add(new Card("Silence.", Colour.WHITE));
        cards.add(new Card("Growing a pair.", Colour.WHITE));
        cards.add(new Card("YOU MUST CONSTRUCT ADDITIONAL PYLONS.", Colour.WHITE));
        cards.add(new Card("Justin Bieber.", Colour.WHITE));
        cards.add(new Card("The Holy Bible.", Colour.WHITE));
        cards.add(new Card("Balls.", Colour.WHITE));
        cards.add(new Card("Praying the gay away.", Colour.WHITE));
        cards.add(new Card("Teenage pregnancy.", Colour.WHITE));
        cards.add(new Card("German dungeon porn.", Colour.WHITE));
        cards.add(new Card("The invisible hand.", Colour.WHITE));
        cards.add(new Card("My inner demons.", Colour.WHITE));
        cards.add(new Card("Powerful thighs.", Colour.WHITE));
        cards.add(new Card("Getting naked and watching Nickelodeon.", Colour.WHITE));
        cards.add(new Card("Crippling debt.", Colour.WHITE));
        cards.add(new Card("Kamikaze pilots.", Colour.WHITE));
        cards.add(new Card("Teaching a robot to love.", Colour.WHITE));
        cards.add(new Card("Police brutality.", Colour.WHITE));
        cards.add(new Card("Horse meat.", Colour.WHITE));
        cards.add(new Card("All-you-can-eat shrimp for $4.99", Colour.WHITE));
        cards.add(new Card("Heteronormativity.", Colour.WHITE));
        cards.add(new Card("Michael Jackson.", Colour.WHITE));
        cards.add(new Card("A really cool hat.", Colour.WHITE));
        cards.add(new Card("Copping a feel.", Colour.WHITE));
        cards.add(new Card("Crystal meth.", Colour.WHITE));
        cards.add(new Card("Shapeshifters.", Colour.WHITE));
        cards.add(new Card("Fingering.", Colour.WHITE));
        cards.add(new Card("A disappointing birthday party.", Colour.WHITE));
        cards.add(new Card("The Patriarchy.", Colour.WHITE));
        cards.add(new Card("My soul.", Colour.WHITE));
        cards.add(new Card("A sausage festival.", Colour.WHITE));
        cards.add(new Card("The chronic.", Colour.WHITE));
        cards.add(new Card("Eugenics.", Colour.WHITE));
        cards.add(new Card("Synergistic management solutions.", Colour.WHITE));
        cards.add(new Card("RoboCop.", Colour.WHITE));
        cards.add(new Card("Stephen Hawking talking dirty.", Colour.WHITE));
        cards.add(new Card("A man on the brink of orgasm.", Colour.WHITE));
        cards.add(new Card("Fiery poops.", Colour.WHITE));
        cards.add(new Card("Public ridicule.", Colour.WHITE));
        cards.add(new Card("White-man scalps.", Colour.WHITE));
        cards.add(new Card("The morbidly obese.", Colour.WHITE));
        cards.add(new Card("Object permanence.", Colour.WHITE));
        cards.add(new Card("Lockjaw.", Colour.WHITE));
        cards.add(new Card("Joe Biden.", Colour.WHITE));
        cards.add(new Card("Bio-engineered assault turtles with acid breath.", Colour.WHITE));
        cards.add(new Card("Wet dreams.", Colour.WHITE));
        cards.add(new Card("Hip hop jewels.", Colour.WHITE));
        cards.add(new Card("Firing a rifle into the air while balls deep into a squealing hog.", Colour.WHITE));
        cards.add(new Card("Panda sex.", Colour.WHITE));
        cards.add(new Card("Necrophilia.", Colour.WHITE));
        cards.add(new Card("Grave robbing.", Colour.WHITE));
        cards.add(new Card("A bleached asshole.", Colour.WHITE));
        cards.add(new Card("Muhammad (Praise Be Unto Him).", Colour.WHITE));
        cards.add(new Card("Multiple stab wounds.", Colour.WHITE));
        cards.add(new Card("Daniel Radcliffe's delicious asshole.", Colour.WHITE));
        cards.add(new Card("A monkey smoking a cigar.", Colour.WHITE));
        cards.add(new Card("Smegma.", Colour.WHITE));
        cards.add(new Card("A live studio audience.", Colour.WHITE));
        cards.add(new Card("The violation of our most basic human rights.", Colour.WHITE));
        cards.add(new Card("Unfathomable stupidity.", Colour.WHITE));
        cards.add(new Card("Sunshine and rainbows.", Colour.WHITE));
        cards.add(new Card("Whipping it out.", Colour.WHITE));
        cards.add(new Card("The token minority.", Colour.WHITE));
        cards.add(new Card("The terrorists.", Colour.WHITE));
        cards.add(new Card("A snapping turtle biting the tip of your penis.", Colour.WHITE));
        cards.add(new Card("Vehicular manslaughter.", Colour.WHITE));
        cards.add(new Card("The Great Depression.", Colour.WHITE));
        cards.add(new Card("Emotions.", Colour.WHITE));
        cards.add(new Card("Getting so angry that you pop a boner.", Colour.WHITE));
        cards.add(new Card("Same-sex ice dancing.", Colour.WHITE));
        cards.add(new Card("An M16 assault rifle.", Colour.WHITE));
        cards.add(new Card("Man meat.", Colour.WHITE));
        cards.add(new Card("Incest.", Colour.WHITE));
        cards.add(new Card("A foul mouth.", Colour.WHITE));
        cards.add(new Card("Flightless birds.", Colour.WHITE));
        cards.add(new Card("Doing the right thing.", Colour.WHITE));
        cards.add(new Card("When you fart and a little bit comes out.", Colour.WHITE));
        cards.add(new Card("Being a dick to children.", Colour.WHITE));
        cards.add(new Card("Poopy diapers.", Colour.WHITE));
        cards.add(new Card("Seeing grandma naked.", Colour.WHITE));
        cards.add(new Card("Raptor attacks.", Colour.WHITE));
        cards.add(new Card("Concealing a boner.", Colour.WHITE));
        cards.add(new Card("Full frontal nudity.", Colour.WHITE));
        cards.add(new Card("Nipple blades.", Colour.WHITE));
        cards.add(new Card("A bitch slap.", Colour.WHITE));
        cards.add(new Card("Michelle Obama's arms.", Colour.WHITE));
        cards.add(new Card("Mouth herpes.", Colour.WHITE));
        cards.add(new Card("A robust mongoloid.", Colour.WHITE));
        cards.add(new Card("Mutually-assured destruction.", Colour.WHITE));
        cards.add(new Card("The Rapture.", Colour.WHITE));
        cards.add(new Card("Road head.", Colour.WHITE));
        cards.add(new Card("Stalin.", Colour.WHITE));
        cards.add(new Card("Lactation.", Colour.WHITE));
        cards.add(new Card("The true meaning of Christmas.", Colour.WHITE));
        cards.add(new Card("Self-loathing.", Colour.WHITE));
        cards.add(new Card("A brain tumor.", Colour.WHITE));
        cards.add(new Card("Dead babies.", Colour.WHITE));
        cards.add(new Card("New Age music.", Colour.WHITE));
        cards.add(new Card("A thermonuclear detonation.", Colour.WHITE));
        cards.add(new Card("Geese.", Colour.WHITE));
        cards.add(new Card("Kanye West.", Colour.WHITE));
        cards.add(new Card("God.", Colour.WHITE));
        cards.add(new Card("A spastic nerd.", Colour.WHITE));
        cards.add(new Card("Harry Potter erotica.", Colour.WHITE));
        cards.add(new Card("Kids with ass cancer.", Colour.WHITE));
        cards.add(new Card("Lumberjack fantasies.", Colour.WHITE));
        cards.add(new Card("The American Dream.", Colour.WHITE));
        cards.add(new Card("Puberty.", Colour.WHITE));
        cards.add(new Card("Sweet, sweet vengeance.", Colour.WHITE));
        cards.add(new Card("Winking at old people.", Colour.WHITE));
        cards.add(new Card("The wonders of the Orient.", Colour.WHITE));
        cards.add(new Card("Oompa-Loompas.", Colour.WHITE));
        cards.add(new Card("Authentic Mexican cuisine.", Colour.WHITE));
        cards.add(new Card("Preteens.", Colour.WHITE));
        cards.add(new Card("The Little Engine That Could.", Colour.WHITE));
        cards.add(new Card("A Fleshlight.", Colour.WHITE));
        cards.add(new Card("Erectile dysfunction.", Colour.WHITE));
        cards.add(new Card("Having anuses for eyes.", Colour.WHITE));
        cards.add(new Card("Saxophone solos.", Colour.WHITE));
        cards.add(new Card("Land mines.", Colour.WHITE));
        cards.add(new Card("Running out of semen.", Colour.WHITE));
        cards.add(new Card("Me time.", Colour.WHITE));
        cards.add(new Card("Nickelback.", Colour.WHITE));
        cards.add(new Card("Vigilante justice.", Colour.WHITE));
        cards.add(new Card("The South.", Colour.WHITE));
        cards.add(new Card("Opposable thumbs.", Colour.WHITE));
        cards.add(new Card("Ghosts.", Colour.WHITE));
        cards.add(new Card("Alcoholism.", Colour.WHITE));
        cards.add(new Card("Poorly-timed Holocaust jokes.", Colour.WHITE));
        cards.add(new Card("Inappropriate yodeling.", Colour.WHITE));
        cards.add(new Card("Battlefield amputations.", Colour.WHITE));
        cards.add(new Card("Exactly what you'd expect.", Colour.WHITE));
        cards.add(new Card("A time travel paradox.", Colour.WHITE));
        cards.add(new Card("AXE Body Spray.", Colour.WHITE));
        cards.add(new Card("The pirate's life.", Colour.WHITE));
        cards.add(new Card("Saying 'I love you.'", Colour.WHITE));
        cards.add(new Card("A sassy black woman.", Colour.WHITE));
        cards.add(new Card("Being a motherfucking sorcerer.", Colour.WHITE));
        cards.add(new Card("A mopey zoo lion.", Colour.WHITE));
        cards.add(new Card("A falcon with a cap on its head.", Colour.WHITE));
        cards.add(new Card("Farting and walking away.", Colour.WHITE));
        cards.add(new Card("A mating display.", Colour.WHITE));
        cards.add(new Card("The Chinese gymnastics team.", Colour.WHITE));
        cards.add(new Card("Friction.", Colour.WHITE));
        cards.add(new Card("Asians who aren't good at math.", Colour.WHITE));
        cards.add(new Card("Fear itself.", Colour.WHITE));
        cards.add(new Card("A can of whoop-ass.", Colour.WHITE));
        cards.add(new Card("Licking things to claim them as your own.", Colour.WHITE));
        cards.add(new Card("Vikings.", Colour.WHITE));
        cards.add(new Card("Hot cheese.", Colour.WHITE));
        cards.add(new Card("Nicolas Cage.", Colour.WHITE));
        cards.add(new Card("A defective condom.", Colour.WHITE));
        cards.add(new Card("The inevitable heat death of the universe.", Colour.WHITE));
        cards.add(new Card("Republicans.", Colour.WHITE));
        cards.add(new Card("William Shatner.", Colour.WHITE));
        cards.add(new Card("Tentacle porn.", Colour.WHITE));
        cards.add(new Card("Sperm whales.", Colour.WHITE));
        cards.add(new Card("Lady Gaga.", Colour.WHITE));
        cards.add(new Card("The wrath of Vladimir Putin.", Colour.WHITE));
        cards.add(new Card("Gloryholes.", Colour.WHITE));
        cards.add(new Card("Daddy issues.", Colour.WHITE));
        cards.add(new Card("A mime having a stroke.", Colour.WHITE));
        cards.add(new Card("White people.", Colour.WHITE));
        cards.add(new Card("A lifetime of sadness.", Colour.WHITE));
        cards.add(new Card("Tasteful sideboob.", Colour.WHITE));
        cards.add(new Card("Nazis.", Colour.WHITE));
        cards.add(new Card("A cooler full of organs.", Colour.WHITE));
        cards.add(new Card("Giving 110%.", Colour.WHITE));
        cards.add(new Card("Doin' it in the butt.", Colour.WHITE));
        cards.add(new Card("Holding down a child and farting all over him.", Colour.WHITE));
        cards.add(new Card("A homoerotic volleyball montage.", Colour.WHITE));
        cards.add(new Card("Puppies!", Colour.WHITE));
        cards.add(new Card("Nature male enhancement.", Colour.WHITE));
        cards.add(new Card("Brown people.", Colour.WHITE));
        cards.add(new Card("Dropping a chandelier on your enemies and riding the rope up.", Colour.WHITE));
        cards.add(new Card("Soup that is too hot.", Colour.WHITE));
        cards.add(new Card("Sex with Patrick Stewart.", Colour.WHITE));
        cards.add(new Card("Hormone injections.", Colour.WHITE));
        cards.add(new Card("Pulling out.", Colour.WHITE));
        cards.add(new Card("The Big Bang.", Colour.WHITE));
        cards.add(new Card("Giving birth to the Antichrist.", Colour.WHITE));
        cards.add(new Card("Dark and mysterious forces beyong our control.", Colour.WHITE));
        cards.add(new Card("Count Chocula.", Colour.WHITE));
        cards.add(new Card("The Hamburglar.", Colour.WHITE));
        cards.add(new Card("Not reciprocating oral sex.", Colour.WHITE));
        cards.add(new Card("Hot people.", Colour.WHITE));
        cards.add(new Card("Foreskin.", Colour.WHITE));
        cards.add(new Card("Assless chaps.", Colour.WHITE));
        cards.add(new Card("The miracle of childbirth.", Colour.WHITE));
        cards.add(new Card("Waiting 'til marriage.", Colour.WHITE));
        cards.add(new Card("Two midgets shitting into a bucket.", Colour.WHITE));
        cards.add(new Card("A sad handjob.", Colour.WHITE));
        cards.add(new Card("Cheating in the Special Olympics.", Colour.WHITE));
        cards.add(new Card("Miley Cirus at 55.", Colour.WHITE));
        cards.add(new Card("Our first chimpanzee president.", Colour.WHITE));
        cards.add(new Card("Extremely tight pants.", Colour.WHITE));
        cards.add(new Card("Third base.", Colour.WHITE));
        cards.add(new Card("Waking up half-naked in a Denny's parking lot.", Colour.WHITE));
        cards.add(new Card("White priilege.", Colour.WHITE));
        cards.add(new Card("Hope.", Colour.WHITE));
        cards.add(new Card("Taking off your shirt.", Colour.WHITE));
        cards.add(new Card("Ethnic cleansing.", Colour.WHITE));
        cards.add(new Card("Getting really high.", Colour.WHITE));
        cards.add(new Card("Natural selection.", Colour.WHITE));
        cards.add(new Card("A gassy antelope.", Colour.WHITE));
        cards.add(new Card("My sex life.", Colour.WHITE));
        cards.add(new Card("Pretending to care.", Colour.WHITE));
        cards.add(new Card("My black ass.", Colour.WHITE));
        cards.add(new Card("BATMAN!!!", Colour.WHITE));
        cards.add(new Card("Homeless people.", Colour.WHITE));
        cards.add(new Card("Racially-biased SAT questions.", Colour.WHITE));
        cards.add(new Card("Centaurs.", Colour.WHITE));
        cards.add(new Card("A salty surprise.", Colour.WHITE));
        cards.add(new Card("72 virgins.", Colour.WHITE));
        cards.add(new Card("Pixelated bukkake.", Colour.WHITE));
        cards.add(new Card("Seppuku.", Colour.WHITE));
        cards.add(new Card("Mestrual rage.", Colour.WHITE));
        cards.add(new Card("Sexual peeing.", Colour.WHITE));
        cards.add(new Card("An endless tream of diarrhea.", Colour.WHITE));
        cards.add(new Card("Horrifiyng laser hair removal accidents.", Colour.WHITE));
        cards.add(new Card("A fetus.", Colour.WHITE));
        cards.add(new Card("Riding off into the sunset.", Colour.WHITE));
        cards.add(new Card("Goblins.", Colour.WHITE));
        cards.add(new Card("Eating the last known bison.", Colour.WHITE));
        cards.add(new Card("Shiny objects.", Colour.WHITE));
        cards.add(new Card("Being rich.", Colour.WHITE));
        cards.add(new Card("World peace.", Colour.WHITE));
        cards.add(new Card("Dick fingers.", Colour.WHITE));
        cards.add(new Card("Chainsaws for hands.", Colour.WHITE));
        cards.add(new Card("Penis breath.", Colour.WHITE));
        cards.add(new Card("Laying an egg.", Colour.WHITE));
        cards.add(new Card("My genitals.", Colour.WHITE));
        cards.add(new Card("Grandma.", Colour.WHITE));
        cards.add(new Card("Flesh-eating bacteria.", Colour.WHITE));
        cards.add(new Card("Poor people.", Colour.WHITE));
        cards.add(new Card("50,000 volts straight to the nipples.", Colour.WHITE));
        cards.add(new Card("The Übermensch.", Colour.WHITE));
        cards.add(new Card("Poor life choices.", Colour.WHITE));
        cards.add(new Card("Altar boys.", Colour.WHITE));
        cards.add(new Card("My vagina.", Colour.WHITE));
        cards.add(new Card("Pac-Man uncontrollably guzzling cum.", Colour.WHITE));
        cards.add(new Card("Sniffing glue.", Colour.WHITE));
        cards.add(new Card("The placenta.", Colour.WHITE));
        cards.add(new Card("Spontaneous human combustion.", Colour.WHITE));
        cards.add(new Card("The KKK.", Colour.WHITE));
        cards.add(new Card("The clitoris.", Colour.WHITE));
        cards.add(new Card("Not wearing pants.", Colour.WHITE));
        cards.add(new Card("Consensual sex.", Colour.WHITE));
        cards.add(new Card("Black people.", Colour.WHITE));
        cards.add(new Card("A bucket of fish heads.", Colour.WHITE));
        cards.add(new Card("Passive-agrressive Post-it notes.", Colour.WHITE));
        cards.add(new Card("The heart of a child.", Colour.WHITE));
        cards.add(new Card("Crumbs all over the god damn carpet.", Colour.WHITE));
        cards.add(new Card("Being fat and stupid.", Colour.WHITE));
        cards.add(new Card("Getting married, having a few kids, buying some stuff, retiring to Florida, and dying.", Colour.WHITE));
        cards.add(new Card("Sean Connery.", Colour.WHITE));
        cards.add(new Card("Expecting to burp and vomiting on the floor.", Colour.WHITE));
        cards.add(new Card("Wifely duties.", Colour.WHITE));
        cards.add(new Card("A pyramid of severed heads.", Colour.WHITE));
        cards.add(new Card("Genghis Khan.", Colour.WHITE));
        cards.add(new Card("Crucifixion.", Colour.WHITE));
        cards.add(new Card("Friendly fire.", Colour.WHITE));
        cards.add(new Card("AIDS.", Colour.WHITE));
        cards.add(new Card("8 oz. of sweet Mexican black-tar heroin.", Colour.WHITE));
        cards.add(new Card("Half-assed foreplay.", Colour.WHITE));
        cards.add(new Card("Edible underpants.", Colour.WHITE));
        cards.add(new Card("My collection of high-tech sex toys.", Colour.WHITE));
        cards.add(new Card("The Force.", Colour.WHITE));
        cards.add(new Card("Bees?", Colour.WHITE));
        cards.add(new Card("Some god-damn peace and quiet.", Colour.WHITE));
        cards.add(new Card("Jerking off into a pool of children's tears.", Colour.WHITE));
        cards.add(new Card("A micropig wearing a tiny raincoat and booties.", Colour.WHITE));
        cards.add(new Card("Three dicks at the same time.", Colour.WHITE));
        cards.add(new Card("Masturbation.", Colour.WHITE));
        cards.add(new Card("Tom Cruise.", Colour.WHITE));
        cards.add(new Card("Anal beads.", Colour.WHITE));
        cards.add(new Card("Drinking alone.", Colour.WHITE));
        cards.add(new Card("Cards Against Humankind.", Colour.WHITE));
        cards.add(new Card("Coat hanger abortions.", Colour.WHITE));
        cards.add(new Card("Used panties.", Colour.WHITE));
        cards.add(new Card("Cuddling.", Colour.WHITE));
        cards.add(new Card("Wiping her butt.", Colour.WHITE));
        cards.add(new Card("Morgan Freeman's voice.", Colour.WHITE));
        cards.add(new Card("A middle-aged man on roller skates.", Colour.WHITE));
        cards.add(new Card("Gandhi.", Colour.WHITE));
        cards.add(new Card("Keanu Reeves.", Colour.WHITE));
        cards.add(new Card("Child beauty pageants.", Colour.WHITE));
        cards.add(new Card("Child abuse.", Colour.WHITE));
        cards.add(new Card("Bill Nye the Science Guy.", Colour.WHITE));
        cards.add(new Card("Science.", Colour.WHITE));
        cards.add(new Card("A tribe of warrior women.", Colour.WHITE));
        cards.add(new Card("Viagra.", Colour.WHITE));
        cards.add(new Card("Her Majesty, Queen Elizabeth II.", Colour.WHITE));
        cards.add(new Card("The entire Mormon Tabernacle Choir.", Colour.WHITE));
        cards.add(new Card("This year's mass shooting.", Colour.WHITE));
        cards.add(new Card("An erection that lasts longer than four hours.", Colour.WHITE));
        cards.add(new Card("Dead Jews.", Colour.WHITE));

        cards.add(new Card("How did I lose my virginity.", Colour.BLACK));
        cards.add(new Card("Why can't I sleep at night?", Colour.BLACK));
        cards.add(new Card("What's that smell?", Colour.BLACK));
        cards.add(new Card("I got 99 problems but __________ ain't one.", Colour.BLACK));
        cards.add(new Card("Maybe she's born with it. Maybe it's _________.", Colour.BLACK));
        cards.add(new Card("What's the next Happy Meal toy?", Colour.BLACK));
        cards.add(new Card("Here is the church. Here is the steeple. Open the doors and there is ________.", Colour.BLACK));
        cards.add(new Card("It's a pity that kids these days are all getting involved with _________.", Colour.BLACK));
        cards.add(new Card("Today on Maury: 'Help! My son is _______!'", Colour.BLACK));
        cards.add(new Card("Alternative medicine is now embracing the curative powers of __________.", Colour.BLACK));
        cards.add(new Card("What's that sound?", Colour.BLACK));
        cards.add(new Card("What ended my last relationship?", Colour.BLACK));
        cards.add(new Card("MTV's new reality show features eight washed-up celebreties living with ________.", Colour.BLACK));
        cards.add(new Card("I drink to forget ________.", Colour.BLACK));
        cards.add(new Card("I'm sorry Professor, but I couldn't complete my homework because of ________.", Colour.BLACK));
        cards.add(new Card("What is Batman's guilty pleasure?", Colour.BLACK));
        cards.add(new Card("The world will end with _________.", Colour.BLACK));
        cards.add(new Card("What's a girl's best friend.", Colour.BLACK));
        cards.add(new Card("TSA guidelines now prohibit __________ on airplanes.", Colour.BLACK));
        cards.add(new Card("__________. That's how I want to die.", Colour.BLACK));
        cards.add(new Card("I get by with a little help from ________.", Colour.BLACK));
        cards.add(new Card("Dear Abby, I'm having some trouble with _______ and would like your advice,", Colour.BLACK));
        cards.add(new Card("Instead of coal, Santa now give the bad children ________.", Colour.BLACK));
        cards.add(new Card("What's the most emo?", Colour.BLACK));
        cards.add(new Card("In 1000 years, when paper money is a distant memory, how will we pay for goods and services?", Colour.BLACK));
        cards.add(new Card("A romantic candlelight dinner would be incomplete without _________.", Colour.BLACK));
        cards.add(new Card("White people like ________.", Colour.BLACK));
        cards.add(new Card("____________! High five, bro.", Colour.BLACK));
        cards.add(new Card("Next from J.K. Rowling: Harry Potter and the Chamber of __________.", Colour.BLACK));
        cards.add(new Card("Introcucing Xtreme Baseball! It's like baseball, but with _________!", Colour.BLACK));
        cards.add(new Card("War! What is it good for?", Colour.BLACK));
        cards.add(new Card("During sex, I like to think about _________.", Colour.BLACK));
        cards.add(new Card("What are my parents hiding from me?", Colour.BLACK));
        cards.add(new Card("What will always get you laid?", Colour.BLACK));
        cards.add(new Card("In L.A. County Jail, word is you can trade 200 cigarettes for _________.", Colour.BLACK));
        cards.add(new Card("What did I bring back from Mexico?", Colour.BLACK));
        cards.add(new Card("What don't you want to find in your Kung Pao chicken?", Colour.BLACK));
        cards.add(new Card("What will I bring back in time to convince people that I am a powerful wizard?", Colour.BLACK));
        cards.add(new Card("How am I maintaining my relationship status?", Colour.BLACK));
        cards.add(new Card("_____________. It's a trap!", Colour.BLACK));
        cards.add(new Card("Coming to Broadway this season: ___________: The Musical.", Colour.BLACK));
        cards.add(new Card("After the earthquake, Sean Penn brought ___________ to the people of Haiti.", Colour.BLACK));
        cards.add(new Card("Next on ESPN2, the World Series of _________.", Colour.BLACK));
        cards.add(new Card("Bug before I kill you, Mr. Bond, I must show you __________.", Colour.BLACK));
        cards.add(new Card("What gives me uncontrollable gas?", Colour.BLACK));
        cards.add(new Card("The class field trip was completely ruined by __________.", Colour.BLACK));
        cards.add(new Card("When Pharaoh remained unmoved, Moses called down a Plague of __________.", Colour.BLACK));
        cards.add(new Card("What's my secret power?", Colour.BLACK));
        cards.add(new Card("What's there a ton of in heaven?", Colour.BLACK));
        cards.add(new Card("What would grandma find disturbing, yet oddly charming?", Colour.BLACK));
        cards.add(new Card("What did the U.S. airdrop to the children Afghanistan?", Colour.BLACK));
        cards.add(new Card("What helps Obama unwind?", Colour.BLACK));
        cards.add(new Card("What did Vin Diesel eat for dinner?", Colour.BLACK));
        cards.add(new Card("_________: good to the last drop.", Colour.BLACK));
        cards.add(new Card("Why am I sticky?", Colour.BLACK));
        cards.add(new Card("What gets better with age?", Colour.BLACK));
        cards.add(new Card("________: kid-tested, mother approved.", Colour.BLACK));
        cards.add(new Card("Daddy, why is mommy crying?", Colour.BLACK));
        cards.add(new Card("Life for American Indians was forever changed  when the White Man introduced them to ________.", Colour.BLACK));
        cards.add(new Card("I don't know with what weapons World War 3 will be fought, but World War 4 will be fought with _______.", Colour.BLACK));
        cards.add(new Card("What am I giving up for Lent?", Colour.BLACK));
        cards.add(new Card("What's Donald Trump thinking about right now?", Colour.BLACK));
        cards.add(new Card("The Smithsonian Museum of Natural History has just opened an interactive exhibit on _________.", Colour.BLACK));
        cards.add(new Card("When I cam President of the U.S., I will create the Department of _________.", Colour.BLACK));
        cards.add(new Card("When I'm a billionaire, I shall erect a 50-foot statue to commemorate ________.", Colour.BLACK));
        cards.add(new Card("What's my anti-drug?", Colour.BLACK));
        cards.add(new Card("What never fails to liven up the party?", Colour.BLACK));
        cards.add(new Card("What's that new diet?", Colour.BLACK));
        cards.add(new Card("Fun tip! When your man asks you to go down on him, try surprising him with _______ instead.", Colour.BLACK));

        Database.addNewCards(cards);
    }
}
