package com.example.foodorder.main_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.ChatPersonAdapter;
import com.example.foodorder.databinding.FragmentChatBinding;
import com.example.foodorder.model.ChatPersonModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatFragment extends Fragment {

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));

    FragmentChatBinding binding;
    ChatPersonAdapter chatPersonAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//         SET VIEW CHAT PROFILE
        setViewChatProfile();



        return root;
    }

    private void setViewChatProfile() {
        RecyclerView view;
        view = binding.recyclerChatProfile;

        String path = "Chats/"+user;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

        view.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        FirebaseRecyclerOptions<ChatPersonModel> options;

        options = new FirebaseRecyclerOptions.Builder<ChatPersonModel>()
                .setQuery(mRef,ChatPersonModel.class)
                .build();

        chatPersonAdapter = new ChatPersonAdapter(options);

        view.setAdapter(chatPersonAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        chatPersonAdapter.startListening();
    }
}