package com.afms.cahgame.gui.components;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afms.cahgame.R;

import java.util.ArrayList;
import java.util.Objects;

import cn.lankton.flowlayout.FlowLayout;

public class ValueSelector extends DialogFragment {

    private ResultListener resultListener;

    public ValueSelector() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_selector, container);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FlowLayout flowLayout = view.findViewById(R.id.component_dialog_selector_flowlayout);
        TextView title = view.findViewById(R.id.component_dialog_selector_title);
        if (getArguments() != null) {
            title.setText(getArguments().getString("title"));
            Objects.requireNonNull(getArguments().getStringArrayList("values")).forEach(value -> {
                Button btn = (Button) getLayoutInflater().inflate(R.layout.dialog_selector_button, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(6, 6, 6, 6);
                btn.setLayoutParams(params);
                btn.setText(value);
                btn.setOnClickListener(event -> {
                    resultListener.onItemClick(String.valueOf(btn.getText()));
                    getDialog().dismiss();
                });
                flowLayout.addView(btn);
            });
        }
    }

    public static ValueSelector create(String title, ArrayList<String> values) {
        ValueSelector valueSelector = new ValueSelector();
        Bundle args = new Bundle();
        args.putStringArrayList("values", values);
        args.putString("title", title);
        valueSelector.setArguments(args);
        return valueSelector;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }
}
