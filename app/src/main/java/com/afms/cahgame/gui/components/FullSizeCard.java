package com.afms.cahgame.gui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;
import com.afms.cahgame.gui.controller.MainController;

public class FullSizeCard extends ConstraintLayout {

    private EditText fullSizeCardText;
    private LinearLayout fullSizeCardLayout;
    private TextView fullSizeGameName;
    private Button fullSizeCardButton;
    private Button fullSizeCardButton2;
    private Card card;
    private float downPosY;
    private float downPosX;
    private float movedPosY;
    private float movedPosX;
    private int[] oldLocation = new int[2];
    private boolean switchedCard = true;

    private Activity mainActivity;
    private MainController mainController;

    @SuppressLint("ClickableViewAccessibility")
    public FullSizeCard(Context context, MainController mainController, Card card) {
        super(context);
        this.card = card;
        mainActivity = (Activity) context;
        this.mainController = mainController;
        LayoutInflater.from(context).inflate(R.layout.fullsize_card_options, this);
        setOnClickListener(v -> {
        });
        fullSizeCardLayout = findViewById(R.id.fullSizeCardLayout);
        fullSizeCardLayout.setBackgroundResource(card.getColour() == Colour.WHITE ? R.drawable.card_background_white : R.drawable.card_background_black);
        fullSizeCardText = findViewById(R.id.fullSizeCardText);
        fullSizeCardText.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardText.setText(card.getText());
        fullSizeGameName = findViewById(R.id.fullSizeGameName);
        fullSizeGameName.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardButton = findViewById(R.id.fullSizeOptionButton);
        fullSizeCardButton.setText(context.getString(R.string.close));
        fullSizeCardButton.setOnClickListener(v -> {
            ((ViewManager) getParent()).removeView(this);
        });
        fullSizeCardButton2 = findViewById(R.id.fullSizeOptionButton2);
        ((ViewManager) fullSizeCardButton2.getParent()).removeView(fullSizeCardButton2);

        fullSizeCardLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    //Remove the listener before proceeding
                    //fullSizeCardLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    fullSizeCardLayout.getLocationOnScreen(oldLocation);
                    if (switchedCard) {
                        fullSizeCardLayout.setScaleX(0.1f);
                        fullSizeCardLayout.setScaleY(0.1f);
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(fullSizeCardLayout, "scaleX", 1f);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(fullSizeCardLayout, "scaleY", 1f);
                        scaleUpX.setDuration(700);
                        scaleUpY.setDuration(700);
                        AnimatorSet scaleUp = new AnimatorSet();
                        scaleUp.play(scaleUpX).with(scaleUpY);
                        scaleUp.start();
                    }
                }
        );

        editTextMode(fullSizeCardText, false);

        fullSizeCardLayout.setOnTouchListener((v, event) -> {
            switchedCard = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downPosY = event.getY();
                    downPosX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    movedPosY = event.getY();
                    movedPosX = event.getX();
                    v.setY(v.getY() + movedPosY - downPosY);
                    v.setX(v.getX() + movedPosX - downPosX);
                    break;
                case MotionEvent.ACTION_UP:
                    if (v.getY() > -500 && v.getX() < 300 && v.getX() > -200) {
                        v.setY(oldLocation[1]);
                        v.setX(oldLocation[0]);
                    } else if (v.getX() > 300) {
                        generateAnimation(v, "translationX", 1000f, 300, mainController, 1);
                    } else if (v.getX() < -200) {
                        generateAnimation(v, "translationX", -1000f, 300, mainController, 2);
                    } else {
                        generateAnimation(v, "translationY", -1800f, 400, mainController, 0);
                    }
                    break;
            }
            return false;
        });
    }

    private void generateAnimation(View v, String translation, float value, int duration, MainController finalMainController, int prevOrNext) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, translation, value);
        animation.setDuration(duration);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ((ViewManager) v.getParent().getParent().getParent()).removeView(((View) v.getParent().getParent()));
                v.setX(oldLocation[0]);
                v.setY(oldLocation[1]);
                switch (prevOrNext) {
                    case 1:
                        finalMainController.showPreviousViewFromList();
                        break;
                    case 2:
                        finalMainController.showNextViewFromList();
                        break;
                }
                switchedCard = true;
            }
        });
        animation.start();
    }

    public Card getCard() {
        return card;
    }

    public void editTextMode(EditText o, boolean state) {
        o.setClickable(state);
        o.setLongClickable(state);
        o.setLinksClickable(state);
        o.setFocusable(state);
        o.setFocusableInTouchMode(state);
        o.setEnabled(state);
    }


}
