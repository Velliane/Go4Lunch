package com.menard.go4lunch.controller.fragment;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.menard.go4lunch.R;
import com.menard.go4lunch.adapter.ChatAdapter;
import com.menard.go4lunch.api.ChatHelper;
import com.menard.go4lunch.model.Message;

import org.threeten.bp.LocalDateTime;

public class ChatFragment extends BaseFragment implements View.OnClickListener {


    /** Input EditText */
    private TextInputEditText editMessage;
    /** Send Button */
    private Button sendButton;

    public static ChatFragment newInstance(){
        return new ChatFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chat_fragment_recycler_view);
        editMessage = view.findViewById(R.id.chat_fragment_edit_text);
        sendButton = view.findViewById(R.id.chat_fragment_send_button);
        sendButton.setOnClickListener(this);

        AndroidThreeTen.init(requireActivity());

        Query query = ChatHelper.getAllMessageForChat();
        FirestoreRecyclerOptions<Message> list = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class)
                .setLifecycleOwner(this).build();

        //-- Recycler View --
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        ChatAdapter workmatesAdapter = new ChatAdapter(requireActivity(),list);
        recyclerView.setAdapter(workmatesAdapter);

        return view;

    }

    @Override
    public void onClick(View v) {
        if(v == sendButton){
            String message = String.valueOf(editMessage.getText());
            if (!message.equals("")){
                ChatHelper.addMessage(LocalDateTime.now().withNano(0).toString(), message, getCurrentUser().getUid());
                editMessage.setText("");
            }
        }
    }
}
