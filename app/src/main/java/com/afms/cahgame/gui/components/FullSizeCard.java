package com.afms.cahgame.gui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

public class FullSizeCard extends ConstraintLayout {

    public static int SWIPE_DISABLE = 0;
    public static int SWIPE_ALL_DIRECTION = 1;
    public static int SWIPE_Y_AXIS = 2;
    public static int SWIPE_X_AXIS = 3;
    public static int SWIPE_LEFT = 4;
    public static int SWIPE_RIGHT = 5;
    public static int SWIPE_UP = 6;
    public static int SWIPE_DOWN = 7;


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
    private float viewPosDownX;
    private float viewPosDownY;
    private int[] oldLocation = new int[2];
    private boolean switchedCard = true;
    private SwipeResultListener swipeResultListener;
    private List<Integer> swipeStates;

    public void setSwipeResultListener(SwipeResultListener swipeResultListener) {
        this.swipeResultListener = swipeResultListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public FullSizeCard(Context context, Card card) {
        super(context);
        this.card = card;
        swipeStates = new ArrayList<>();
        swipeStates.add(SWIPE_DISABLE);

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
            if (!swipeStates.contains(SWIPE_DISABLE)) {
                switchedCard = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downPosY = event.getY();
                        downPosX = event.getX();
                        viewPosDownX = v.getX();
                        viewPosDownY = v.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        movedPosY = event.getY();
                        movedPosX = event.getX();
                        if (swipeStates.contains(SWIPE_ALL_DIRECTION) || swipeStates.contains(SWIPE_Y_AXIS)) {
                            v.setY(v.getY() + movedPosY - downPosY);
                        } else if (swipeStates.contains(SWIPE_UP)) {
                            v.setY(min(viewPosDownY, v.getY() + movedPosY - downPosY));
                        } else if (swipeStates.contains(SWIPE_DOWN)) {
                            v.setY(max(viewPosDownY, v.getY() + movedPosY - downPosY));
                        }
                        if (swipeStates.contains(SWIPE_ALL_DIRECTION) || swipeStates.contains(SWIPE_X_AXIS)) {
                            v.setX(v.getX() + movedPosX - downPosX);
                        } else if (swipeStates.contains(SWIPE_LEFT)) {
                            v.setX(min(viewPosDownX, v.getX() + movedPosX - downPosX));
                        } else if (swipeStates.contains(SWIPE_RIGHT)) {
                            v.setX(max(viewPosDownX, v.getX() + movedPosX - downPosX));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getY() > (viewPosDownY - (v.getHeight() * 0.25)) &&
                                v.getY() < (viewPosDownY + (v.getHeight() * 0.25)) &&
                                v.getX() > (viewPosDownX - (v.getWidth() * 0.3)) &&
                                v.getX() < (viewPosDownX + (v.getWidth() * 0.3))) {
                            v.setY(viewPosDownY);
                            v.setX(viewPosDownX);
                        } else if (v.getY() < (viewPosDownY - (v.getHeight() * 0.25))){
                            generateAnimation(v, "translationY", -v.getHeight(), (int)(v.getHeight() * 0.25), 0);
                        } else if (v.getY() > (viewPosDownY + (v.getHeight() * 0.25))){
                            generateAnimation(v, "translationY", v.getHeight(), (int)(v.getHeight() * 0.25), 1);
                        } else if (v.getX() < (viewPosDownX - (v.getWidth() * 0.3))){
                            generateAnimation(v, "translationX", -v.getWidth(), (int)(v.getWidth() * 0.3), 2);
                        } else if (v.getX() > (viewPosDownX + (v.getWidth() * 0.3))){
                            generateAnimation(v, "translationX", v.getWidth(), (int)(v.getWidth() * 0.3), 3);
                        }
                        break;
                }
            }
            return false;
        });
    }

    public void setSwipeGestures(int... states) {
        swipeStates.clear();
        for (Integer state : states) {
            swipeStates.add(state);
        }
    }

    private void generateAnimation(View v, String translation, float value, int duration, int direction) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, translation, value);
        animation.setDuration(duration);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ((ViewManager) v.getParent().getParent().getParent()).removeView(((View) v.getParent().getParent()));
                v.setX(oldLocation[0]);
                v.setY(oldLocation[1]);
                if (swipeResultListener != null) {
                    switch (direction) {
                        case 0:
                            swipeResultListener.onSwipeUp();
                            break;
                        case 1:
                            swipeResultListener.onSwipeDown();
                            break;
                        case 2:
                            swipeResultListener.onSwipeLeft();
                            break;
                        case 3:
                            swipeResultListener.onSwipeRight();
                            break;
                    }
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
