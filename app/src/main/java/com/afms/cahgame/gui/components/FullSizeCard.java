package com.afms.cahgame.gui.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;

public class FullSizeCard extends ConstraintLayout {

    private float dx;
    private float dy;

    private Activity mainActivity;

    public FullSizeCard(Context context, Card card) {
        super(context);
        mainActivity = (Activity) context;
        LayoutInflater.from(context).inflate(R.layout.fullsize_card_options, this);
        setOnClickListener(v -> {});
        LinearLayout fullSizeCardLayout = findViewById(R.id.fullSizeCardLayout);
        fullSizeCardLayout.setBackgroundResource(card.getColour() == Colour.WHITE ? R.drawable.card_background_white : R.drawable.card_background_black);
        TextView fullSizeCardText = findViewById(R.id.fullSizeCardText);
        fullSizeCardText.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardText.setText(card.getText());
        TextView fullSizeGameName = findViewById(R.id.fullSizeGameName);
        fullSizeGameName.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        Button fullSizeCardButton = findViewById(R.id.fullSizeOptionButton);
        fullSizeCardButton.setText(context.getString(R.string.close));
        fullSizeCardButton.setOnClickListener(v -> ((ViewManager) getParent()).removeView(this));
        Button fullSizeCardButton2 = findViewById(R.id.fullSizeOptionButton2);
        ((ViewManager) fullSizeCardButton2.getParent()).removeView(fullSizeCardButton2);
    }
}
