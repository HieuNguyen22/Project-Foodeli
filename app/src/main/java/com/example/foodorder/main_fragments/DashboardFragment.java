package com.example.foodorder.main_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.CheckOutActivity;
import com.example.foodorder.adapter.CartItemsAdapter;
import com.example.foodorder.databinding.FragmentDashboardBinding;
import com.example.foodorder.model.CartItems;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    CartItemsAdapter cart_adapter;
    private FragmentDashboardBinding binding;

    int sum;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));
    String path = "Users/" + user + "/cart";


    DatabaseReference mRefSum = FirebaseDatabase.getInstance().getReference(path);
    Query mList = mRefSum.orderByChild("total");
    ValueEventListener mListenerSum;

    DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference(path);
    ValueEventListener mListenerCheck;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CartRecyclerView();

        // CHECK DANH SACH CART
        checkRef.addValueEventListener(mListenerCheck = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.clearAll.setVisibility(View.VISIBLE);
                    binding.checkOut.setVisibility(View.VISIBLE);
                } else {
                    binding.clearAll.setVisibility(View.INVISIBLE);
                    binding.checkOut.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // SET SUM VIEW
        calculateSum();

        // CLICK ON CLEAR
        binding.clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAll();
            }
        });

        // CLICK ON CHECK OUT
        binding.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOut();
            }
        });


        return root;
    }

    // CALCULATE SUM
    private void calculateSum() {
        mList.addValueEventListener(mListenerSum = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sum = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int total_of_dish = dataSnapshot.child("total").getValue(Integer.class);
                    sum += total_of_dish;
                }
                binding.total.setText(String.valueOf(sum));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void checkOut() {
        Intent intent = new Intent(getContext(), CheckOutActivity.class);
        intent.putExtra("sum", sum);
        startActivity(intent);
    }

    private void clearAll() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);
        mRef.removeValue();
        binding.clearAll.setVisibility(View.INVISIBLE);
    }

    private void CartRecyclerView() {
        DatabaseReference reference;
        RecyclerView viewCart;
        FirebaseRecyclerOptions<CartItems> options;

        reference = FirebaseDatabase.getInstance().getReference(path);

        viewCart = binding.recyclerCart;

        viewCart.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        options = new FirebaseRecyclerOptions.Builder<CartItems>()
                .setQuery(reference, CartItems.class)
                .build();

        cart_adapter = new CartItemsAdapter(options);

        viewCart.setAdapter(cart_adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mList.removeEventListener(mListenerSum);
        checkRef.removeEventListener(mListenerCheck);
    }

    @Override
    public void onStart() {
        super.onStart();
        cart_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    // CATCH EVENT BACK
//    private void catchEventBack(){
//        binding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                getFragmentManager().popBackStack();
//            }
//        });
//
//    }

}