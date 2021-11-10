package com.example.foodorder.tab_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.OrderListItemAdapter;
import com.example.foodorder.databinding.FragmentOnDeliveryBinding;
import com.example.foodorder.model.OrderListItem;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class fragment_on_delivery extends Fragment {

    FragmentOnDeliveryBinding binding;
    OrderListItemAdapter orderListAdapter;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnDeliveryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerOrderList();

        return root;
    }

    private void recyclerOrderList() {
        RecyclerView view;

        FirebaseRecyclerOptions<OrderListItem> options;

        String path = "Users/" + user + "/Orders/";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);
        Query query = mRef.orderByChild("isCanceled").equalTo("false");

        view = binding.recyclerOrder;

        view.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        options = new FirebaseRecyclerOptions.Builder<OrderListItem>()
                .setQuery(query,OrderListItem.class)
                .build();

        orderListAdapter = new OrderListItemAdapter(options);

        view.setAdapter(orderListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        orderListAdapter.startListening();
    }
}