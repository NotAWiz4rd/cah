package com.afms.cahgame.gui.controller;

import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.FullSizeCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainController {
    // Main
    private Activity mainActivity;
    private FrameLayout mainFrame;

    // user card selection
    private ListView userSelectionListView;
    private int selectedListViewPosition;
    private CardListAdapter userSelectionListAdapter;

    // instances
    private List<FullSizeCard> fullSizeCardList = new ArrayList<>();

    public MainController(Activity mainActivity) {
        this.mainActivity = mainActivity;
        mainFrame = mainActivity.findViewById(R.id.mainFrame);

        // initializer
        initializeUserCardSelection();

        // dummy data

        for (int i = 0; i < 5; i++) {
            userSelectionListAdapter.add(new Card(
                    Colour.values()[(int) (Math.random() * Colour.values().length)],
                    Arrays.stream(mainActivity.getResources().getStringArray(R.array.sample_card_texts))
                            .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                            .findAny()
                            .get()));
        }
    }


    // initializer methods

    private void initializeUserCardSelection() {
        userSelectionListView = mainActivity.findViewById(R.id.cardSelectList);
        userSelectionListAdapter = new CardListAdapter(mainActivity, new ArrayList<Card>());
        userSelectionListView.setAdapter(userSelectionListAdapter);

        userSelectionListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedListViewPosition = position;
            Toast.makeText(mainActivity, String.valueOf(selectedListViewPosition), Toast.LENGTH_SHORT).show();
            Card card = (Card) parent.getItemAtPosition(selectedListViewPosition);
            //mainFrame.addView(getFullSizeCardInstance(card));
        });
    }

    // get instances

/*
    public FullSizeCard getFullSizeCardInstance(Card card) {
        Log.d("Test", "Test");
        if (fullSizeCardList.stream().anyMatch(f -> f.getCard().equals(card))) {
            return fullSizeCardList.stream().filter(f -> f.getCard().equals(card)).findFirst().get();
        } else {
            FullSizeCard returnValue = new FullSizeCard(mainActivity, this, card);
            fullSizeCardList.add(returnValue);
            return returnValue;
        }
    }
*/

    public void showNextViewFromList() {
        int nextPos = (selectedListViewPosition + 1) % userSelectionListView.getCount();
        selectedListViewPosition = nextPos;
        Toast.makeText(mainActivity, String.valueOf(nextPos), Toast.LENGTH_SHORT).show();
        //mainFrame.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos)));
    }


    public void showPreviousViewFromList() {
        int nextPos = selectedListViewPosition - 1;
        if(nextPos < 0){
            nextPos = userSelectionListView.getCount() - 1;
        }
        selectedListViewPosition = nextPos;
        Toast.makeText(mainActivity, String.valueOf(nextPos), Toast.LENGTH_SHORT).show();
        //mainFrame.addView(getFullSizeCardInstance((Card) userSelectionListView.getItemAtPosition(nextPos)));
    }

}
