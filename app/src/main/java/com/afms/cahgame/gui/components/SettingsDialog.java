package com.afms.cahgame.gui.components;

import android.app.Dialog;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afms.cahgame.R;
import com.afms.cahgame.util.Util;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class SettingsDialog extends DialogFragment {

    private SharedPreferences settings;
    private EditText playerNameView;
    private View.OnClickListener onClickListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settings = Objects.requireNonNull(this.getActivity()).getSharedPreferences("Preferences", MODE_PRIVATE);

        ImageButton playerName_random = view.findViewById(R.id.btn_settings_randomname);
        Button btn_save = view.findViewById(R.id.btn_settings_save);
        playerNameView = view.findViewById(R.id.input_settings_playername);

        String playerString = settings.getString("player", Util.getRandomName());
        Util.saveName(settings, playerString);
        playerNameView.setText(playerString);

        playerName_random.setOnClickListener(v -> {
            String newPlayerName = Util.getRandomName();
            playerNameView.setText(newPlayerName);
            Util.saveName(settings, newPlayerName);
        });

        btn_save.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
            getDialog().dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settings, container);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public EditText getPlayerNameView() {
        return playerNameView;
    }
}
