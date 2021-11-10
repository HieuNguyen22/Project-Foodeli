package com.example.foodorder.other_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.DishAdapter;
import com.example.foodorder.databinding.FragmentDishListBinding;
import com.example.foodorder.model.Dishes;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DishList extends Fragment {

    private FragmentDishListBinding binding;
    DishAdapter dishAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDishListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        showView();
        catchEventBack();
        return root;
    }

    private void showView() {
        // SET NAME OF LIST
        String name = getArguments().getString("name");
        binding.nameList.setText(name);

        // SET RECYCLERVIEW ITEMS
        DatabaseReference reference;
        RecyclerView view;
        FirebaseRecyclerOptions<Dishes> options;

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));
        String path = "Dishes";
        reference = FirebaseDatabase.getInstance().getReference(path);

        view = binding.recyclerFavor;

        view.setLayoutManager(new GridLayoutManager(getContext(),2));
        options = new FirebaseRecyclerOptions.Builder<Dishes>()
                .setQuery(reference,Dishes.class)
                .build();

        dishAdapter = new DishAdapter(options);

        view.setAdapter(dishAdapter);
    }

    // CATCH EVENT BACK
    private void catchEventBack(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStack();
            }
        });

    }

    @Override public void onStart()
    {
        super.onStart();
        dishAdapter.startListening();
    }
}