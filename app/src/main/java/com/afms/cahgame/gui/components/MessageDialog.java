package com.afms.cahgame.gui.components;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afms.cahgame.R;

import java.util.ArrayList;

public class MessageDialog extends DialogFragment {

    private ResultListener resultListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView label_dialog_message_title = view.findViewById(R.id.label_dialog_message_title);
        TextView label_dialog_message_text = view.findViewById(R.id.label_dialog_message_text);
        LinearLayout layout_dialog_message_buttons = view.findViewById(R.id.layout_dialog_message_buttons);

        label_dialog_message_title.setText(getArguments().getString("title"));
        label_dialog_message_text.setText(getArguments().getString("message"));

        getArguments().getStringArrayList("values").forEach(value -> {
            Button btn = (Button) getLayoutInflater().inflate(R.layout.component_dialog_selector_button, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(6, 6, 6, 6);
            btn.setLayoutParams(params);
            btn.setText(value);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btn.setOnClickListener(event -> {
                resultListener.onItemClick(String.valueOf(btn.getText()));
                getDialog().dismiss();
            });
            layout_dialog_message_buttons.addView(btn);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.component_dialog_message, container);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static MessageDialog create(String title, String msg, ArrayList<String> values) {
        MessageDialog messageDialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("values", values);
        args.putString("title", title);
        args.putString("message", msg);
        messageDialog.setArguments(args);
        return messageDialog;
    }



    public static MessageDialog create(String title, ArrayList<String> values) {
        MessageDialog messageDialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("values", values);
        args.putString("title", title);
        args.putString("message", "");
        messageDialog.setArguments(args);
        return messageDialog;
    }


    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }
}
