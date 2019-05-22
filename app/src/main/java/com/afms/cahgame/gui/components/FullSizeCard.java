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

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FullSizeCard extends ConstraintLayout {

    public static final int SWIPE_DISABLE = 0;
    public static final int SWIPE_ALL_DIRECTION = 1;
    public static final int SWIPE_Y_AXIS = 2;
    public static final int SWIPE_X_AXIS = 3;
    public static final int SWIPE_LEFT = 4;
    public static final int SWIPE_RIGHT = 5;
    public static final int SWIPE_UP = 6;
    public static final int SWIPE_DOWN = 7;


    private EditText fullSizeCardText;
    private LinearLayout fullSizeCardLayout;
    private LinearLayout fullSizeCardOptionLayout;
    private ConstraintLayout constraintLayout;
    private TextView fullSizeGameName;
    private Card card;
    private float downPosY;
    private float downPosX;
    private float movedPosY;
    private float movedPosX;
    private float viewPosDownX;
    private float viewPosDownY;
    private double yAxisHeightFactor = 0.25;
    private double xAxisWidthFactor = 0.3;
    private SwipeResultListener swipeResultListener;
    private List<Integer> swipeStates;

    public void setSwipeResultListener(SwipeResultListener swipeResultListener) {
        this.swipeResultListener = swipeResultListener;
    }

    public void setDimBackground(boolean value) {
        if (value) {
            constraintLayout.setBackgroundColor(Color.parseColor("#8D000000"));
        } else {
            constraintLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void addOptionButton(String label, OnClickListener onClickListener) {
        Button btn = (Button) LayoutInflater.from(getContext()).inflate(R.layout.dialog_selector_button, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(6, 6, 6, 6);
        btn.setLayoutParams(params);
        btn.setText(label);
        btn.setOnClickListener(event -> {
            onClickListener.onClick(btn);
        });
        fullSizeCardOptionLayout.addView(btn);
    }

    @SuppressLint("ClickableViewAccessibility")
    public FullSizeCard(Context context, Card card) {
        super(context);
        this.card = card;
        swipeStates = new ArrayList<>();
        swipeStates.add(SWIPE_DISABLE);

        LayoutInflater.from(context).inflate(R.layout.element_fullsize_card_buttonbar, this);
        setOnClickListener(v -> {
        });
        constraintLayout = findViewById(R.id.optionsBackground);
        fullSizeCardOptionLayout = findViewById(R.id.optionLayout);
        fullSizeCardLayout = findViewById(R.id.fullSizeCardLayout);
        fullSizeCardLayout.setBackgroundResource(card.getColour() == Colour.WHITE ? R.drawable.bg_card_white_radius_20dp : R.drawable.bg_card_black_radius_20dp);
        fullSizeCardText = findViewById(R.id.fullSizeCardText);
        fullSizeCardText.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);
        fullSizeCardText.setText(card.getText());
        fullSizeGameName = findViewById(R.id.fullSizeGameName);
        fullSizeGameName.setTextColor(card.getColour() == Colour.WHITE ? ContextCompat.getColor(context, R.color.cardTextColorWhite) : Color.WHITE);

        fullSizeCardLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
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
        );

        editTextMode(fullSizeCardText, false);

        fullSizeCardLayout.setOnTouchListener((v, event) -> {
            if (!swipeStates.contains(SWIPE_DISABLE)) {
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
                        if (v.getY() > (viewPosDownY - (v.getHeight() * yAxisHeightFactor)) &&
                                v.getY() < (viewPosDownY + (v.getHeight() * yAxisHeightFactor)) &&
                                v.getX() > (viewPosDownX - (v.getWidth() * xAxisWidthFactor)) &&
                                v.getX() < (viewPosDownX + (v.getWidth() * xAxisWidthFactor))) {
                            v.setY(viewPosDownY);
                            v.setX(viewPosDownX);
                        } else if (v.getY() < (viewPosDownY - (v.getHeight() * yAxisHeightFactor))) {
                            generateAnimation(v, "translationY", -v.getHeight(), (int) (v.getHeight() * yAxisHeightFactor), SWIPE_UP);
                        } else if (v.getY() > (viewPosDownY + (v.getHeight() * yAxisHeightFactor))) {
                            generateAnimation(v, "translationY", v.getHeight(), (int) (v.getHeight() * yAxisHeightFactor), SWIPE_DOWN);
                        } else if (v.getX() < (viewPosDownX - (v.getWidth() * xAxisWidthFactor))) {
                            generateAnimation(v, "translationX", -v.getWidth(), (int) (v.getWidth() * xAxisWidthFactor), SWIPE_LEFT);
                        } else if (v.getX() > (viewPosDownX + (v.getWidth() * xAxisWidthFactor))) {
                            generateAnimation(v, "translationX", v.getWidth(), (int) (v.getWidth() * xAxisWidthFactor), SWIPE_RIGHT);
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
                v.setX(viewPosDownX);
                v.setY(viewPosDownY);
                if (swipeResultListener != null) {
                    switch (direction) {
                        case SWIPE_UP:
                            swipeResultListener.onSwipeUp();
                            break;
                        case SWIPE_DOWN:
                            swipeResultListener.onSwipeDown();
                            break;
                        case SWIPE_LEFT:
                            swipeResultListener.onSwipeLeft();
                            break;
                        case SWIPE_RIGHT:
                            swipeResultListener.onSwipeRight();
                            break;
                    }
                }
            }
        });
        animation.start();
    }

    public void doSwipe(int swipe){
        viewPosDownX = fullSizeCardLayout.getX();
        viewPosDownY = fullSizeCardLayout.getY();
        LinearLayout v = fullSizeCardLayout;
        switch (swipe){
            case SWIPE_UP:
                generateAnimation(v, "translationY", -v.getHeight(), (int) (v.getHeight() * yAxisHeightFactor), SWIPE_UP);
                break;
            case SWIPE_DOWN:
                generateAnimation(v, "translationY", v.getHeight(), (int) (v.getHeight() * yAxisHeightFactor), SWIPE_DOWN);
                break;
            case SWIPE_LEFT:
                generateAnimation(v, "translationX", -v.getWidth(), (int) (v.getWidth() * xAxisWidthFactor), SWIPE_LEFT);
                break;
            case SWIPE_RIGHT:
                generateAnimation(v, "translationX", v.getWidth(), (int) (v.getWidth() * xAxisWidthFactor), SWIPE_RIGHT);
                break;
        }
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
