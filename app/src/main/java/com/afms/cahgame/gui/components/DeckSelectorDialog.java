package com.afms.cahgame.gui.components;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.util.Database;
import com.afms.cahgame.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class DeckSelectorDialog extends DialogFragment {

    private ResultListener resultListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView label_dialog_deckselector_title = view.findViewById(R.id.label_dialog_deckselector_title);
        ListView list_dialog_deckselector = view.findViewById(R.id.list_dialog_deckselector);

        label_dialog_deckselector_title.setText(Objects.requireNonNull(getArguments()).getString("title"));
        DeckListAdapter deckListAdapter = new DeckListAdapter(Objects.requireNonNull(getContext()), new ArrayList<>());
        deckListAdapter.addAll(Database.getDecks().stream().map(Util::convertDataDeckToPlayDeck).collect(Collectors.toList()));
        list_dialog_deckselector.setAdapter(deckListAdapter);
        list_dialog_deckselector.setOnItemClickListener((parent, view1, position, id) -> {
            String deckName = ((TextView) view1.findViewById(R.id.item_deckselector_name)).getText().toString();
            if (Util.godMode) {
                MessageDialog messageDialog = MessageDialog.create(
                        getContext().getString(R.string.label_choose_action),
                        new ArrayList<>(Arrays.asList(getString(R.string.select), getString(R.string.delete), getString(R.string.cancel)))
                );
                messageDialog.setResultListener(result -> {
                    if (result.equals(getString(R.string.select))) {
                        resultListener.onItemClick(deckName);
                        dismiss();
                    } else if (result.equals(getString(R.string.delete))) {
                        Database.removeDeck(deckName);
                        Toast.makeText(getContext(), String.format(getString(R.string.deletedDeckMessage), deckName), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
                messageDialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "godmodeDeck");
            } else {
                resultListener.onItemClick(deckName);
                dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_deckselector, container);
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


    public static DeckSelectorDialog create(String title) {
        DeckSelectorDialog deckSelectorDialog = new DeckSelectorDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        deckSelectorDialog.setArguments(args);
        return deckSelectorDialog;
    }


    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }
}
