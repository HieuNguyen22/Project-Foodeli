package com.example.foodorder.other_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.ChatMessageAdapter;
import com.example.foodorder.databinding.FragmentChatBoxBinding;
import com.example.foodorder.model.ChatMessageModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatBox extends Fragment {



    FragmentChatBoxBinding binding;
    ChatMessageAdapter messageAdapter;
    DatabaseReference mRefScroll;
    ValueEventListener mListenerScroll;

    String id_client;
    String avatar_client;
    String name_client;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBoxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        id_client = getArguments().getString("id_client");
        avatar_client = getArguments().getString("avatar_client");
        name_client = getArguments().getString("name_client");

        // SET VIEW CLIENT
        setViewClient();

        // ON SEND MESSAGE
        onSendMessage();

        // SET VIEW MESSAGE
        setViewMassage();

        // ON CLICK BACK
        onClickBack();

        // SCROLL
        String path = "Chats/"+user+"/"+id_client+"/messages";
        mRefScroll = FirebaseDatabase.getInstance().getReference(path);
        mRefScroll.addValueEventListener(mListenerScroll = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    count++;
                }
                if(count > 0) binding.recyclerMessage.scrollToPosition(count - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return root;
    }

    private void onClickBack() {

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void onSendMessage() {

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.messageText.getText().toString().trim().isEmpty()) {
                    // CLEAR EDITTEXT
                    binding.messageText.setText("");
                    return;
                }

                // GET TIME
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = dateFormat.format(date);
                // GET TEXT
                String text = binding.messageText.getText().toString().trim();
                // GET FROM
                String from = user;

                // UPDATE HOST
                String path = "Chats/"+user+"/"+id_client+"/messages";
                mRefScroll = FirebaseDatabase.getInstance().getReference(path);

                mRefScroll.child(time).child("from").setValue(from);
                mRefScroll.child(time).child("text").setValue(text);
                mRefScroll.child(time).child("time").setValue(time);

                // UPDATE CLIENT
                String path_client = "Chats/"+id_client+"/"+user+"/messages";
                DatabaseReference mRefClient = FirebaseDatabase.getInstance().getReference(path_client);

                mRefClient.child(time).child("from").setValue(from);
                mRefClient.child(time).child("text").setValue(text);
                mRefClient.child(time).child("time").setValue(time);

                // CLEAR EDITTEXT
                binding.messageText.setText("");

                // SCROLL
                mRefScroll.addValueEventListener(mListenerScroll = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                            count++;
                        }
                        if(count > 0) binding.recyclerMessage.scrollToPosition(count - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void setViewClient() {
        // SET AVATAR
        Picasso.get().load(avatar_client).into(binding.avatarClient);
        // SET NAME
        binding.nameClient.setText(name_client);
    }

    private void setViewMassage() {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0, email.indexOf("@"));

        RecyclerView view;
        view = binding.recyclerMessage;

        String path = "Chats/"+user+"/"+id_client+"/messages";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

        view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        FirebaseRecyclerOptions<ChatMessageModel> options;

        options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(mRef,ChatMessageModel.class)
                .build();

        messageAdapter = new ChatMessageAdapter(options);

        view.setAdapter(messageAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRefScroll.removeEventListener(mListenerScroll);
    }
}