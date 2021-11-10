package com.example.foodorder.other_activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityCheckOutBinding;
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
import java.util.UUID;

public class CheckOutActivity extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    String id;
    DatabaseReference mRefLocation, mRefLocate;
    ValueEventListener mListenerLocation, mListenerLocate;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0, email.indexOf("@"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);

        // SET VIEW
        setView();

        // CLICK ON GET LOCATION
        getLocation();

        // CLICK ON ORDER
        order();

        // CLICK ON BACK
        back();

    }

    private void back() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void order() {
        binding.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // CHECK LOCATION
                mRefLocate = FirebaseDatabase.getInstance().getReference("Users/"+user+"/location");
                mRefLocate.addValueEventListener(mListenerLocate = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                // CREATE RANDOM ID
                UUID uuid = UUID.randomUUID();
                id = uuid.toString();

                // UPDATE DATA
                String path_order = "Users/" + user + "/Orders";
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path_order);
                String path_cart = "Users/" + user + "/cart";
                DatabaseReference mRefCart = FirebaseDatabase.getInstance().getReference(path_cart);

                mRef.child(id).child("id").setValue(id);
                mRef.child(id).child("total_money").setValue(binding.totalMoney.getText().toString());
                mRef.child(id).child("delivery_money").setValue(binding.deliveryMoney.getText().toString());
                mRef.child(id).child("voucher").setValue(binding.voucherMoney.getText().toString());
                mRef.child(id).child("total_bill_money").setValue(binding.totalBillMoney.getText().toString());
                mRef.child(id).child("location").setValue(binding.location.getText().toString());
                mRef.child(id).child("note").setValue(binding.note.getText().toString());
                mRef.child(id).child("isCanceled").setValue("false");


                //// UPDATE LIST DISHES
                mRefCart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            String name = s.child("name").getValue(String.class);
                            int qty = s.child("amount").getValue(Integer.class);
                            mRef.child(id).child("dishes").child(name).child("name").setValue(name);
                            mRef.child(id).child("dishes").child(name).child("qty").setValue(qty);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                // UPDATE TIME ORDER
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = dateFormat.format(date);

                mRef.child(id).child("time_order").setValue(dateFormat.format(date));

                // DELETE CART
                mRefCart.removeValue();

                // NOTIFY
                Toast.makeText(getBaseContext(), "Order successfully" + id, Toast.LENGTH_SHORT).show();

                // SEND NOTIFICATION
                sendPushNotification();

                // CHANGE ACTIVITY
                Intent intent = new Intent(getBaseContext(), DonHang.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();

                // UPDATE NOTIFY
                String path_notify = "Users/" + user + "/Notify";

                DatabaseReference mRefNotify = FirebaseDatabase.getInstance().getReference(path_notify);
                mRefNotify.child(time).child("id").setValue(id);
                mRefNotify.child(time).child("time_order").setValue(dateFormat.format(date));

            }
        });

    }

    private void sendPushNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, DonHang.class);
        resultIntent.putExtra("id", id);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(getNotificationID(), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, MyNotification.CHANNEL_ID)
                .setContentTitle("Order successfully")
                .setContentText("Your order " + id + " is ready for delivery now!")
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

    private void getLocation() {
        binding.getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setView() {
        Intent intent = getIntent();
        int total = intent.getIntExtra("sum", 1);

        // SET TOTAL
        binding.totalMoney.setText(String.valueOf(total));

        // SET TOTAL BILL
        int totalBill = total + Integer.parseInt(binding.deliveryMoney.getText().toString()) - Integer.parseInt(binding.voucherMoney.getText().toString());
        binding.totalBillMoney.setText(String.valueOf(totalBill));

        // SET LOCATION
        String path = "Users/" + user + "/location";
        mRefLocation = FirebaseDatabase.getInstance().getReference(path);

        mRefLocation.addValueEventListener(mListenerLocation = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String location = snapshot.getValue(String.class);
                    binding.location.setText(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        mRefLocation.removeEventListener(mListenerLocation);
        mRefLocate.removeEventListener(mListenerLocate);
    }
}