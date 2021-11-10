package com.example.foodorder.adapter;

import static android.view.View.GONE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.model.ChatMessageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageAdapter.ChatViewholder> {


    public ChatMessageAdapter(@NonNull FirebaseRecyclerOptions<ChatMessageModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewholder holder, int position, @NonNull ChatMessageModel model) {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));
        String from = model.getFrom();
        if(from.equals(user)){
            holder.fromMe.setText(model.getText());
            holder.fromClient.setVisibility(GONE);
        }
        else {
            holder.fromClient.setText(model.getText());
            holder.fromMe.setVisibility(GONE);
        }
//        holder.fromClient.setText(model.getText());
    }

    @NonNull
    @Override
    public ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message,parent,false);

        return new ChatViewholder(view);
    }

    public class ChatViewholder extends RecyclerView.ViewHolder {
        TextView fromMe, fromClient;
        public ChatViewholder(@NonNull View itemView) {
            super(itemView);

            fromMe = itemView.findViewById(R.id.textFromMe);
            fromClient= itemView.findViewById(R.id.textFromClient);
        }
    }
}
