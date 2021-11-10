package com.example.foodorder.other_activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.adapter.OrderAdapter;
import com.example.foodorder.databinding.ActivityDonHangBinding;
import com.example.foodorder.model.OrderItems;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DonHang extends AppCompatActivity {

    ActivityDonHangBinding binding;
    OrderAdapter adapterOrder;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_don_hang);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // SET VIEW
        setViewDishes();

        // ON CLICK CANCEL
        onClickCancel();

    }

    private void onClickCancel() {
        Intent intent = getIntent();
        String index = intent.getStringExtra("id");

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder checkDialog = new AlertDialog.Builder(view.getContext());
                checkDialog.setTitle("CANCEL");
                checkDialog.setMessage("Do you want to cancel?");

                checkDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                        String path = "Users/" + user + "/Orders/" + index;
                        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        mRef.child("time_cancel").setValue(dateFormat.format(date));

                        mRef.child("isCanceled").setValue("true");

                        sendPushNotification();

                        // UPDATE NOTIFY
                        String path_notify = "Users/" + user + "/Notify";
                        String time = dateFormat.format(date);
                        DatabaseReference mRefNotify = FirebaseDatabase.getInstance().getReference(path_notify);
                        mRefNotify.child(time).child("id").setValue(index);
                        mRefNotify.child(time).child("time_cancel").setValue(dateFormat.format(date));

                    }
                });

                checkDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Close the dialog
//                        dialog.cancel();
                    }
                });

                checkDialog.create().show();

            }
        });
    }

    private void sendPushNotification() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, DonHangCanceled.class);
        resultIntent.putExtra("id", id);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(getNotificationID(), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, MyNotification.CHANNEL_ID)
                .setContentTitle("Cancel successfully")
                .setContentText("Your order " + id + " was canceled!")
                .setSmallIcon(R.drawable.default_avatar)
                .setLargeIcon(bitmap)
                .setColor(getResources().getColor(R.color.main_color))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(getNotificationID(), notification);

    }

    private int getNotificationID() {
        return (int) new Date().getTime();
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

                binding.timeOrder.setText(order_time);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}