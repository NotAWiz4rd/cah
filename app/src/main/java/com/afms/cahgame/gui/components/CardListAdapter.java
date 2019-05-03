package com.afms.cahgame.gui.components;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Colour;

import android.support.annotation.*;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardListAdapter extends ArrayAdapter<Card> {

    private Activity mainActivity;

    Map<Colour, HashMap<String, Integer>> colorMap = new HashMap<Colour, HashMap<String, Integer>>(){{
        put(Colour.BLACK, new HashMap<String, Integer>() {{
            put("icon", R.drawable.cardblack);
            put("background", R.drawable.card_background_small_black);
            put("textcolor", Color.WHITE);
        }});
        put(Colour.WHITE, new HashMap<String, Integer>() {{
            put("icon", R.drawable.card);
            put("background", R.drawable.card_background_small_white);
            put("textcolor", Color.BLACK);
        }});
    }};

    public CardListAdapter(@NonNull Context context, ArrayList<Card> cards) {
        super(context, 0, cards);
        this.mainActivity = (Activity) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Card card = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card_select, parent, false);
        }

        ImageView cardIcon = convertView.findViewById(R.id.cardIcon);
        cardIcon.setImageResource(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("icon"));

        ConstraintLayout cardLayout = convertView.findViewById(R.id.cardLayout);
        cardLayout.setBackgroundResource(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("background"));

        TextView cardText = convertView.findViewById(R.id.cardText);
        cardText.setTextColor(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("textcolor"));
        cardText.setText(card.getText());

        return convertView;
    }
}
