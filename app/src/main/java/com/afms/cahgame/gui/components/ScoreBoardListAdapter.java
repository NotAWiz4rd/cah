package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Player;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ScoreBoardListAdapter extends ArrayAdapter<Player> {

    private Player roundWinner;
    private SharedPreferences settings;

    public ScoreBoardListAdapter(@NonNull Context context, ArrayList<Player> players, Player roundWin) {
        super(context, 0, players);
        roundWinner = roundWin;
    }

    public ScoreBoardListAdapter(@NonNull Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Player player = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_game_player, parent, false);
        }

        TextView playerName = convertView.findViewById(R.id.score_board_player_name);
        TextView playerScore = convertView.findViewById(R.id.score_board_player_score);

        settings = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        if (player.getName().equals(settings.getString("player", ""))) {
            playerName.setTypeface(ResourcesCompat.getFont(getContext(), R.font.helveticaneuebold));
        } else {
            playerName.setTypeface(ResourcesCompat.getFont(getContext(), R.font.helveticaneuelight));
        }

        if(roundWinner != null && roundWinner.getName().equals(player.getName())){
            playerName.setTextColor(getContext().getResources().getColor(R.color.pastel_green));
        }else {
            playerName.setTextColor(getContext().getResources().getColor(R.color.inputTextColorBlack));
        }
        playerName.setText(player.getName());
        playerScore.setText(String.valueOf(player.getScore()));

        convertView.setEnabled(false);
        convertView.setOnClickListener(null);

        return convertView;
    }
}
