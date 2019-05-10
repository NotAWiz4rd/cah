package com.afms.cahgame.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.game.Gamestate;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.game.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        Button fab = findViewById(R.id.btn_create_lobby_create_lobby);
        fab.setOnClickListener(view -> createLobby());
    }

    private void createLobby() {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("lobby", new Lobby("012", createSamplePlayers(), "Testlobby", "", Gamestate.START));
        intent.putExtra("deck", createSampleDeck());
        intent.putExtra("handcardcount", 6);
        intent.putExtra("name", "Player1");
        intent.putExtra("host", "Player1");
        startActivity(intent);
    }

    private Deck createSampleDeck() {
        return new Deck("testdeck", getSampleWhiteCards(), getSampleBlackCards());
    }

    private Card[] getSampleBlackCards() {
        return new Card[]{
                new Card(Colour.BLACK, "Black Lorem Ipsum0"),
                new Card(Colour.BLACK, "Black Lorem Ipsum1"),
                new Card(Colour.BLACK, "Black Lorem Ipsum2"),
                new Card(Colour.BLACK, "Black Lorem Ipsum3"),
                new Card(Colour.BLACK, "Black Lorem Ipsum4"),
                new Card(Colour.BLACK, "Black Lorem Ipsum5"),
                new Card(Colour.BLACK, "Black Lorem Ipsum6"),
                new Card(Colour.BLACK, "Black Lorem Ipsum7"),
                new Card(Colour.BLACK, "Black Lorem Ipsum8")
        };
    }

    private Card[] getSampleWhiteCards() {
        return new Card[]{
                new Card(Colour.WHITE, "White Lorem Ipsum0"),
                new Card(Colour.WHITE, "White Lorem Ipsum1"),
                new Card(Colour.WHITE, "White Lorem Ipsum2"),
                new Card(Colour.WHITE, "White Lorem Ipsum3"),
                new Card(Colour.WHITE, "White Lorem Ipsum4"),
                new Card(Colour.WHITE, "White Lorem Ipsum5"),
                new Card(Colour.WHITE, "White Lorem Ipsum6"),
                new Card(Colour.WHITE, "White Lorem Ipsum7"),
                new Card(Colour.WHITE, "White Lorem Ipsum8"),
                new Card(Colour.WHITE, "White Lorem Ipsum9"),
                new Card(Colour.WHITE, "White Lorem Ipsum10"),
                new Card(Colour.WHITE, "White Lorem Ipsum11"),
                new Card(Colour.WHITE, "White Lorem Ipsum12"),
                new Card(Colour.WHITE, "White Lorem Ipsum13"),
                new Card(Colour.WHITE, "White Lorem Ipsum14"),
                new Card(Colour.WHITE, "White Lorem Ipsum15"),
                new Card(Colour.WHITE, "White Lorem Ipsum16"),
                new Card(Colour.WHITE, "White Lorem Ipsum18"),
                new Card(Colour.WHITE, "White Lorem Ipsum19"),
                new Card(Colour.WHITE, "White Lorem Ipsum20"),
                new Card(Colour.WHITE, "White Lorem Ipsum21"),
                new Card(Colour.WHITE, "White Lorem Ipsum22"),
                new Card(Colour.WHITE, "White Lorem Ipsum23"),
                new Card(Colour.WHITE, "White Lorem Ipsum24"),
                new Card(Colour.WHITE, "White Lorem Ipsum25"),
                new Card(Colour.WHITE, "White Lorem Ipsum26"),
                new Card(Colour.WHITE, "White Lorem Ipsum27"),
                new Card(Colour.WHITE, "White Lorem Ipsum28"),
                new Card(Colour.WHITE, "White Lorem Ipsum29"),
                new Card(Colour.WHITE, "White Lorem Ipsum30")
        };
    }

    private List<Player> createSamplePlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        players.add(new Player("Player3"));
        players.add(new Player("Player4"));
        return players;
    }


}
