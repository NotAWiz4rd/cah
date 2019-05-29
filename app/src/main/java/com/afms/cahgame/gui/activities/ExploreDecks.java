package com.afms.cahgame.gui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Deck;
import com.afms.cahgame.gui.components.CardListAdapter;
import com.afms.cahgame.gui.components.DeckSelectorDialog;
import com.afms.cahgame.gui.components.FullSizeCard;
import com.afms.cahgame.gui.components.MessageDialog;
import com.afms.cahgame.gui.components.ResultListener;
import com.afms.cahgame.gui.components.SwipeResultListener;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ExploreDecks extends AppCompatActivity {
    private DeckSelectorDialog deckSelectorDialog;
    private FrameLayout layout_explore_decks_frame;
    private ImageButton btn_explore_decks_back;
    private Button btn_explore_decks_select;
    private ImageButton btn_explore_decks_add;
    private ListView list_explore_decks_cards;
    private FrameLayout btn_explore_decks_white_cards;
    private FrameLayout btn_explore_decks_black_cards;
    private ImageView img_explore_decks_black_cards_selected_icon;
    private ImageView img_explore_decks_white_cards_selected_icon;
    private EditText label_explore_decks_name;

    private CardListAdapter cardListAdapter;
    private Deck selectedDeck;
    private FullSizeCard customFullSizeCard;
    private FullSizeCard fullSizeCard;

    private boolean selectedWhiteCards = false;
    private boolean selectedBlackCards = false;
    private boolean createCustomDeck = false;

    private MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_decks);
        hideUI();
        initializeUIElements();
        initializeUIEvents();
    }

    private void initializeUIEvents() {
        editTextMode(label_explore_decks_name, false);
        btn_explore_decks_back.setOnClickListener(event -> {
            if (createCustomDeck) {
                if(messageDialog == null){
                    messageDialog = MessageDialog.create(
                            getString(R.string.label_leave_deck_create),
                            getString(R.string.label_hint_leave_deck_create),
                            new ArrayList<>(Arrays.asList(getString(R.string.yes), getString(R.string.close))));
                    messageDialog.setResultListener(new ResultListener() {
                        @Override
                        public void onItemClick(String result) {
                            if (result.equals(getString(R.string.yes))) {
                                createCustomDeck = false;
                                selectedDeck = null;
                                btn_explore_decks_select.setText(getString(R.string.label_select_deck));
                                editTextMode(label_explore_decks_name, false);
                                updateCardList();
                            }
                        }

                        @Override
                        public void clearReference() {
                            messageDialog = null;
                        }
                    });
                    messageDialog.show(getSupportFragmentManager(), "leaveCustomDeck");
                }
            } else {
                finish();
            }
        });
        btn_explore_decks_add.setOnClickListener(event -> {
            if (createCustomDeck) {
                if(messageDialog == null){
                    messageDialog = MessageDialog.create(
                            getString(R.string.title_cardcolor),
                            getString(R.string.label_cardcolor),
                            new ArrayList<>(Arrays.asList(getString(R.string.black), getString(R.string.white), getString(R.string.cancel)))
                    );
                    messageDialog.setResultListener(new ResultListener() {
                        @Override
                        public void onItemClick(String result) {
                            if (result.equals(getString(R.string.black))) {
                                customFullSizeCard = new FullSizeCard(getApplicationContext(), new Card(Colour.BLACK, getString(R.string.enterCardText)));
                            } else if (result.equals(getString(R.string.white))) {
                                customFullSizeCard = new FullSizeCard(getApplicationContext(), new Card(Colour.WHITE, getString(R.string.enterCardText)));
                            } else {
                                return;
                            }
                            customFullSizeCard.setDimBackground(true);
                            customFullSizeCard.addOptionButton(getString(R.string.save), v -> {
                                if (customFullSizeCard.getColour().equals(Colour.BLACK)) {
                                    selectedDeck.addBlackCard(new Card(Colour.BLACK, customFullSizeCard.getFullSizeCardText()));
                                } else {
                                    selectedDeck.addWhiteCard(new Card(Colour.WHITE, customFullSizeCard.getFullSizeCardText()));
                                }
                                layout_explore_decks_frame.removeView(customFullSizeCard);
                                updateCardList();
                            });
                            customFullSizeCard.addOptionButton(getString(R.string.remove), v -> {
                                if(customFullSizeCard.getColour().equals(Colour.BLACK)){
                                    selectedDeck.removeBlackCard(new Card(Colour.BLACK, customFullSizeCard.getFullSizeCardText()));
                                } else {
                                    selectedDeck.removeWhiteCard(new Card(Colour.WHITE, customFullSizeCard.getFullSizeCardText()));
                                }
                                layout_explore_decks_frame.removeView(customFullSizeCard);
                                updateCardList();
                            });
                            customFullSizeCard.addOptionButton(getString(R.string.close), v -> {
                                layout_explore_decks_frame.removeView(customFullSizeCard);
                            });
                            customFullSizeCard.editTextMode(true);
                            layout_explore_decks_frame.addView(customFullSizeCard);
                        }

                        @Override
                        public void clearReference() {
                            messageDialog = null;
                        }
                    });
                    messageDialog.show(getSupportFragmentManager(), "addCardMessage");
                }
            } else {
                selectedDeck = new Deck();
                createCustomDeck = true;
                editTextMode(label_explore_decks_name, true);
                btn_explore_decks_select.setText(getString(R.string.label_save_deck));
                setDisplayCardMode(true, true);
                label_explore_decks_name.setText(getString(R.string.label_enter_deck_name));
                label_explore_decks_name.setSelection(label_explore_decks_name.getText().length());
            }
        });
        btn_explore_decks_select.setOnClickListener(event -> {
            if (createCustomDeck) {
                if(messageDialog == null){
                    messageDialog = MessageDialog.create(
                            getString(R.string.title_savedeck),
                            getString(R.string.label_savedeck) + label_explore_decks_name.getText().toString(),
                            new ArrayList<>(Arrays.asList(getString(R.string.save), getString(R.string.cancel)))
                    );
                    messageDialog.setResultListener(new ResultListener() {
                        @Override
                        public void onItemClick(String result) {
                            if (result.equals(getString(R.string.save))) {
                                List<Card> gameCards = new ArrayList<>();
                                gameCards.addAll(selectedDeck.getBlackCards());
                                gameCards.addAll(selectedDeck.getWhiteCards());
                                List<Integer> dataCardIds = gameCards.stream().map(e -> Database.createNewCard(e.getText(), e.getColour()).getId()).collect(Collectors.toList());
                                gameCards.clear();
                                com.afms.cahgame.data.Deck deck = new com.afms.cahgame.data.Deck();
                                deck.setName(label_explore_decks_name.getText().toString());
                                deck.setCardIds(dataCardIds);
                                Database.addDeck(deck);
                                createCustomDeck = false;
                                editTextMode(label_explore_decks_name, false);
                                btn_explore_decks_select.setText(getString(R.string.label_select_deck));
                                selectedDeck = Database.getDeck(deck.getName());
                                updateCardList();
                            }
                        }

                        @Override
                        public void clearReference() {
                            messageDialog = null;
                        }
                    });
                    messageDialog.show(getSupportFragmentManager(), "saveCustomDeck");

                }
            } else {
                if(deckSelectorDialog == null){
                    deckSelectorDialog = DeckSelectorDialog.create(getString(R.string.label_choose_deck));
                    deckSelectorDialog.setResultListener(new ResultListener() {
                        @Override
                        public void onItemClick(String result) {
                            selectedDeck = Util.convertDataDeckToPlayDeck(Util.getDataDeckFromName(result));
                            updateCardList();
                        }

                        @Override
                        public void clearReference() {
                            deckSelectorDialog = null;
                        }
                    });
                    deckSelectorDialog.show(getSupportFragmentManager(), "chooseDeckExplore");
                }
            }
        });

        btn_explore_decks_black_cards.setOnClickListener(event -> selectDisplayCardMode(false));
        btn_explore_decks_white_cards.setOnClickListener(event -> selectDisplayCardMode(true));

        list_explore_decks_cards.setOnItemClickListener((parent, view, position, id) -> {
            if(fullSizeCard == null){
                layout_explore_decks_frame.addView(getFullSizeCardInstance((Card) parent.getItemAtPosition(position), position));
            }
        });

        selectDisplayCardMode(true);
    }

    private FullSizeCard getFullSizeCardInstance(Card card, int position) {
        fullSizeCard = new FullSizeCard(this, card);
        fullSizeCard.setDimBackground(true);
        fullSizeCard.addOptionButton(getString(R.string.close), event -> {
            layout_explore_decks_frame.removeView(fullSizeCard);
            fullSizeCard = null;
        });
        fullSizeCard.setSwipeGestures(FullSizeCard.SWIPE_X_AXIS);
        fullSizeCard.setSwipeResultListener(new SwipeResultListener() {
            @Override
            public void onSwipeLeft() {
                int nextPos = (position + 1) % list_explore_decks_cards.getCount();
                fullSizeCard = null;
                layout_explore_decks_frame.addView(getFullSizeCardInstance((Card) list_explore_decks_cards.getItemAtPosition(nextPos), nextPos));
            }

            @Override
            public void onSwipeRight() {
                int nextPos = position - 1;
                if (nextPos < 0) {
                    nextPos = list_explore_decks_cards.getCount() - 1;
                }
                fullSizeCard = null;
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
        RelativeLayout layout_explore_decks_navigation = findViewById(R.id.layout_explore_decks_navigation);
        btn_explore_decks_back = findViewById(R.id.btn_explore_decks_back);
        btn_explore_decks_select = findViewById(R.id.btn_explore_decks_select);
        btn_explore_decks_add = findViewById(R.id.btn_explore_decks_add);
        RelativeLayout layout_explore_decks_bottom = findViewById(R.id.layout_explore_decks_bottom);
        list_explore_decks_cards = findViewById(R.id.list_explore_decks_cards);
        btn_explore_decks_white_cards = findViewById(R.id.btn_explore_decks_white_cards);
        RelativeLayout overlay_explore_decks_white_cards_selected = findViewById(R.id.overlay_explore_decks_white_cards_selected);
        btn_explore_decks_black_cards = findViewById(R.id.btn_explore_decks_black_cards);
        RelativeLayout overlay_explore_decks_black_cards_selected = findViewById(R.id.overlay_explore_decks_black_cards_selected);
        img_explore_decks_black_cards_selected_icon = findViewById(R.id.img_explore_decks_black_cards_selected_icon);
        img_explore_decks_white_cards_selected_icon = findViewById(R.id.img_explore_decks_white_cards_selected_icon);
        label_explore_decks_name = findViewById(R.id.label_explore_decks_name);

        cardListAdapter = new CardListAdapter(this, new ArrayList<>());
        list_explore_decks_cards.setAdapter(cardListAdapter);

    }

    private void updateCardList() {
        cardListAdapter.clear();
        if (selectedDeck != null) {
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

    private void setDisplayCardMode(boolean white, boolean black) {
        selectedBlackCards = black;
        selectedWhiteCards = white;
        if (white) {
            img_explore_decks_white_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_tick_circle_24dp));
            DrawableCompat.setTint(img_explore_decks_white_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_green));
        } else {
            img_explore_decks_white_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_x_circle_24dp));
            DrawableCompat.setTint(img_explore_decks_white_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_red));
        }
        if (black) {
            img_explore_decks_black_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_tick_circle_24dp));
            DrawableCompat.setTint(img_explore_decks_black_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_green));
        } else {
            img_explore_decks_black_cards_selected_icon.setImageDrawable(getDrawable(R.drawable.vector_x_circle_24dp));
            DrawableCompat.setTint(img_explore_decks_black_cards_selected_icon.getDrawable(), ContextCompat.getColor(this, R.color.pastel_red));
        }
        updateCardList();
    }


    public void editTextMode(EditText o, boolean state) {
        o.setClickable(state);
        o.setLongClickable(state);
        o.setLinksClickable(state);
        o.setFocusable(state);
        o.setFocusableInTouchMode(state);
        o.setEnabled(state);
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
