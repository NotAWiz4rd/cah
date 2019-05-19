package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Lobby;
import com.afms.cahgame.gui.activities.WaitingLobby;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LobbyListAdapter extends ArrayAdapter<Lobby> {
    private SharedPreferences settings;

    public LobbyListAdapter(@NonNull Context context, ArrayList<Lobby> lobbies) {
        super(context, 0, lobbies);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        settings = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
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

        if (lobby != null && lobby.getPassword().equals("")) {
            item_lobby_select_lock.setVisibility(View.INVISIBLE);
        }

        item_lobby_select_name.setText(lobby.getId());
        item_lobby_select_host.setText(lobby.getHost());
        item_lobby_select_count_handcard.setText(String.valueOf(lobby.getHandcardCount()));
        int currentPlayerCount = lobby.getPlayers().size();
        item_lobby_select_count_maxplayer.setText(String.format("%s / %s", currentPlayerCount, String.valueOf(lobby.getMaxPlayers())));

        btn_item_lobby_select_join.setOnClickListener(e -> {
            boolean joinedSuccessfully = Database.joinLobby(lobby.getId(), settings.getString("player", Util.getRandomName(settings)));

            if (joinedSuccessfully) {
                Intent intent = new Intent(getContext(), WaitingLobby.class);
                intent.putExtra("lobbyId", lobby.getId());
                getContext().startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Wasn't able to join selected lobby", Toast.LENGTH_LONG).show();
            }

        });

        return convertView;
    }
}
