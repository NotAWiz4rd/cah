package com.afms.cahgame.gui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
    private float downPos;
    private float movedPos;
    private float oldPos;
    private float newPos;

    private Activity mainActivity;

    @SuppressLint("ClickableViewAccessibility")
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
        fullSizeCardButton.setOnClickListener(v -> {
            ((ViewManager) getParent()).removeView(this);
            fullSizeCardLayout.setY(oldPos);
        });
        fullSizeCardButton2 = findViewById(R.id.fullSizeOptionButton2);
        ((ViewManager) fullSizeCardButton2.getParent()).removeView(fullSizeCardButton2);

        oldPos = fullSizeCardLayout.getY();

        // Observer
        final Observer<String> fullSizeCardTextObserver = fullSizeCardText::setText;
        card.getText().observe((LifecycleOwner) mainActivity, fullSizeCardTextObserver);

        editTextMode(fullSizeCardText, false);

        fullSizeCardLayout.setOnTouchListener((v, event) -> {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downPos = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    movedPos = event.getY();
                    newPos = v.getY()+ movedPos-downPos;
                    v.setY(newPos);
                    break;
                case MotionEvent.ACTION_UP:
                    if(v.getY() > -500){
                        v.setY(oldPos);
                    }else{
                        ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationY", -2800f);
                        animation.setDuration(400);
                        animation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ((ViewManager) v.getParent().getParent().getParent()).removeView(((View)v.getParent().getParent()));
                                v.setY(oldPos);
                            }
                        });
                        animation.start();
                    }
                    break;
            }
            return false;
        });
    }

    public Card getCard() {
        return card;
    }

    public void editTextMode(EditText o, boolean state){
        o.setClickable(state);
        o.setLongClickable(state);
        o.setLinksClickable(state);
        o.setFocusable(state);
        o.setFocusableInTouchMode(state);
        o.setEnabled(state);
    }

}
