package com.afms.cahgame.gui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.DeckSelectorDialog;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.SwipeResultListener;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;
import java.util.List;


public class ExploreDecks extends AppCompatActivity {

    private DeckSelectorDialog deckSelectorDialog;
    private FrameLayout layout_explore_decks_frame;
    private RelativeLayout layout_explore_decks_navigation;
    private ImageButton btn_explore_decks_back;
    private Button btn_explore_decks_select;
    private ImageButton btn_explore_decks_add;
    private RelativeLayout layout_explore_decks_bottom;
    private ListView list_explore_decks_cards;
    private FrameLayout btn_explore_decks_white_cards;
    private FrameLayout btn_explore_decks_black_cards;
    private RelativeLayout overlay_explore_decks_white_cards_selected;
    private RelativeLayout overlay_explore_decks_black_cards_selected;
    private ImageView img_explore_decks_black_cards_selected_icon;
    private ImageView img_explore_decks_white_cards_selected_icon;
    private TextView label_explore_decks_name;

    private CardListAdapter cardListAdapter;
    private Deck selectedDeck;

    private boolean selectedWhiteCards = false;
    private boolean selectedBlackCards = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_decks);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
    }

    private void initializeUIEvents() {
        btn_explore_decks_back.setOnClickListener(event -> finish());
        btn_explore_decks_add.setOnClickListener(event -> Toast.makeText(this, "clicked: add", Toast.LENGTH_SHORT).show());
        btn_explore_decks_select.setOnClickListener(event -> {
            deckSelectorDialog.show(getSupportFragmentManager(), "chooseDeckExplore");
        });

        deckSelectorDialog.setResultListener(result -> {
            selectedDeck = Util.convertDataDeckToPlayDeck(Util.getDataDeckFromName(result));
            label_explore_decks_name.setText(selectedDeck.getName());
            updateCardList();
        });

        btn_explore_decks_black_cards.setOnClickListener(event -> selectDisplayCardMode(false));
        btn_explore_decks_white_cards.setOnClickListener(event -> selectDisplayCardMode(true));

        list_explore_decks_cards.setOnItemClickListener((parent, view, position, id) -> {
            layout_explore_decks_frame.addView(getFullSizeCardInstance((Card) parent.getItemAtPosition(position), position));
        });

        selectDisplayCardMode(true);
    }

    private FullSizeCard getFullSizeCardInstance(Card card, int position) {
        FullSizeCard fullSizeCard = new FullSizeCard(this, card);
        fullSizeCard.setDimBackground(true);
        fullSizeCard.addOptionButton("Close", event -> layout_explore_decks_frame.removeView(fullSizeCard));
        fullSizeCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
        fullSizeCard.setSwipeResultListener(new SwipeResultListener() {
            @Override
            public void onSwipeLeft() {
                int nextPos = (position + 1) % list_explore_decks_cards.getCount();
                layout_explore_decks_frame.addView(getFullSizeCardInstance((Card) list_explore_decks_cards.getItemAtPosition(nextPos), nextPos));
            }

            @Override
            public void onSwipeRight() {
                int nextPos = position - 1;
                if (nextPos < 0) {
                    nextPos = list_explore_decks_cards.getCount() - 1;
                }
                layout_explore_decks_frame.addView(getFullSizeCardInstance((Card) list_explore_decks_cards.getItemAtPosition(nextPos), nextPos));
            }

            @Override
            public void onSwipeUp() {
                // do nothing
            }

            @Override
            public void onSwipeDown() {
                // do nothing
            }
        });
        return fullSizeCard;
    }

    private void initializeUIElements() {
        layout_explore_decks_frame = findViewById(R.id.layout_explore_decks_frame);
        layout_explore_decks_navigation = findViewById(R.id.layout_explore_decks_navigation);
        btn_explore_decks_back = findViewById(R.id.btn_explore_decks_back);
        btn_explore_decks_select = findViewById(R.id.btn_explore_decks_select);
        btn_explore_decks_add = findViewById(R.id.btn_explore_decks_add);
        layout_explore_decks_bottom = findViewById(R.id.layout_explore_decks_bottom);
        list_explore_decks_cards = findViewById(R.id.list_explore_decks_cards);
        btn_explore_decks_white_cards = findViewById(R.id.btn_explore_decks_white_cards);
        overlay_explore_decks_white_cards_selected = findViewById(R.id.overlay_explore_decks_white_cards_selected);
        btn_explore_decks_black_cards = findViewById(R.id.btn_explore_decks_black_cards);
        overlay_explore_decks_black_cards_selected = findViewById(R.id.overlay_explore_decks_black_cards_selected);
        img_explore_decks_black_cards_selected_icon = findViewById(R.id.img_explore_decks_black_cards_selected_icon);
        img_explore_decks_white_cards_selected_icon = findViewById(R.id.img_explore_decks_white_cards_selected_icon);
        label_explore_decks_name = findViewById(R.id.label_explore_decks_name);

        deckSelectorDialog = DeckSelectorDialog.create(getString(R.string.label_choose_deck));
        cardListAdapter = new CardListAdapter(this, new ArrayList<>());
        list_explore_decks_cards.setAdapter(cardListAdapter);

    }

    private void updateCardList() {
        if (selectedDeck != null) {
            cardListAdapter.clear();
            List<Card> cards = new ArrayList<>();
            if (selectedBlackCards) {
                cards.addAll(selectedDeck.getBlackCards());
            }
            if (selectedWhiteCards) {
                cards.addAll(selectedDeck.getWhiteCards());
            }
            cardListAdapter.addAll(cards);
        }
    }


    private void selectDisplayCardMode(boolean whiteCard) {
        if (whiteCard) {
            selectedWhiteCards = !selectedWhiteCards;
            if (selectedWhiteCards) {
                img_explore_decks_white_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_tick_circle_24dp));
                DrawableCompat.setTint(img_explore_decks_white_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_green));
            } else {
                img_explore_decks_white_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_x_circle_24dp));
                DrawableCompat.setTint(img_explore_decks_white_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_red));
            }
        } else {
            selectedBlackCards = !selectedBlackCards;
            if (selectedBlackCards) {
                img_explore_decks_black_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_tick_circle_24dp));
                DrawableCompat.setTint(img_explore_decks_black_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_green));
            } else {
                img_explore_decks_black_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_x_circle_24dp));
                DrawableCompat.setTint(img_explore_decks_black_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_red));
            }
        }
        updateCardList();
    }


    private void hideUI() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
    }
}
