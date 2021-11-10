package com.example.foodorder.tab_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.CancelListItemAdapter;
import com.example.foodorder.databinding.FragmentCanceledBinding;
import com.example.foodorder.model.CanceledListItem;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class fragment_canceled extends Fragment {

    FragmentCanceledBinding binding;
    CancelListItemAdapter cancelListAdapter;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCanceledBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerCancelList();

        return root;
    }

    private void recyclerCancelList() {
        RecyclerView view;

        FirebaseRecyclerOptions<CanceledListItem> options;

        String path = "Users/" + user + "/Orders/";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);
        Query query = mRef.orderByChild("isCanceled").equalTo("true");

        view = binding.recyclerCanceled;

        view.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        options = new FirebaseRecyclerOptions.Builder<CanceledListItem>()
                .setQuery(query,CanceledListItem.class)
                .build();

        cancelListAdapter = new CancelListItemAdapter(options);

        view.setAdapter(cancelListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        cancelListAdapter.startListening();
    }
}