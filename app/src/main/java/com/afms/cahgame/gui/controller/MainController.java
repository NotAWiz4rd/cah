package com.afms.cahgame.gui.controller;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.FullSizeCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MainController {
    // main
    private Activity mainActivity;
    private FrameLayout mainFrame;

    // user card selection
    private ListView userSelectionListView;
    private CardListAdapter userSelectionListAdapter;

    public MainController(Activity mainActivity){
        this.mainActivity = mainActivity;
        mainFrame = mainActivity.findViewById(R.id.mainFrame);

        // initializer
        initializeUserCardSelection();

        // dummy data

        for(int i = 0; i < 15; i++){
            userSelectionListAdapter.add(new Card(
                    Colour.values()[(int)(Math.random()*Colour.values().length)],
                    Arrays.stream(mainActivity.getResources().getStringArray(R.array.sample_card_texts))
                            .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                            .findAny()
                            .get()));
        }
    }

    private void initializeUserCardSelection(){
        userSelectionListView = mainActivity.findViewById(R.id.cardSelectList);
        userSelectionListAdapter = new CardListAdapter(mainActivity, new ArrayList<Card>());
        userSelectionListView.setAdapter(userSelectionListAdapter);

        userSelectionListView.setOnItemClickListener((parent, view, position, id) -> {
            Card card = (Card) parent.getItemAtPosition(position);
            View fullSizeCard = new FullSizeCard(mainActivity, card);
            mainFrame.addView(fullSizeCard);
        });
    }
}
