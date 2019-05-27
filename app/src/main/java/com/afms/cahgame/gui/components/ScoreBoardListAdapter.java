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
import android.widget.ImageView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Player;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ScoreBoardListAdapter extends ArrayAdapter<Player> {

    private Player roundWinner;
    private Game game;

    public ScoreBoardListAdapter(@NonNull Context context, ArrayList<Player> players, Player roundWin, Game game) {
        super(context, 0, players);
        roundWinner = roundWin;
        this.game = game;
    }

    public ScoreBoardListAdapter(@NonNull Context context, ArrayList<Player> players, Game game) {
        super(context, 0, players);
        this.game = game;
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
        TextView playedCard = convertView.findViewById(R.id.score_board_card_text);
        ImageView scoreIcon = convertView.findViewById(R.id.score_board_score_icon);
        ImageView icon = convertView.findViewById(R.id.score_board_player_icon);

        SharedPreferences settings = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        if (player != null) {
            if (player.getName().equals(settings.getString("player", ""))) {
                playerName.setTypeface(ResourcesCompat.getFont(getContext(), R.font.helveticaneuebold));
            } else {
                playerName.setTypeface(ResourcesCompat.getFont(getContext(), R.font.helveticaneuelight));
            }
            if(roundWinner != null){
                List<Card> playedCardList = game.getRoundEndPlayedCards();
                for (Card card: playedCardList) {
                    if(card.getOwner().getName().equals(player.getName())){
                        playedCard.setText(card.getText());
                    }
                }
                if(playedCardList.stream().map(Card::getOwner).noneMatch(p -> p.getName().equals(player.getName()))){
                    if(game.getCardCzar().equals(player.getName())){
                        playedCard.setText(getContext().getString(R.string.last_round_czar));
                    } else {
                        playedCard.setText(getContext().getString(R.string.no_played_card));
                    }
                }
            }
            if (roundWinner != null && roundWinner.getName().equals(player.getName())) {
                playerName.setTextColor(getContext().getColor(R.color.pastel_green_darker));
                scoreIcon.setBackground(getContext().getDrawable(R.drawable.img_score));
            } else {
                playerName.setTextColor(getContext().getColor(R.color.inputTextColorBlack));
                scoreIcon.setBackground(null);
            }

            if(game.getCardCzar().equals(player.getName())){
                icon.setBackground(getContext().getDrawable(R.drawable.vector_eye_black_24dp));
            }
            playerName.setText(player.getName());
            playerScore.setText(String.valueOf(player.getScore()));
        }

        convertView.setEnabled(false);
        convertView.setOnClickListener(null);

        return convertView;
    }
}
