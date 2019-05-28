package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Colour;
import com.afms.cahgame.data.Message;
import com.afms.cahgame.game.Card;
import com.afms.cahgame.gui.utils.JustifiedTextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ChatItemAdapter extends ArrayAdapter<Message> {

    public ChatItemAdapter(@NonNull Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);
        SharedPreferences settings = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        View view;
        assert message != null;
        if(message.getOwner().equals(settings.getString("player", ""))){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_message_rtl, parent, false);
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_message_ltr, parent, false);
        }

        JustifiedTextView item_message_text = view.findViewById(R.id.item_message_text);
        TextView item_message_owner = view.findViewById(R.id.item_message_owner);

        item_message_text.setText(message.getMessage());
        item_message_owner.setText(message.getOwner());

        return view;
    }
}
