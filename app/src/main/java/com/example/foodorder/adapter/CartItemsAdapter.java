package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.model.CartItems;
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

public class CartItemsAdapter extends FirebaseRecyclerAdapter<CartItems,CartItemsAdapter.CartViewholder> {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public CartItemsAdapter(@NonNull FirebaseRecyclerOptions<CartItems> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewholder holder, int i, @NonNull CartItems dish) {

        Picasso.get().load(dish.getImage()).into(holder.image);
        holder.name.setText(dish.getName());
        holder.price.setText(dish.getPrice());
        holder.total.setText(String.valueOf(dish.getTotal()));
        holder.amount.setText(String.valueOf(dish.getAmount()));

        holder.plus.setOnClickListener(new View.OnClickListener() {
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
                        if(snapshot.child(user).child("cart").child(dish_name).child("amount").getValue(Integer.class) == null)
                        {
                            count = 0;
                        }
                        else count = snapshot.child(user).child("cart").child(dish_name).child("amount").getValue(Integer.class);

                        // UPDATE DATA
                        int total = (count+1)*Integer.parseInt(dish.getPrice());
                        mRef.child(user).child("cart").child(dish_name).child("total").setValue(total);
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

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String dish_name = dish.getName();
                String path = "Users/"+user+"/cart/"+dish_name;
                DatabaseReference mRef = firebaseDatabase.getReference(path);

//                // READ DATA
                mRef.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        if(snapshot.child("amount").getValue(Integer.class) <= 1)
                        {
                            mRef.removeValue();

                        }
                        else {
                            count = snapshot.child("amount").getValue(Integer.class);
                            // UPDATE DATA
                            int total = (count-1)*Integer.parseInt(dish.getPrice());
                            mRef.child("total").setValue(total);
                            mRef.child("amount").setValue(count-1);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // DELETE A DISH
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                String dish_name = dish.getName();
                String path = "Users/"+user+"/cart/"+dish_name;
                DatabaseReference mRef = firebaseDatabase.getReference(path);

                mRef.removeValue();
            }
        });
    }

    @NonNull
    @Override
    public CartViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_items,parent,false);

        return new CartItemsAdapter.CartViewholder(view);
    }

    public class CartViewholder extends RecyclerView.ViewHolder {
        TextView name,amount,price,total;
        ImageView plus,sub,image,delete;
        public CartViewholder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name_dish);
            amount = itemView.findViewById(R.id.amount);
            price = itemView.findViewById(R.id.price_dish);
            total = itemView.findViewById(R.id.total_dish);
            plus = itemView.findViewById(R.id.plus);
            sub = itemView.findViewById(R.id.sub);
            delete = itemView.findViewById(R.id.delete);


        }
    }
}
