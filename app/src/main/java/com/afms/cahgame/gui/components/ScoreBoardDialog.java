package com.afms.cahgame.gui.components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.afms.cahgame.R;
import com.afms.cahgame.game.Game;
import com.afms.cahgame.game.Player;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreBoardDialog extends DialogFragment {

    private ListView playerList;
    private Button closeButton;
    private ResultListener resultListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerList = view.findViewById(R.id.score_board_list);
        Game game = null;
        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");
        }
        if (game != null && game.getPlayers() != null) {
            ScoreBoardListAdapter scoreBoardListAdapter;
            Player roundWin = (Player) getArguments().getSerializable("player");
            if (roundWin != null) {
                scoreBoardListAdapter = new ScoreBoardListAdapter(Objects.requireNonNull(getContext()), new ArrayList<>(game.getPlayers().values()), roundWin);
            } else {
                scoreBoardListAdapter = new ScoreBoardListAdapter(Objects.requireNonNull(getContext()), new ArrayList<>(game.getPlayers().values()));
            }
            playerList.setAdapter(scoreBoardListAdapter);
        }

        closeButton = view.findViewById(R.id.score_board_button_close);
        closeButton.setOnClickListener(event -> {
            getDialog().cancel();
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        resultListener.clearReference();
        super.onCancel(dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_score_board, container);
    }

    public static ScoreBoardDialog create(Game game) {
        ScoreBoardDialog scoreBoardDialog = new ScoreBoardDialog();
        Bundle args = new Bundle();
        args.putSerializable("game", game);
        scoreBoardDialog.setArguments(args);
        return scoreBoardDialog;
    }

    public static ScoreBoardDialog create(Game game, Player roundWinner) {
        ScoreBoardDialog scoreBoardDialog = new ScoreBoardDialog();
        Bundle args = new Bundle();
        args.putSerializable("game", game);
        args.putSerializable("player", roundWinner);
        scoreBoardDialog.setArguments(args);
        return scoreBoardDialog;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }
}

