package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Arrays;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class LobbyListAdapter extends ArrayAdapter<Lobby> {
    private SharedPreferences settings;
    private ResultListener resultListener;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_lobby, parent, false);
        }

        ImageView item_lobby_select_lock = convertView.findViewById(R.id.item_lobby_select_lock);
        TextView item_lobby_select_count_handcard = convertView.findViewById(R.id.item_lobby_select_count_handcard);
        TextView item_lobby_select_count_maxplayer = convertView.findViewById(R.id.item_lobby_select_count_maxplayer);
        TextView item_lobby_select_host = convertView.findViewById(R.id.item_lobby_select_host);
        TextView item_lobby_select_name = convertView.findViewById(R.id.item_lobby_select_name);
        TextView item_lobby_select_deck = convertView.findViewById(R.id.item_lobby_select_deck);
        Button btn_item_lobby_select_join = convertView.findViewById(R.id.btn_item_lobby_select_join);

        if (lobby != null && lobby.getPassword().equals("")) {
            item_lobby_select_lock.setVisibility(View.INVISIBLE);
        }

        item_lobby_select_name.setText(String.format("%s %s", getContext().getResources().getString(R.string.label_name), Objects.requireNonNull(lobby).getId()));
        item_lobby_select_host.setText(String.format("%s %s", getContext().getResources().getString(R.string.host), lobby.getHost()));
        item_lobby_select_deck.setText(String.format("%s %s", getContext().getResources().getString(R.string.label_deck), lobby.getDeckName()));
        item_lobby_select_count_handcard.setText(String.valueOf(lobby.getHandcardCount()));
        int currentPlayerCount = lobby.getPlayers().size();
        item_lobby_select_count_maxplayer.setText(String.format("%s / %s", currentPlayerCount, String.valueOf(lobby.getMaxPlayers())));

        btn_item_lobby_select_join.setOnClickListener(e -> {
            if (Util.godMode) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                final MessageDialog[] messageDialog = {MessageDialog.create(getContext().getString(R.string.label_choose_action), new ArrayList<>(Arrays.asList("Join", "Delete", "Cancel")))};
                resultListener = result -> {
                    if (result.equals("Delete")) {
                        Database.removeLobby(lobby.getId());
                        Toast.makeText(getContext(), String.format("Deleted lobby: %s", lobby.getId()), Toast.LENGTH_SHORT).show();
                    } else if (result.equals("Join")){
                        String playerName = settings.getString("player", Util.getRandomName());
                        Util.saveName(settings, playerName);

                        Intent intent = new Intent(getContext(), WaitingLobby.class);
                        intent.putExtra(getContext().getString(R.string.lobbyId), lobby.getId());
                        getContext().startActivity(intent);
                        ((AppCompatActivity) getContext()).finish();
                    }
                };
                messageDialog[0].setResultListener(resultListener);
                messageDialog[0].show(fragmentManager, "godModeDialogLobby");
            } else {
                if (!lobby.getPassword().equals("")) {
                    requestPasswordDialog(lobby);
                } else {
                    String playerName = settings.getString("player", Util.getRandomName());
                    Util.saveName(settings, playerName);

                    Intent intent = new Intent(getContext(), WaitingLobby.class);
                    intent.putExtra(getContext().getString(R.string.lobbyId), lobby.getId());
                    getContext().startActivity(intent);
                    ((AppCompatActivity) getContext()).finish();
                }
            }
        });

        return convertView;
    }

    private void requestPasswordDialog(Lobby lobby){
        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        final PasswordDialog[] passwordDialog = {PasswordDialog.create(getContext(), new ArrayList<>(Arrays.asList("Join", "Cancel")))};
        resultListener = result -> {
            if (result.equals("Join")) {
                if (passwordDialog[0].getPassword().equals(lobby.getPassword())) {
                    String playerName = settings.getString("player", Util.getRandomName());
                    Util.saveName(settings, playerName);

                    Intent intent = new Intent(getContext(), WaitingLobby.class);
                    intent.putExtra(getContext().getString(R.string.lobbyId), lobby.getId());
                    getContext().startActivity(intent);
                    ((AppCompatActivity) getContext()).finish();
                } else {
                    fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag("passwordDialog"))).commit();
                    passwordDialog[0] = PasswordDialog.create(getContext().getResources().getString(R.string.title_password), getContext().getResources().getString(R.string.label_private_lobby_wrong), new ArrayList<>(Arrays.asList("Join", "Cancel")));
                    passwordDialog[0].setResultListener(resultListener);
                    passwordDialog[0].show(fragmentManager, "passwordDialog");
                }
            }
        };
        passwordDialog[0].setResultListener(resultListener);
        passwordDialog[0].show(fragmentManager, "passwordDialog");
    }
}
