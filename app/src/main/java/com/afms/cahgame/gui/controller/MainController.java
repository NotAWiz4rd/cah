package com.afms.cahgame.gui.controller;

import android.app.Activity;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.gui.components.CardListAdapter;

import org.w3c.dom.Text;

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
            View fullSizeCard = LayoutInflater.from(mainActivity.getApplicationContext()).inflate(R.layout.fullsize_card_options, parent, false);
            ConstraintLayout fullsizeCardLayout = fullSizeCard.findViewById(R.id.fullsizeCardLayout);
            fullsizeCardLayout.setBackgroundResource(card.getColour() == Colour.WHITE ? R.drawable.card_background_white : R.drawable.card_background_black);
            TextView fullSizeCardText = fullSizeCard.findViewById(R.id.fullsizeCardText);
            fullSizeCardText.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.cardTextColorWhite) : Color.WHITE);
            fullSizeCardText.setText(card.getText());
            TextView fullSizeGameName = fullSizeCard.findViewById(R.id.fullsizeGameName);
            fullSizeGameName.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.cardTextColorWhite) : Color.WHITE);
            Button fullSizeCardButton = fullSizeCard.findViewById(R.id.fullSizeOptionButton);
            fullSizeCardButton.setText(mainActivity.getString(R.string.close));
            fullSizeCardButton.setOnClickListener(v -> ((ViewManager)fullSizeCard.getParent()).removeView(fullSizeCard));
            Button fullSizeCardButton2 = fullSizeCard.findViewById(R.id.fullSizeOptionButton2);
            ((ViewManager)fullSizeCardButton2.getParent()).removeView(fullSizeCardButton2);
            mainFrame.addView(fullSizeCard);
        });
    }
}
