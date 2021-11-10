package com.example.foodorder.other_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.adapter.OrderAdapter;
import com.example.foodorder.databinding.ActivityDonHangCanceledBinding;
import com.example.foodorder.model.OrderItems;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonHangCanceled extends AppCompatActivity {

    ActivityDonHangCanceledBinding binding;
    OrderAdapter adapterOrder;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_don_hang_canceled);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // SET VIEW
        setViewDishes();

        // ON CLICK CANCEL
        onClickOrder();

    }

    private void onClickOrder() {
        binding.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "This function is still developed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewDishes() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        // SET ID
        binding.orderId.setText(id);

        // SET VIEW DISHES LIST
        RecyclerView viewOrderDishes;

        FirebaseRecyclerOptions<OrderItems> options;

        String path_order = "Users/" + user + "/Orders/" + id + "/dishes";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path_order);
        viewOrderDishes = binding.recyclerOrderDishes;

        viewOrderDishes.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        options = new FirebaseRecyclerOptions.Builder<OrderItems>()
                .setQuery(mRef, OrderItems.class)
                .build();

        adapterOrder = new OrderAdapter(options);
        viewOrderDishes.setAdapter(adapterOrder);

        // SET VIEW BILL

        String path = "Users/" + user + "/Orders/" + id;
        DatabaseReference mRefOrder = FirebaseDatabase.getInstance().getReference(path);
        mRefOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String total_money = snapshot.child("total_money").getValue(String.class);
                String total_bill_money = snapshot.child("total_bill_money").getValue(String.class);
                String delivery_money = snapshot.child("delivery_money").getValue(String.class);
                String voucher = snapshot.child("voucher").getValue(String.class);
                String location = snapshot.child("location").getValue(String.class);
                String note = snapshot.child("note").getValue(String.class);
                String order_time = snapshot.child("time_order").getValue(String.class);
                String cancel_time = snapshot.child("time_cancel").getValue(String.class);

                binding.timeOrder.setText(order_time);
                binding.timeCancel.setText(cancel_time);
                binding.totalMoney.setText(total_money);
                binding.deliveryMoney.setText(delivery_money);
                binding.voucherMoney.setText(voucher);
                binding.totalBillMoney.setText(total_bill_money);
                binding.location.setText(location);
                binding.note.setText(note);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterOrder.startListening();

    }
}