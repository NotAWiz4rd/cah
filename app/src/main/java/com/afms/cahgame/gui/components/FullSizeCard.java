package com.afms.cahgame.gui.components;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;

public class FullSizeCard extends ConstraintLayout {

    private EditText fullSizeCardText;
    private LinearLayout fullSizeCardLayout;
    private TextView fullSizeGameName;
    private Button fullSizeCardButton;
    private Button fullSizeCardButton2;
    private Card card;

    private Activity mainActivity;

    public FullSizeCard(Context context, Card card) {
        super(context);
        this.card = card;
        mainActivity = (Activity) context;
        LayoutInflater.from(context).inflate(R.layout.fullsize_card_options, this);
        setOnClickListener(v -> {
        });
        fullSizeCardLayout = findViewById(R.id.fullSizeCardLayout);
        fullSizeCardLayout.setBackgroundResource(card.getColour() == Colour.WHITE ? R.drawable.card_background_white : R.drawable.card_background_black);
        fullSizeCardText = findViewById(R.id.fullSizeCardText);
        fullSizeCardText.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardText.setText(card.getText().getValue());
        fullSizeGameName = findViewById(R.id.fullSizeGameName);
        fullSizeGameName.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardButton = findViewById(R.id.fullSizeOptionButton);
        fullSizeCardButton.setText(context.getString(R.string.close));
        fullSizeCardButton.setOnClickListener(v -> ((ViewManager) getParent()).removeView(this));
        fullSizeCardButton2 = findViewById(R.id.fullSizeOptionButton2);
        ((ViewManager) fullSizeCardButton2.getParent()).removeView(fullSizeCardButton2);

        // Observer
        final Observer<String> fullSizeCardTextObserver = fullSizeCardText::setText;
        card.getText().observe((LifecycleOwner) mainActivity, fullSizeCardTextObserver);
    }

    public Card getCard() {
        return card;
    }
}
