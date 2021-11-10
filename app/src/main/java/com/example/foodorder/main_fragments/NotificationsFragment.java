package com.example.foodorder.main_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.NotifyItemAdapter;
import com.example.foodorder.databinding.FragmentNotificationsBinding;
import com.example.foodorder.model.NotifyItems;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    NotifyItemAdapter notifyItemAdapter;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerNotify();
        return root;
    }

    private void recyclerNotify() {

        RecyclerView view;
        view = binding.recyclerNotify;

        String path = "Users/" + user + "/Notify";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

        Query query = mRef.orderByValue().limitToLast(10);
        view.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        FirebaseRecyclerOptions<NotifyItems> options;
        options = new FirebaseRecyclerOptions.Builder<NotifyItems>()
                .setQuery(query,NotifyItems.class)
                .build();

        notifyItemAdapter = new NotifyItemAdapter(options);

        view.setAdapter(notifyItemAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        notifyItemAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
