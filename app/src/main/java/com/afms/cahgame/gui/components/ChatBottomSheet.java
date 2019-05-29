package com.afms.cahgame.gui.components;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afms.cahgame.R;
import com.afms.cahgame.data.Message;
import com.afms.cahgame.game.Lobby;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatBottomSheet extends BottomSheetDialogFragment {
    private ResultListener resultListener;

    private ChatItemAdapter chatItemAdapter;
    private ListView list_chat;
    private List<Message> lastMessages = new ArrayList<>();
    private RelativeLayout layout_chat_no_messages;

    public static ChatBottomSheet create(Lobby lobby) {
        ChatBottomSheet chatBottomSheet = new ChatBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("lobby", lobby);
        chatBottomSheet.setArguments(bundle);
        return chatBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_ingame_chat, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Lobby lobby = (Lobby) getArguments().getSerializable("lobby");

            if (lobby != null) {
                EditText input_chat_message = view.findViewById(R.id.input_chat_message);
                ImageButton btn_chat_send = view.findViewById(R.id.btn_chat_send);
                layout_chat_no_messages = view.findViewById(R.id.layout_chat_no_messages);
                btn_chat_send.setOnClickListener(v -> {
                    String inputText = input_chat_message.getText().toString().trim();
                    if (inputText.length() > 0) {
                        resultListener.onItemClick(inputText);
                        input_chat_message.setText("");
                    } else {
                        Toast.makeText(getContext(), getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
                    }
                });

                list_chat = view.findViewById(R.id.list_chat);

                chatItemAdapter = new ChatItemAdapter(view.getContext(), new ArrayList<>());
                chatItemAdapter.addAll(lobby.getMessages());
                list_chat.setAdapter(chatItemAdapter);
                list_chat.setSelection(list_chat.getAdapter().getCount() - 1);
                list_chat.setOnTouchListener((v, event) -> {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow NestedScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow NestedScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                });

                if (chatItemAdapter.isEmpty()) {
                    layout_chat_no_messages.setVisibility(View.VISIBLE);
                    list_chat.setVisibility(View.INVISIBLE);
                } else {
                    layout_chat_no_messages.setVisibility(View.INVISIBLE);
                    list_chat.setVisibility(View.VISIBLE);
                }
                initializeDatabaseConnection(lobby.getId());
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        resultListener.clearReference();
        super.onCancel(dialog);
    }

    private void initializeDatabaseConnection(String lobbyId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference chatReference = database.getReference("lobbies/" + lobbyId + "/messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Message>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Message>>() {
                };
                List<Message> messages = dataSnapshot.getValue(genericTypeIndicator);
                if (messages != null) {
                    chatItemAdapter.clear();
                    chatItemAdapter.addAll(messages);
                    lastMessages = messages;
                    list_chat.setAdapter(chatItemAdapter);
                    if (chatItemAdapter.isEmpty()) {
                        layout_chat_no_messages.setVisibility(View.VISIBLE);
                        list_chat.setVisibility(View.INVISIBLE);
                    } else {
                        layout_chat_no_messages.setVisibility(View.INVISIBLE);
                        list_chat.setVisibility(View.VISIBLE);
                    }
                    list_chat.setSelection(list_chat.getAdapter().getCount()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(getString(R.string.errorLog), getString(R.string.failedToGetLobby), databaseError.toException());
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.GONE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) Objects.requireNonNull(getView()).getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

}
