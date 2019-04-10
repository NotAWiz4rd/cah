package com.afms.cahgame.gui.controller;

import android.app.Activity;
import android.widget.ListView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.gui.components.CardListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MainController {
    private Activity mainActivity;

    public MainController(Activity mainActivity){
        this.mainActivity = mainActivity;

        // initializer
        initializeCardSelection();
    }

    private void initializeCardSelection(){
        ListView listView = mainActivity.findViewById(R.id.cardSelectList);
        CardListAdapter cardListAdapter = new CardListAdapter(mainActivity, new ArrayList<Card>());
        listView.setAdapter(cardListAdapter);
        for(int i = 0; i < 15; i++){
            cardListAdapter.add(new Card(Colour.WHITE,
                    Arrays.asList(mainActivity.getResources().getStringArray(R.array.sample_card_texts))
                            .stream()
                            .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                            .findAny()
                            .get()));
        }
    }
}
