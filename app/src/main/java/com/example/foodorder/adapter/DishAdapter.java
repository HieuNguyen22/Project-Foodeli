package com.example.foodorder.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.DishDetail;
import com.example.foodorder.R;
import com.example.foodorder.model.Dishes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DishAdapter extends FirebaseRecyclerAdapter<Dishes,DishAdapter.DishViewholder> {

    private List<Dishes> dishesList;

    public DishAdapter(@NonNull FirebaseRecyclerOptions<Dishes> options, List<Dishes> dishesList) {
        super(options);
        this.dishesList = dishesList;
    }

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public DishAdapter(@NonNull FirebaseRecyclerOptions<Dishes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DishViewholder holder, int i, @NonNull Dishes dish) {

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));

        String name = dish.getName();
        DatabaseReference ref = firebaseDatabase.getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user).child("favorite").child(name).exists()) holder.favor.setImageResource(R.drawable.heart);
                else holder.favor.setImageResource(R.drawable.blank_heart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // SET DATA
        holder.name.setText(dish.getName());
        holder.price.setText(dish.getPrice());
        Picasso.get().load(dish.getImage()).into(holder.image);


        // EVENT FAVORITE
        holder.favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String dish_name = dish.getName();
                DatabaseReference mRef = firebaseDatabase.getReference("Users");

//                // READ DATA
                mRef.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count;
                        if(snapshot.child(user).child("favorite").child(dish_name).exists())
                        {
                            holder.favor.setImageResource(R.drawable.blank_heart);
                            mRef.child(user).child("favorite").child(dish_name).removeValue();
                        }
                        else {
                            holder.favor.setImageResource(R.drawable.heart);
                            mRef.child(user).child("favorite").child(dish_name).child("name").setValue((dish_name));
                            mRef.child(user).child("favorite").child(dish_name).child("price").setValue((dish.getPrice()));
                            mRef.child(user).child("favorite").child(dish_name).child("image").setValue((dish.getImage()));
                            mRef.child(user).child("favorite").child(dish_name).child("id").setValue((dish.getId()));

//                            mRef.child(user).child("favorite").child(dish_name).child("isFavor").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // EVENT ADD
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String dish_name = dish.getName();
                String dish_price = dish.getPrice();

                DatabaseReference mRef = firebaseDatabase.getReference("Users");

//                // READ DATA
                mRef.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count;
                        if(snapshot.child(user).child("cart").child(dish_name).child("amount").getValue(Integer.class) == null)
                        {
                            count = 0;
                        }
                        else count = snapshot.child(user).child("cart").child(dish_name).child("amount").getValue(Integer.class);

                        // UPDATE DATA
                        mRef.child(user).child("cart").child(dish_name).child("name").setValue((dish_name));
                        mRef.child(user).child("cart").child(dish_name).child("price").setValue((dish_price));
                        mRef.child(user).child("cart").child(dish_name).child("image").setValue((dish.getImage()));
                        mRef.child(user).child("cart").child(dish_name).child("id").setValue((dish.getId()));
                        // UPDATE TOTAL
                        int total = (count+1)*Integer.parseInt(dish_price);
                        mRef.child(user).child("cart").child(dish_name).child("total").setValue((total));
                        mRef.child(user).child("cart").child(dish_name).child("amount").setValue((count+1), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(view.getContext(), "Added successfully "+ (count+1), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // EVENT LAYOUT
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = dish.getId();
//                String id = String.valueOf(holder.getAdapterPosition());
                Toast.makeText(view.getContext(), "Vi tri: "+id, Toast.LENGTH_SHORT).show();
                Fragment fragment = new DishDetail();
                //
                Bundle bundle = new Bundle();
                bundle.putString("pos", id);
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
    public DishViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_dishes,parent,false);

        return new DishAdapter.DishViewholder(view);
    }

    public class DishViewholder extends RecyclerView.ViewHolder {
        TextView name,price;
        ImageView image,add,favor;
        ConstraintLayout layout;
        public DishViewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_dish);
            price = itemView.findViewById(R.id.price_dish);
            image = itemView.findViewById(R.id.image_dish);
            add = itemView.findViewById(R.id.add);
            favor = itemView.findViewById(R.id.favorite);
            layout = itemView.findViewById(R.id.item_recycler_view);

        }
    }
}
