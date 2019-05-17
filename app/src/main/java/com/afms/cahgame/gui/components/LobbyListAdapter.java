package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.game.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LobbyListAdapter extends ArrayAdapter<Lobby> {

    public LobbyListAdapter(@NonNull Context context, ArrayList<Lobby> lobbies) {
        super(context, 0, lobbies);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lobby lobby = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lobby_select, parent, false);
        }

        ImageView item_lobby_select_lock = convertView.findViewById(R.id.item_lobby_select_lock);
        TextView item_lobby_select_count_handcard = convertView.findViewById(R.id.item_lobby_select_count_handcard);
        TextView item_lobby_select_count_maxplayer = convertView.findViewById(R.id.item_lobby_select_count_maxplayer);
        TextView item_lobby_select_host = convertView.findViewById(R.id.item_lobby_select_host);
        TextView item_lobby_select_name = convertView.findViewById(R.id.item_lobby_select_name);
        Button btn_item_lobby_select_join = convertView.findViewById(R.id.btn_item_lobby_select_join);

        if(lobby.getPassword().equals("")){
            item_lobby_select_lock.setBackground(null);
        }

        item_lobby_select_name.setText(lobby.getId());
        item_lobby_select_host.setText(lobby.getHost());
        item_lobby_select_count_handcard.setText(String.valueOf(lobby.getHandcardCount()));
        item_lobby_select_count_maxplayer.setText(String.valueOf(lobby.getMaxPlayers()));

        btn_item_lobby_select_join.setOnClickListener(e -> {
            Toast.makeText(getContext(), String.format("Join lobby with id: %s", lobby.getId()), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
