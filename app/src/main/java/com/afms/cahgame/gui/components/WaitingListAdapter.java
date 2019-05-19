package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Lobby;

import java.util.ArrayList;

public class WaitingListAdapter extends ArrayAdapter<String> {

    private Lobby currentLobby;

    public WaitingListAdapter(@NonNull Context context, ArrayList<String> players, Lobby lobby) {
        super(context, 0, players);
        currentLobby = lobby;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String player = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_waiting_players, parent, false);
        }

        ImageView img_item_waiting_players_host = convertView.findViewById(R.id.img_item_waiting_players_host);
        TextView label_item_waiting_players_name = convertView.findViewById(R.id.label_item_waiting_players_name);
        TextView label_item_waiting_players_ready = convertView.findViewById(R.id.label_item_waiting_players_ready);

        label_item_waiting_players_name.setText(player);

        if (player.equals(currentLobby.getHost())) {
            img_item_waiting_players_host.setVisibility(View.VISIBLE);
        } else {
            img_item_waiting_players_host.setVisibility(View.INVISIBLE);
        }

        // todo this is not intended for in-lobby use
//        if (player.isReady()) {
//            label_item_waiting_players_ready.setText(getContext().getResources().getString(R.string.label_ready));
//            label_item_waiting_players_ready.setBackgroundResource(R.drawable.pill_ready);
//        } else {
//            label_item_waiting_players_ready.setText(getContext().getResources().getString(R.string.label_not_ready));
//            label_item_waiting_players_ready.setBackgroundResource(R.drawable.pill_not_ready);
//        }

        return convertView;
    }
}
