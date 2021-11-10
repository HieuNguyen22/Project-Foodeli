package com.example.foodorder.main_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.adapter.DishAdapter;
import com.example.foodorder.databinding.FragmentFavoriteBinding;
import com.example.foodorder.model.Dishes;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FavoriteFragment extends Fragment {

    DishAdapter dishAdapter;
    private FragmentFavoriteBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFavoriteBinding.inflate(inflater, container,false);
        View root = binding.getRoot();

        favorRecyclerView();
//        catchEventBack();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override public void onStart()
    {
        super.onStart();
        dishAdapter.startListening();
    }

    //    @Override public void onStop()
//    {
//        super.onStop();
//        adapterPopular.stopListening();
//        adapterDiscount.startListening();
//    }

    private void favorRecyclerView() {
        DatabaseReference reference;
        RecyclerView viewFavor;
        FirebaseRecyclerOptions<Dishes> options;

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));
        String path = "Users/"+user+"/favorite";
        reference = FirebaseDatabase.getInstance().getReference(path);

        viewFavor = binding.recyclerFavor;

        viewFavor.setLayoutManager(new GridLayoutManager(getContext(),2));
        options = new FirebaseRecyclerOptions.Builder<Dishes>()
                .setQuery(reference,Dishes.class)
                .build();

        dishAdapter = new DishAdapter(options);

        viewFavor.setAdapter(dishAdapter);
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