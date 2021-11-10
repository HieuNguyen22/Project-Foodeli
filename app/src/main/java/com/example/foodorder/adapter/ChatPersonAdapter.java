package com.example.foodorder.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.ChatBox;
import com.example.foodorder.R;
import com.example.foodorder.model.ChatPersonModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ChatPersonAdapter extends FirebaseRecyclerAdapter<ChatPersonModel, ChatPersonAdapter.ChatViewholder> {

    public ChatPersonAdapter(@NonNull FirebaseRecyclerOptions<ChatPersonModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatPersonAdapter.ChatViewholder holder, int position, @NonNull ChatPersonModel model) {

        String id_client = model.getUsername();
        String name_client = model.getName();
        String avatar_client = model.getAvatar();
        Picasso.get().load(avatar_client).into(holder.avatar);
        holder.name.setText(name_client);
        holder.username.setText(id_client);

        // ON CLICK LAYOUT
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ChatBox();
                //
                Bundle bundle = new Bundle();
                bundle.putString("id_client", id_client);
                bundle.putString("avatar_client",avatar_client);
                bundle.putString("name_client",name_client);
                fragment.setArguments(bundle);
                // load fragment
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }

    @NonNull
    @Override
    public ChatPersonAdapter.ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_person,parent,false);

        return new ChatPersonAdapter.ChatViewholder(view);
    }

    public class ChatViewholder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, username;
        ConstraintLayout layout;
        public ChatViewholder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            layout = itemView.findViewById(R.id.recycler_chat);
        }
    }
}
