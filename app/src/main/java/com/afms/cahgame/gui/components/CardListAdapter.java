package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardListAdapter extends ArrayAdapter<Card> {

    private Activity mainActivity;

    private Map<Colour, HashMap<String, Integer>> colorMap = new HashMap<Colour, HashMap<String, Integer>>() {{
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

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Card card = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card_select, parent, false);
        }

        ImageView cardIcon = convertView.findViewById(R.id.cardIcon);
        cardIcon.setImageResource(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("icon"));

        convertView.setBackgroundResource(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("background"));

        TextView cardText = convertView.findViewById(R.id.cardText);
        cardText.setTextColor(colorMap.getOrDefault(card.getColour(), colorMap.get(Colour.WHITE)).get("textcolor"));
        cardText.setText(card.getText());

        return convertView;
    }
}
