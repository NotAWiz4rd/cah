package com.afms.cahgame.gui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnimationStack {

    private View view;
    private List<Map<String, Object>> animationList = new ArrayList<>();
    private int currentIndex;

    public AnimationStack(View view) {
        this.view = view;
        currentIndex = -1;
    }

    public void addAnimation(int index, String propertyName, float value, int duration) {
        animationList.add(new HashMap<String, Object>() {{
            put("index", index);
            put("property", propertyName);
            put("value", value);
            put("duration", duration);
        }});
    }

    public void animate() {
        if (currentIndex < animationList.size()) {
            currentIndex++;
            playAnimation(currentIndex);
        }
    }

    private void playAnimation(int index) {
        List<Map<String, Object>> animationsAtIndex = animationList.stream().filter(o -> (int) o.get("index") == index).collect(Collectors.toList());
        AnimatorSet animatorSet = new AnimatorSet();
        final AnimatorSet.Builder[] builder = new AnimatorSet.Builder[1];
        final boolean[] firstAnimation = {true};
        animationsAtIndex.forEach(animation -> {
            if (firstAnimation[0]) {
                builder[0] = animatorSet.play(ObjectAnimator.ofFloat(
                        view,
                        (String) animation.get("property"),
                        Float.parseFloat((String) Objects.requireNonNull(animation.get("value"))))
                        .setDuration((int) animation.get("duration")));
                firstAnimation[0] = !firstAnimation[0];
            } else {
                builder[0].with(ObjectAnimator.ofFloat(
                        view,
                        (String) animation.get("property"),
                        Float.parseFloat((String) Objects.requireNonNull(animation.get("value"))))
                        .setDuration((int) animation.get("duration")));
            }
        });
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animate();
            }
        });
        animatorSet.start();
    }

    public void clear() {
        animationList.clear();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
