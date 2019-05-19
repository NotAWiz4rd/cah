package com.afms.cahgame.gui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.components.ValueSelector;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;

public class CreateLobby extends AppCompatActivity {

    // statics
    private final static int DEFAULT_HANDCARD_COUNT = 7;
    private final static int DEFAULT_PLAYER_COUNT = 5;
    private final static int MIN_PLAYER_COUNT = 3;
    private final static int MAX_PLAYER_COUNT = 8;
    private final static int MIN_HANDCARD_COUNT = 3;
    private final static int MAX_HANDCARD_COUNT = 10;

    // ui elements
    private Button btn_create_lobby;
    private Button btn_select_deck;
    private ImageButton btn_back;

    private EditText input_lobby_name;
    private EditText input_handcard_count;
    private EditText input_player_count;
    private EditText input_create_lobby_password;
    private EditText input_select_deck;

    private SharedPreferences settings;

    // variables
    private ValueSelector value_selector_player_count;
    private ValueSelector value_selector_handcard_count;

    private MutableLiveData<Integer> value_player_count = new MutableLiveData<>();
    private MutableLiveData<Integer> value_handcard_count = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        settings = getSharedPreferences("Preferences", MODE_PRIVATE);
        hideUI();
        createCards();
        createSampleDeck();
        initializeUIElements();
        initializeUIEvents();
        initializeVariables();
    }

    private void initializeVariables() {
        value_handcard_count.setValue(DEFAULT_HANDCARD_COUNT);
        value_handcard_count.observe(this, integer -> input_handcard_count.setText(String.valueOf(integer)));
        value_player_count.setValue(DEFAULT_PLAYER_COUNT);
        value_player_count.observe(this, integer -> input_player_count.setText(String.valueOf(integer)));
    }

    private void initializeUIElements() {
        btn_create_lobby = findViewById(R.id.btn_create_lobby_create_lobby);
        btn_select_deck = findViewById(R.id.btn_create_lobby_select_deck);
        btn_back = findViewById(R.id.btn_create_lobby_back);
        input_handcard_count = findViewById(R.id.input_create_lobby_handcard_count);
        input_lobby_name = findViewById(R.id.input_create_lobby_name);
        input_player_count = findViewById(R.id.input_create_lobby_player_count);
        input_select_deck = findViewById(R.id.input_create_lobby_select_deck);
        input_create_lobby_password = findViewById(R.id.input_create_lobby_password);

        ArrayList<String> player_count_values = new ArrayList<>();
        for (int i = MIN_PLAYER_COUNT; i <= MAX_PLAYER_COUNT; i++) {
            player_count_values.add(String.valueOf(i));
        }
        value_selector_player_count = ValueSelector.create(getString(R.string.select_player_count), player_count_values);
        value_selector_player_count.setResultListener(result -> value_player_count.setValue(Integer.valueOf(result)));


        ArrayList<String> handcard_count_values = new ArrayList<>();
        for (int i = MIN_HANDCARD_COUNT; i <= MAX_HANDCARD_COUNT; i++) {
            handcard_count_values.add(String.valueOf(i));
        }
        value_selector_handcard_count = ValueSelector.create(getString(R.string.select_handcard_count), handcard_count_values);
        value_selector_handcard_count.setResultListener(result -> value_handcard_count.setValue(Integer.valueOf(result)));
    }

    private void initializeUIEvents() {
        btn_create_lobby.setOnClickListener(event -> {
            String lobbyId = input_lobby_name.getText().toString();

            if (Database.getLobbies().containsKey(lobbyId)) {
                Toast.makeText(this, "A lobby with this name already exists", Toast.LENGTH_LONG).show();
                return;
            }
            if (lobbyId.isEmpty()) {
                Toast.makeText(this, "Please enter a lobbyname to proceed.", Toast.LENGTH_LONG).show();
                return;
            }

            String playerName = settings.getString("player", Util.getRandomName());
            Util.saveName(settings, playerName);
            Database.addLobby(lobbyId, new Lobby(
                    lobbyId,
                    playerName,
                    input_create_lobby_password.getText().toString(),
                    Integer.parseInt(input_handcard_count.getText().toString()),
                    Integer.parseInt(input_player_count.getText().toString())));

            Intent intent = new Intent(this, WaitingLobby.class);
            intent.putExtra("lobbyId", lobbyId);
            startActivity(intent);
        });
        btn_select_deck.setOnClickListener(event -> Toast.makeText(this, "clicked " + btn_select_deck.toString(), Toast.LENGTH_SHORT).show());
        btn_back.setOnClickListener(event -> {
            finish();
        });
        input_player_count.setOnClickListener(event -> value_selector_player_count.show(getSupportFragmentManager(), "value_selector_player_count"));
        input_handcard_count.setOnClickListener(event -> value_selector_handcard_count.show(getSupportFragmentManager(), "value_selector_handcard_count"));
    }

    private void createSampleDeck() {
        if (Util.getDataDeckFromName("standarddeck") != null) {
            return;
        }
        com.afms.cahgame.data.Deck deck = new com.afms.cahgame.data.Deck("standarddeck");
        deck.addCard(1);
        deck.addCard(2);
        deck.addCard(3);
        deck.addCard(4);
        deck.addCard(5);
        deck.addCard(6);
        deck.addCard(7);
        deck.addCard(0);
        deck.addCard(8);
        deck.addCard(9);
        deck.addCard(10);
        deck.addCard(11);
        deck.addCard(12);
        deck.addCard(13);
        deck.addCard(14);
        deck.addCard(15);
        deck.addCard(16);
        deck.addCard(17);
        deck.addCard(18);
        deck.addCard(19);
        deck.addCard(20);
        deck.addCard(21);
        deck.addCard(22);
        deck.addCard(23);
        deck.addCard(24);
        deck.addCard(25);
        deck.addCard(26);
        deck.addCard(27);
        deck.addCard(28);
        deck.addCard(29);
        deck.addCard(30);
        deck.addCard(31);
        deck.addCard(32);
        deck.addCard(33);
        deck.addCard(34);
        deck.addCard(35);
        deck.addCard(36);
        deck.addCard(37);
        deck.addCard(38);
        deck.addCard(39);
        deck.addCard(40);
        deck.addCard(99);
        deck.addCard(109);
        deck.addCard(121);
        deck.addCard(240);
        deck.addCard(400);
        deck.addCard(320);
        deck.addCard(301);
        deck.addCard(299);
        deck.addCard(287);
        deck.addCard(76);
        deck.addCard(322);
        deck.addCard(288);
        deck.addCard(77);
        deck.addCard(276);
        deck.addCard(131);
        deck.addCard(311);
        deck.addCard(255);
        deck.addCard(350);
        deck.addCard(265);
        deck.addCard(340);
        deck.addCard(412);
        deck.addCard(413);
        deck.addCard(414);
        deck.addCard(415);
        deck.addCard(416);
        deck.addCard(417);
        deck.addCard(418);
        deck.addCard(419);
        deck.addCard(420);
        deck.addCard(421);
        deck.addCard(422);
        deck.addCard(423);
        deck.addCard(424);
        deck.addCard(425);
        deck.addCard(426);
        deck.addCard(427);
        deck.addCard(428);
        deck.addCard(429);
        deck.addCard(430);
        deck.addCard(431);
        Database.addDeck(deck);
    }

    private void createCards() {
        // White Cards
        Database.createNewCard("Being on fire.", Colour.WHITE);
        Database.createNewCard("Racism.", Colour.WHITE);
        Database.createNewCard("Old-people smell.", Colour.WHITE);
        Database.createNewCard("A micropenis.", Colour.WHITE);
        Database.createNewCard("Women in yogurt commercials.", Colour.WHITE);
        Database.createNewCard("Classist undertones.", Colour.WHITE);
        Database.createNewCard("Not giving a shit about the Third World", Colour.WHITE);
        Database.createNewCard("Inserting a mason jar into my anus.", Colour.WHITE);
        Database.createNewCard("Court-ordered rehab.", Colour.WHITE);
        Database.createNewCard("A windmill full of corpses.", Colour.WHITE);
        Database.createNewCard("The gays.", Colour.WHITE);
        Database.createNewCard("An oversized lollipop.", Colour.WHITE);
        Database.createNewCard("African children.", Colour.WHITE);
        Database.createNewCard("An asymmetric boob job.", Colour.WHITE);
        Database.createNewCard("Bingeing and purging.", Colour.WHITE);
        Database.createNewCard("The hardworking Mexican.", Colour.WHITE);
        Database.createNewCard("An Oedipus complex.", Colour.WHITE);
        Database.createNewCard("A tiny horse.", Colour.WHITE);
        Database.createNewCard("Boogers.", Colour.WHITE);
        Database.createNewCard("Penis envy.", Colour.WHITE);
        Database.createNewCard("Barack Obama.", Colour.WHITE);
        Database.createNewCard("My humps.", Colour.WHITE);
        Database.createNewCard("Scientology.", Colour.WHITE);
        Database.createNewCard("Skeletor.", Colour.WHITE);
        Database.createNewCard("Darth Vader.", Colour.WHITE);
        Database.createNewCard("Figgy pudding.", Colour.WHITE);
        Database.createNewCard("Advice from a wise, old black man.", Colour.WHITE);
        Database.createNewCard("Elderly Japanese men.", Colour.WHITE);
        Database.createNewCard("Free samples.", Colour.WHITE);
        Database.createNewCard("Estrogen.", Colour.WHITE);
        Database.createNewCard("Sexual tension.", Colour.WHITE);
        Database.createNewCard("Famine.", Colour.WHITE);
        Database.createNewCard("A stray pube.", Colour.WHITE);
        Database.createNewCard("Men.", Colour.WHITE);
        Database.createNewCard("Heartwarming orphans.", Colour.WHITE);
        Database.createNewCard("Chunks of dead hitchhiker.", Colour.WHITE);
        Database.createNewCard("A bag of magic beans.", Colour.WHITE);
        Database.createNewCard("Repressions.", Colour.WHITE);
        Database.createNewCard("My relationship status.", Colour.WHITE);
        Database.createNewCard("Overcompensation.", Colour.WHITE);
        Database.createNewCard("Peeing a little bit.", Colour.WHITE);
        Database.createNewCard("Pooping back and forth. Forever.", Colour.WHITE);
        Database.createNewCard("A ball of earwax, semen and toenail clippings.", Colour.WHITE);
        Database.createNewCard("Testicular torsion.", Colour.WHITE);
        Database.createNewCard("The Devil himself.", Colour.WHITE);
        Database.createNewCard("The World of Warcraft.", Colour.WHITE);
        Database.createNewCard("MechaHitler.", Colour.WHITE);
        Database.createNewCard("Being fabulous.", Colour.WHITE);
        Database.createNewCard("Pictures of boobs.", Colour.WHITE);
        Database.createNewCard("A gentle caress of the inner thigh.", Colour.WHITE);
        Database.createNewCard("The Amish.", Colour.WHITE);
        Database.createNewCard("The rhythms of Africa.", Colour.WHITE);
        Database.createNewCard("Lance Armstrong's missing testicle.", Colour.WHITE);
        Database.createNewCard("Pedophiles.", Colour.WHITE);
        Database.createNewCard("The Pope.", Colour.WHITE);
        Database.createNewCard("Flying sex snakes.", Colour.WHITE);
        Database.createNewCard("Sexy pillow fights.", Colour.WHITE);
        Database.createNewCard("Invading Poiand.", Colour.WHITE);
        Database.createNewCard("Cybernetic enhancements.", Colour.WHITE);
        Database.createNewCard("Civilian casualties.", Colour.WHITE);
        Database.createNewCard("Jobs.", Colour.WHITE);
        Database.createNewCard("The female orgasm.", Colour.WHITE);
        Database.createNewCard("Bitches.", Colour.WHITE);
        Database.createNewCard("The Boy Scouts of America.", Colour.WHITE);
        Database.createNewCard("Auschwitz.", Colour.WHITE);
        Database.createNewCard("Finger painting.", Colour.WHITE);
        Database.createNewCard("The Care Bear Stare.", Colour.WHITE);
        Database.createNewCard("The Jews.", Colour.WHITE);
        Database.createNewCard("Being marginalized.", Colour.WHITE);
        Database.createNewCard("The Blood of Christ.", Colour.WHITE);
        Database.createNewCard("Dead parents.", Colour.WHITE);
        Database.createNewCard("The art of seduction.", Colour.WHITE);
        Database.createNewCard("Dying of dysentery.", Colour.WHITE);
        Database.createNewCard("Magnets.", Colour.WHITE);
        Database.createNewCard("Jewish fraternities.", Colour.WHITE);
        Database.createNewCard("Natalie Portman.", Colour.WHITE);
        Database.createNewCard("Agriculture.", Colour.WHITE);
        Database.createNewCard("Surprise sex!", Colour.WHITE);
        Database.createNewCard("The homosexual agenda.", Colour.WHITE);
        Database.createNewCard("Robert Downey, Jr.", Colour.WHITE);
        Database.createNewCard("The Trail of Tears.", Colour.WHITE);
        Database.createNewCard("Funky fresh rhymes.", Colour.WHITE);
        Database.createNewCard("The light of a billion suns.", Colour.WHITE);
        Database.createNewCard("Amputees.", Colour.WHITE);
        Database.createNewCard("Throwing a virgin into a volcano.", Colour.WHITE);
        Database.createNewCard("Italians.", Colour.WHITE);
        Database.createNewCard("Explosions.", Colour.WHITE);
        Database.createNewCard("A good sniff.", Colour.WHITE);
        Database.createNewCard("Destroying the evidence.", Colour.WHITE);
        Database.createNewCard("Children on leashes.", Colour.WHITE);
        Database.createNewCard("Catapults.", Colour.WHITE);
        Database.createNewCard("One trillion dollars.", Colour.WHITE);
        Database.createNewCard("Friends with benefits.", Colour.WHITE);
        Database.createNewCard("Dying.", Colour.WHITE);
        Database.createNewCard("Silence.", Colour.WHITE);
        Database.createNewCard("Growing a pair.", Colour.WHITE);
        Database.createNewCard("YOU MUST CONSTRUCT ADDITIONAL PYLONS.", Colour.WHITE);
        Database.createNewCard("Justin Bieber.", Colour.WHITE);
        Database.createNewCard("The Holy Bible.", Colour.WHITE);
        Database.createNewCard("Balls.", Colour.WHITE);
        Database.createNewCard("Praying the gay away.", Colour.WHITE);
        Database.createNewCard("Teenage pregnancy.", Colour.WHITE);
        Database.createNewCard("German dungeon porn.", Colour.WHITE);
        Database.createNewCard("The invisible hand.", Colour.WHITE);
        Database.createNewCard("My inner demons.", Colour.WHITE);
        Database.createNewCard("Powerful thighs.", Colour.WHITE);
        Database.createNewCard("Getting naked and watching Nickelodeon.", Colour.WHITE);
        Database.createNewCard("Crippling debt.", Colour.WHITE);
        Database.createNewCard("Kamikaze pilots.", Colour.WHITE);
        Database.createNewCard("Teaching a robot to love.", Colour.WHITE);
        Database.createNewCard("Police brutality.", Colour.WHITE);
        Database.createNewCard("Horse meat.", Colour.WHITE);
        Database.createNewCard("All-you-can-eat shrimp for $4.99", Colour.WHITE);
        Database.createNewCard("Heteronormativity.", Colour.WHITE);
        Database.createNewCard("Michael Jackson.", Colour.WHITE);
        Database.createNewCard("A really cool hat.", Colour.WHITE);
        Database.createNewCard("Copping a feel.", Colour.WHITE);
        Database.createNewCard("Crystal meth.", Colour.WHITE);
        Database.createNewCard("Shapeshifters.", Colour.WHITE);
        Database.createNewCard("Fingering.", Colour.WHITE);
        Database.createNewCard("A disappointing birthday party.", Colour.WHITE);
        Database.createNewCard("The Patriarchy.", Colour.WHITE);
        Database.createNewCard("My soul.", Colour.WHITE);
        Database.createNewCard("A sausage festival.", Colour.WHITE);
        Database.createNewCard("The chronic.", Colour.WHITE);
        Database.createNewCard("Eugenics.", Colour.WHITE);
        Database.createNewCard("Synergistic management solutions.", Colour.WHITE);
        Database.createNewCard("RoboCop.", Colour.WHITE);
        Database.createNewCard("Stephen Hawking talking dirty.", Colour.WHITE);
        Database.createNewCard("A man on the brink of orgasm.", Colour.WHITE);
        Database.createNewCard("Fiery poops.", Colour.WHITE);
        Database.createNewCard("Public ridicule.", Colour.WHITE);
        Database.createNewCard("White-man scalps.", Colour.WHITE);
        Database.createNewCard("The morbidly obese.", Colour.WHITE);
        Database.createNewCard("Object permanence.", Colour.WHITE);
        Database.createNewCard("Lockjaw.", Colour.WHITE);
        Database.createNewCard("Joe Biden.", Colour.WHITE);
        Database.createNewCard("Bio-engineered assault turtles with acid breath.", Colour.WHITE);
        Database.createNewCard("Wet dreams.", Colour.WHITE);
        Database.createNewCard("Hip hop jewels.", Colour.WHITE);
        Database.createNewCard("Firing a rifle into the air while balls deep into a squealing hog.", Colour.WHITE);
        Database.createNewCard("Panda sex.", Colour.WHITE);
        Database.createNewCard("Necrophilia.", Colour.WHITE);
        Database.createNewCard("Grave robbing.", Colour.WHITE);
        Database.createNewCard("A bleached asshole.", Colour.WHITE);
        Database.createNewCard("Muhammad (Praise Be Unto Him).", Colour.WHITE);
        Database.createNewCard("Multiple stab wounds.", Colour.WHITE);
        Database.createNewCard("Daniel Radcliffe's delicious asshole.", Colour.WHITE);
        Database.createNewCard("A monkey smoking a cigar.", Colour.WHITE);
        Database.createNewCard("Smegma.", Colour.WHITE);
        Database.createNewCard("A live studio audience.", Colour.WHITE);
        Database.createNewCard("The violation of our most basic human rights.", Colour.WHITE);
        Database.createNewCard("Unfathomable stupidity.", Colour.WHITE);
        Database.createNewCard("Sunshine and rainbows.", Colour.WHITE);
        Database.createNewCard("Whipping it out.", Colour.WHITE);
        Database.createNewCard("The token minority.", Colour.WHITE);
        Database.createNewCard("The terrorists.", Colour.WHITE);
        Database.createNewCard("A snapping turtle biting the tip of your penis.", Colour.WHITE);
        Database.createNewCard("Vehicular manslaughter.", Colour.WHITE);
        Database.createNewCard("The Great Depression.", Colour.WHITE);
        Database.createNewCard("Emotions.", Colour.WHITE);
        Database.createNewCard("Getting so angry that you pop a boner.", Colour.WHITE);
        Database.createNewCard("Same-sex ice dancing.", Colour.WHITE);
        Database.createNewCard("An M16 assault rifle.", Colour.WHITE);
        Database.createNewCard("Man meat.", Colour.WHITE);
        Database.createNewCard("Incest.", Colour.WHITE);
        Database.createNewCard("A foul mouth.", Colour.WHITE);
        Database.createNewCard("Flightless birds.", Colour.WHITE);
        Database.createNewCard("Doing the right thing.", Colour.WHITE);
        Database.createNewCard("When you fart and a little bit comes out.", Colour.WHITE);
        Database.createNewCard("Being a dick to children.", Colour.WHITE);
        Database.createNewCard("Poopy diapers.", Colour.WHITE);
        Database.createNewCard("Seeing grandma naked.", Colour.WHITE);
        Database.createNewCard("Raptor attacks.", Colour.WHITE);
        Database.createNewCard("Concealing a boner.", Colour.WHITE);
        Database.createNewCard("Full frontal nudity.", Colour.WHITE);
        Database.createNewCard("Nipple blades.", Colour.WHITE);
        Database.createNewCard("A bitch slap.", Colour.WHITE);
        Database.createNewCard("Michelle Obama's arms.", Colour.WHITE);
        Database.createNewCard("Mouth herpes.", Colour.WHITE);
        Database.createNewCard("A robust mongoloid.", Colour.WHITE);
        Database.createNewCard("Mutually-assured destruction.", Colour.WHITE);
        Database.createNewCard("The Rapture.", Colour.WHITE);
        Database.createNewCard("Road head.", Colour.WHITE);
        Database.createNewCard("Stalin.", Colour.WHITE);
        Database.createNewCard("Lactation.", Colour.WHITE);
        Database.createNewCard("The true meaning of Christmas.", Colour.WHITE);
        Database.createNewCard("Self-loathing.", Colour.WHITE);
        Database.createNewCard("A brain tumor.", Colour.WHITE);
        Database.createNewCard("Dead babies.", Colour.WHITE);
        Database.createNewCard("New Age music.", Colour.WHITE);
        Database.createNewCard("A thermonuclear detonation.", Colour.WHITE);
        Database.createNewCard("Geese.", Colour.WHITE);
        Database.createNewCard("Kanye West.", Colour.WHITE);
        Database.createNewCard("God.", Colour.WHITE);
        Database.createNewCard("A spastic nerd.", Colour.WHITE);
        Database.createNewCard("Harry Potter erotica.", Colour.WHITE);
        Database.createNewCard("Kids with ass cancer.", Colour.WHITE);
        Database.createNewCard("Lumberjack fantasies.", Colour.WHITE);
        Database.createNewCard("The American Dream.", Colour.WHITE);
        Database.createNewCard("Puberty.", Colour.WHITE);
        Database.createNewCard("Sweet, sweet vengeance.", Colour.WHITE);
        Database.createNewCard("Winking at old people.", Colour.WHITE);
        Database.createNewCard("The wonders of the Orient.", Colour.WHITE);
        Database.createNewCard("Oompa-Loompas.", Colour.WHITE);
        Database.createNewCard("Authentic Mexican cuisine.", Colour.WHITE);
        Database.createNewCard("Preteens.", Colour.WHITE);
        Database.createNewCard("The Little Engine That Could.", Colour.WHITE);
        Database.createNewCard("A Fleshlight.", Colour.WHITE);
        Database.createNewCard("Erectile dysfunction.", Colour.WHITE);
        Database.createNewCard("Having anuses for eyes.", Colour.WHITE);
        Database.createNewCard("Saxophone solos.", Colour.WHITE);
        Database.createNewCard("Land mines.", Colour.WHITE);
        Database.createNewCard("Running out of semen.", Colour.WHITE);
        Database.createNewCard("Me time.", Colour.WHITE);
        Database.createNewCard("Nickelback.", Colour.WHITE);
        Database.createNewCard("Vigilante justice.", Colour.WHITE);
        Database.createNewCard("The South.", Colour.WHITE);
        Database.createNewCard("Opposable thumbs.", Colour.WHITE);
        Database.createNewCard("Ghosts.", Colour.WHITE);
        Database.createNewCard("Alcoholism.", Colour.WHITE);
        Database.createNewCard("Poorly-timed Holocaust jokes.", Colour.WHITE);
        Database.createNewCard("Inappropriate yodeling.", Colour.WHITE);
        Database.createNewCard("Battlefield amputations.", Colour.WHITE);
        Database.createNewCard("Exactly what you'd expect.", Colour.WHITE);
        Database.createNewCard("A time travel paradox.", Colour.WHITE);
        Database.createNewCard("AXE Body Spray.", Colour.WHITE);
        Database.createNewCard("The pirate's life.", Colour.WHITE);
        Database.createNewCard("Saying 'I love you.'", Colour.WHITE);
        Database.createNewCard("A sassy black woman.", Colour.WHITE);
        Database.createNewCard("Being a motherfucking sorcerer.", Colour.WHITE);
        Database.createNewCard("A mopey zoo lion.", Colour.WHITE);
        Database.createNewCard("A falcon with a cap on its head.", Colour.WHITE);
        Database.createNewCard("Farting and walking away.", Colour.WHITE);
        Database.createNewCard("A mating display.", Colour.WHITE);
        Database.createNewCard("The Chinese gymnastics team.", Colour.WHITE);
        Database.createNewCard("Friction.", Colour.WHITE);
        Database.createNewCard("Asians who aren't good at math.", Colour.WHITE);
        Database.createNewCard("Fear itself.", Colour.WHITE);
        Database.createNewCard("A can of whoop-ass.", Colour.WHITE);
        Database.createNewCard("Licking things to claim them as your own.", Colour.WHITE);
        Database.createNewCard("Vikings.", Colour.WHITE);
        Database.createNewCard("Hot cheese.", Colour.WHITE);
        Database.createNewCard("Nicolas Cage.", Colour.WHITE);
        Database.createNewCard("A defective condom.", Colour.WHITE);
        Database.createNewCard("The inevitable heat death of the universe.", Colour.WHITE);
        Database.createNewCard("Republicans.", Colour.WHITE);
        Database.createNewCard("William Shatner.", Colour.WHITE);
        Database.createNewCard("Tentacle porn.", Colour.WHITE);
        Database.createNewCard("Sperm whales.", Colour.WHITE);
        Database.createNewCard("Lady Gaga.", Colour.WHITE);
        Database.createNewCard("The wrath of Vladimir Putin.", Colour.WHITE);
        Database.createNewCard("Gloryholes.", Colour.WHITE);
        Database.createNewCard("Daddy issues.", Colour.WHITE);
        Database.createNewCard("A mime having a stroke.", Colour.WHITE);
        Database.createNewCard("White people.", Colour.WHITE);
        Database.createNewCard("A lifetime of sadness.", Colour.WHITE);
        Database.createNewCard("Tasteful sideboob.", Colour.WHITE);
        Database.createNewCard("Nazis.", Colour.WHITE);
        Database.createNewCard("A cooler full of organs.", Colour.WHITE);
        Database.createNewCard("Giving 110%.", Colour.WHITE);
        Database.createNewCard("Doin' it in the butt.", Colour.WHITE);
        Database.createNewCard("Holding down a child and farting all over him.", Colour.WHITE);
        Database.createNewCard("A homoerotic volleyball montage.", Colour.WHITE);
        Database.createNewCard("Puppies!", Colour.WHITE);
        Database.createNewCard("Nature male enhancement.", Colour.WHITE);
        Database.createNewCard("Brown people.", Colour.WHITE);
        Database.createNewCard("Dropping a chandelier on your enemies and riding the rope up.", Colour.WHITE);
        Database.createNewCard("Soup that is too hot.", Colour.WHITE);
        Database.createNewCard("Sex with Patrick Stewart.", Colour.WHITE);
        Database.createNewCard("Hormone injections.", Colour.WHITE);
        Database.createNewCard("Pulling out.", Colour.WHITE);
        Database.createNewCard("The Big Bang.", Colour.WHITE);
        Database.createNewCard("Giving birth to the Antichrist.", Colour.WHITE);
        Database.createNewCard("Dark and mysterious forces beyong our control.", Colour.WHITE);
        Database.createNewCard("Count Chocula.", Colour.WHITE);
        Database.createNewCard("The Hamburglar.", Colour.WHITE);
        Database.createNewCard("Not reciprocating oral sex.", Colour.WHITE);
        Database.createNewCard("Hot people.", Colour.WHITE);
        Database.createNewCard("Foreskin.", Colour.WHITE);
        Database.createNewCard("Assless chaps.", Colour.WHITE);
        Database.createNewCard("The miracle of childbirth.", Colour.WHITE);
        Database.createNewCard("Waiting 'til marriage.", Colour.WHITE);
        Database.createNewCard("Two midgets shitting into a bucket.", Colour.WHITE);
        Database.createNewCard("A sad handjob.", Colour.WHITE);
        Database.createNewCard("Cheating in the Special Olympics.", Colour.WHITE);
        Database.createNewCard("Miley Cirus at 55.", Colour.WHITE);
        Database.createNewCard("Our first chimpanzee president.", Colour.WHITE);
        Database.createNewCard("Extremely tight pants.", Colour.WHITE);
        Database.createNewCard("Third base.", Colour.WHITE);
        Database.createNewCard("Waking up half-naked in a Denny's parking lot.", Colour.WHITE);
        Database.createNewCard("White priilege.", Colour.WHITE);
        Database.createNewCard("Hope.", Colour.WHITE);
        Database.createNewCard("Taking off your shirt.", Colour.WHITE);
        Database.createNewCard("Ethnic cleansing.", Colour.WHITE);
        Database.createNewCard("Getting really high.", Colour.WHITE);
        Database.createNewCard("Natural selection.", Colour.WHITE);
        Database.createNewCard("A gassy antelope.", Colour.WHITE);
        Database.createNewCard("My sex life.", Colour.WHITE);
        Database.createNewCard("Pretending to care.", Colour.WHITE);
        Database.createNewCard("My black ass.", Colour.WHITE);
        Database.createNewCard("BATMAN!!!", Colour.WHITE);
        Database.createNewCard("Homeless people.", Colour.WHITE);
        Database.createNewCard("Racially-biased SAT questions.", Colour.WHITE);
        Database.createNewCard("Centaurs.", Colour.WHITE);
        Database.createNewCard("A salty surprise.", Colour.WHITE);
        Database.createNewCard("72 virgins.", Colour.WHITE);
        Database.createNewCard("Pixelated bukkake.", Colour.WHITE);
        Database.createNewCard("Seppuku.", Colour.WHITE);
        Database.createNewCard("Mestrual rage.", Colour.WHITE);
        Database.createNewCard("Sexual peeing.", Colour.WHITE);
        Database.createNewCard("An endless tream of diarrhea.", Colour.WHITE);
        Database.createNewCard("Horrifiyng laser hair removal accidents.", Colour.WHITE);
        Database.createNewCard("A fetus.", Colour.WHITE);
        Database.createNewCard("Riding off into the sunset.", Colour.WHITE);
        Database.createNewCard("Goblins.", Colour.WHITE);
        Database.createNewCard("Eating the last known bison.", Colour.WHITE);
        Database.createNewCard("Shiny objects.", Colour.WHITE);
        Database.createNewCard("Being rich.", Colour.WHITE);
        Database.createNewCard("World peace.", Colour.WHITE);
        Database.createNewCard("Dick fingers.", Colour.WHITE);
        Database.createNewCard("Chainsaws for hands.", Colour.WHITE);
        Database.createNewCard("Penis breath.", Colour.WHITE);
        Database.createNewCard("Laying an egg.", Colour.WHITE);
        Database.createNewCard("My genitals.", Colour.WHITE);
        Database.createNewCard("Grandma.", Colour.WHITE);
        Database.createNewCard("Flesh-eating bacteria.", Colour.WHITE);
        Database.createNewCard("Poor people.", Colour.WHITE);
        Database.createNewCard("50,000 volts straight to the nipples.", Colour.WHITE);
        Database.createNewCard("The Übermensch.", Colour.WHITE);
        Database.createNewCard("Poor life choices.", Colour.WHITE);
        Database.createNewCard("Altar boys.", Colour.WHITE);
        Database.createNewCard("My vagina.", Colour.WHITE);
        Database.createNewCard("Pac-Man uncontrollably guzzling cum.", Colour.WHITE);
        Database.createNewCard("Sniffing glue.", Colour.WHITE);
        Database.createNewCard("The placenta.", Colour.WHITE);
        Database.createNewCard("Spontaneous human combustion.", Colour.WHITE);
        Database.createNewCard("The KKK.", Colour.WHITE);
        Database.createNewCard("The clitoris.", Colour.WHITE);
        Database.createNewCard("Not wearing pants.", Colour.WHITE);
        Database.createNewCard("Consensual sex.", Colour.WHITE);
        Database.createNewCard("Black people.", Colour.WHITE);
        Database.createNewCard("A bucket of fish heads.", Colour.WHITE);
        Database.createNewCard("Passive-agrressive Post-it notes.", Colour.WHITE);
        Database.createNewCard("The heart of a child.", Colour.WHITE);
        Database.createNewCard("Crumbs all over the god damn carpet.", Colour.WHITE);
        Database.createNewCard("Being fat and stupid.", Colour.WHITE);
        Database.createNewCard("Getting married, having a few kids, buying some stuff, retiring to Florida, and dying.", Colour.WHITE);
        Database.createNewCard("Sean Connery.", Colour.WHITE);
        Database.createNewCard("Expecting to burp and vomiting on the floor.", Colour.WHITE);
        Database.createNewCard("Wifely duties.", Colour.WHITE);
        Database.createNewCard("A pyramid of severed heads.", Colour.WHITE);
        Database.createNewCard("Genghis Khan.", Colour.WHITE);
        Database.createNewCard("Crucifixion.", Colour.WHITE);
        Database.createNewCard("Friendly fire.", Colour.WHITE);
        Database.createNewCard("AIDS.", Colour.WHITE);
        Database.createNewCard("8 oz. of sweet Mexican black-tar heroin.", Colour.WHITE);
        Database.createNewCard("Half-assed foreplay.", Colour.WHITE);
        Database.createNewCard("Edible underpants.", Colour.WHITE);
        Database.createNewCard("My collection of high-tech sex toys.", Colour.WHITE);
        Database.createNewCard("The Force.", Colour.WHITE);
        Database.createNewCard("Bees?", Colour.WHITE);
        Database.createNewCard("Some god-damn peace and quiet.", Colour.WHITE);
        Database.createNewCard("Jerking off into a pool of children's tears.", Colour.WHITE);
        Database.createNewCard("A micropig wearing a tiny raincoat and booties.", Colour.WHITE);
        Database.createNewCard("Three dicks at the same time.", Colour.WHITE);
        Database.createNewCard("Masturbation.", Colour.WHITE);
        Database.createNewCard("Tom Cruise.", Colour.WHITE);
        Database.createNewCard("Anal beads.", Colour.WHITE);
        Database.createNewCard("Drinking alone.", Colour.WHITE);
        Database.createNewCard("Cards Against Humanity.", Colour.WHITE);
        Database.createNewCard("Coat hanger abortions.", Colour.WHITE);
        Database.createNewCard("Used panties.", Colour.WHITE);
        Database.createNewCard("Cuddling.", Colour.WHITE);
        Database.createNewCard("Wiping her butt.", Colour.WHITE);
        Database.createNewCard("Morgan Freeman's voice.", Colour.WHITE);
        Database.createNewCard("A middle-aged man on roller skates.", Colour.WHITE);
        Database.createNewCard("Gandhi.", Colour.WHITE);
        Database.createNewCard("Keanu Reeves.", Colour.WHITE);
        Database.createNewCard("Child beauty pageants.", Colour.WHITE);
        Database.createNewCard("Child abuse.", Colour.WHITE);
        Database.createNewCard("Bill Nye the Science Guy.", Colour.WHITE);
        Database.createNewCard("Science.", Colour.WHITE);
        Database.createNewCard("A tribe of warrior women.", Colour.WHITE);
        Database.createNewCard("Viagra.", Colour.WHITE);
        Database.createNewCard("Her Majesty, Queen Elizabeth II.", Colour.WHITE);
        Database.createNewCard("The entire Mormon Tabernacle Choir.", Colour.WHITE);
        Database.createNewCard("This year's mass shooting.", Colour.WHITE);
        Database.createNewCard("An erection that lasts longer than four hours.", Colour.WHITE);
        Database.createNewCard("Dead Jews.", Colour.WHITE);

        // Black Cards
        Database.createNewCard("How did I lose my virginity.", Colour.BLACK);
        Database.createNewCard("Why can't I sleep at night?", Colour.BLACK);
        Database.createNewCard("What's that smell?", Colour.BLACK);
        Database.createNewCard("I got 99 problems but __________ ain't one.", Colour.BLACK);
        Database.createNewCard("Maybe she's born with it. Maybe it's _________.", Colour.BLACK);
        Database.createNewCard("What's the next Happy Meal toy?", Colour.BLACK);
        Database.createNewCard("Here is the church. Here is the steeple. Open the doors and there is ________.", Colour.BLACK);
        Database.createNewCard("It's a pity that kids these days are all getting involved with _________.", Colour.BLACK);
        Database.createNewCard("Today on Maury: 'Help! My son is _______!'", Colour.BLACK);
        Database.createNewCard("Alternative medicine is now embracing the curative powers of __________.", Colour.BLACK);
        Database.createNewCard("What's that sound?", Colour.BLACK);
        Database.createNewCard("What ended my last relationship?", Colour.BLACK);
        Database.createNewCard("MTV's new reality show features eight washed-up celebreties living with ________.", Colour.BLACK);
        Database.createNewCard("I drink to forget ________.", Colour.BLACK);
        Database.createNewCard("I'm sorry Professor, but I couldn't complete my homework because of ________.", Colour.BLACK);
        Database.createNewCard("What is Batman's guilty pleasure?", Colour.BLACK);
        Database.createNewCard("The world will end with _________.", Colour.BLACK);
        Database.createNewCard("What's a girl's best friend.", Colour.BLACK);
        Database.createNewCard("TSA guidelines now prohibit __________ on airplanes.", Colour.BLACK);
        Database.createNewCard("__________. That's how I want to die.", Colour.BLACK);
        Database.createNewCard("I get by with a little help from ________.", Colour.BLACK);
        Database.createNewCard("Dear Abby, I'm having some trouble with _______ and would like your advice,", Colour.BLACK);
        Database.createNewCard("Instead of coal, Santa now give the bad children ________.", Colour.BLACK);
        Database.createNewCard("What's the most emo?", Colour.BLACK);
        Database.createNewCard("In 1000 years, when paper money is a distant memory, how will we pay for goods and services?", Colour.BLACK);
        Database.createNewCard("A romantic candlelight dinner would be incomplete without _________.", Colour.BLACK);
        Database.createNewCard("White people like ________.", Colour.BLACK);
        Database.createNewCard("____________! High five, bro.", Colour.BLACK);
        Database.createNewCard("Next from J.K. Rowling: Harry Potter and the Chamber of __________.", Colour.BLACK);
        Database.createNewCard("Introcucing Xtreme Baseball! It's like baseball, but with _________!", Colour.BLACK);
        Database.createNewCard("War! What is it good for?", Colour.BLACK);
        Database.createNewCard("During sex, I like to think about _________.", Colour.BLACK);
        Database.createNewCard("What are my parents hiding from me?", Colour.BLACK);
        Database.createNewCard("What will always get you laid?", Colour.BLACK);
        Database.createNewCard("In L.A. County Jail, word is you can trade 200 cigarettes for _________.", Colour.BLACK);
        Database.createNewCard("What did I bring back from Mexico?", Colour.BLACK);
        Database.createNewCard("What don't you want to find in your Kung Pao chicken?", Colour.BLACK);
        Database.createNewCard("What will I bring back in time to convince people that I am a powerful wizard?", Colour.BLACK);
        Database.createNewCard("How am I maintaining my relationship status?", Colour.BLACK);
        Database.createNewCard("_____________. It's a trap!", Colour.BLACK);
        Database.createNewCard("Coming to Broadway this season: ___________: The Musical.", Colour.BLACK);
        Database.createNewCard("After the earthquake, Sean Penn brought ___________ to the people of Haiti.", Colour.BLACK);
        Database.createNewCard("Next on ESPN2, the World Series of _________.", Colour.BLACK);
        Database.createNewCard("Bug before I kill you, Mr. Bond, I must show you __________.", Colour.BLACK);
        Database.createNewCard("What gives me uncontrollable gas?", Colour.BLACK);
        Database.createNewCard("The class field trip was completely ruined by __________.", Colour.BLACK);
        Database.createNewCard("When Pharaoh remained unmoved, Moses called down a Plague of __________.", Colour.BLACK);
        Database.createNewCard("What's my secret power?", Colour.BLACK);
        Database.createNewCard("What's there a ton of in heaven?", Colour.BLACK);
        Database.createNewCard("What would grandma find disturbing, yet oddly charming?", Colour.BLACK);
        Database.createNewCard("What did the U.S. airdrop to the children Afghanistan?", Colour.BLACK);
        Database.createNewCard("What helps Obama unwind?", Colour.BLACK);
        Database.createNewCard("What did Vin Diesel eat for dinner?", Colour.BLACK);
        Database.createNewCard("_________: good to the last drop.", Colour.BLACK);
        Database.createNewCard("Why am I sticky?", Colour.BLACK);
        Database.createNewCard("What gets better with age?", Colour.BLACK);
        Database.createNewCard("________: kid-tested, mother approved.", Colour.BLACK);
        Database.createNewCard("Daddy, why is mommy crying?", Colour.BLACK);
        Database.createNewCard("Life for American Indians was forever changed  when the White Man introduced them to ________.", Colour.BLACK);
        Database.createNewCard("I don't know with what weapons World War 3 will be fought, but World War 4 will be fought with _______.", Colour.BLACK);
        Database.createNewCard("What am I giving up for Lent?", Colour.BLACK);
        Database.createNewCard("What's Donald Trump thinking about right now?", Colour.BLACK);
        Database.createNewCard("The Smithsonian Museum of Natural History has just opened an interactive exhibit on _________.", Colour.BLACK);
        Database.createNewCard("When I cam President of the U.S., I will create the Department of _________.", Colour.BLACK);
        Database.createNewCard("When I'm a billionaire, I shall erect a 50-foot statue to commemorate ________.", Colour.BLACK);
        Database.createNewCard("What's my anti-drug?", Colour.BLACK);
        Database.createNewCard("What never fails to liven up the party?", Colour.BLACK);
        Database.createNewCard("What's that new diet?", Colour.BLACK);
        Database.createNewCard("Fun tip! When your man asks you to go down on him, try surprising him with _______ instead.", Colour.BLACK);
    }

    private void hideUI() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+

        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
    }
}
