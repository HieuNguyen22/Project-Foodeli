package com.example.foodorder.other_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodorder.R;
import com.example.foodorder.databinding.FragmentDishDetailBinding;
import com.example.foodorder.main_fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DishDetail extends Fragment {

    private FragmentDishDetailBinding binding;
    String name,image,price,id;
    int count = 1;
    int total;

    String pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDishDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pos = getArguments().getString("pos");
        
        showDetail();

        catchEventBack();

        catchEventFavor();

        catchAddToCart();

        return root;
    }

    private void catchAddToCart() {

        // CATCH EVENT PLUS
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                binding.qty.setText(Integer.toString(count));
                // UPDATE TOTAL
                total = count*Integer.parseInt(price);
                binding.total.setText(Integer.toString(total));
            }
        });

        // CATCH EVENT SUB
        binding.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 0) count = 1;
                else
                {
                    count--;
                    binding.qty.setText(Integer.toString(count));
                    // UPDATE TOTAL
                    total = count*Integer.parseInt(price);
                    binding.total.setText(Integer.toString(total));
                }
            }
        });

        // CATCH EVENT ADD TO CART
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String path = "Users/"+user+"/cart/"+name;
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int qty = snapshot.child("amount").getValue(Integer.class);
                            int total_old = snapshot.child("total").getValue(Integer.class);
                            mRef.child("amount").setValue(count+qty);
                            mRef.child("total").setValue(total+total_old);
                        }
                        else{
                            if(count > 0) {
                                // UPDATE DATA
                                mRef.child("name").setValue(name);
                                mRef.child("image").setValue(image);
                                mRef.child("price").setValue(price);
                                mRef.child("total").setValue(total);
                                mRef.child("amount").setValue((count), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(view.getContext(), "Added to cart: "+ count, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else Toast.makeText(getContext(), "Pick up something!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    // CATCH EVENT FAVOR
    private void catchEventFavor() {
        binding.favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String path = "Users/"+user+"/favorite/"+name;
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

//                // READ DATA
                mRef.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count;
                        if(snapshot.exists())
                        {
                            binding.favor.setImageResource(R.drawable.ic_favorite_blank);
                            mRef.removeValue();
                        }
                        else {
                            binding.favor.setImageResource(R.drawable.ic_favorite);
                            mRef.child("id").setValue(pos);
                            mRef.child("name").setValue((name));
                            mRef.child("price").setValue(price);
                            mRef.child("image").setValue(image);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // SHOW DETAIL
    private void showDetail() {
        // GET PATH TO DISHES

        String path_dish = "Dishes/"+pos;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path_dish);

        // GET PATH TO FAVORITE IN USER
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));
        String path_favor = "Users/"+user+"/favorite";
        DatabaseReference mRef_user = FirebaseDatabase.getInstance().getReference(path_favor);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("name").exists()) binding.name.setText("vi tri: "+pos);
                else {
                    // GET NAME
                    name = snapshot.child("name").getValue(String.class);
                    binding.name.setText(name);
                    // GET IMAGE
                    image = snapshot.child("image").getValue(String.class);
                    Picasso.get().load(image).into(binding.image);
                    // GET PRICE
                    price = snapshot.child("price").getValue(String.class);
                    binding.price.setText(price);
                    // GET TOTAL
                    binding.total.setText(String.valueOf(price));
                    // GET AMOUNT
                    binding.qty.setText("1");

                    // CHECK DATA IN FAVORITE
                    mRef_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(name).exists()) {
                                binding.favor.setImageResource(R.drawable.ic_favorite);
                                Toast.makeText(getContext(), "Bing", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                binding.favor.setImageResource(R.drawable.ic_favorite_blank);
                                Toast.makeText(getContext(), "Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // CATCH EVENT BACK
    private void catchEventBack(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment();

                // load fragment
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
////                transaction.replace(R.id.frame_container, fragment);
////                transaction.addToBackStack(null);
////                transaction.commit();
                getFragmentManager().popBackStack();
            }
        });

    }


}