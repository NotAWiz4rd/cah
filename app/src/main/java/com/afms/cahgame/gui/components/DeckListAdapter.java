package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Deck;

import java.util.ArrayList;
import java.util.Objects;

public class DeckListAdapter extends ArrayAdapter<Deck> {

    public DeckListAdapter(@NonNull Context context, ArrayList<Deck> decks) {
        super(context, 0, decks);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Deck deck = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_deckselector, parent, false);
        }

        TextView item_deckselector_name = convertView.findViewById(R.id.item_deckselector_name);
        TextView item_deckselector_countWhite = convertView.findViewById(R.id.item_deckselector_countWhite);
        TextView item_deckselector_countBlack = convertView.findViewById(R.id.item_deckselector_countBlack);

        item_deckselector_name.setText(Objects.requireNonNull(deck).getName());
        item_deckselector_countBlack.setText(String.valueOf(deck.getBlackCards().size()));
        item_deckselector_countWhite.setText(String.valueOf(deck.getWhiteCards().size()));

        return convertView;
    }
}
