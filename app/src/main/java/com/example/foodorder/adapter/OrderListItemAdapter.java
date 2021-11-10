package com.example.foodorder.adapter;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.DonHang;
import com.example.foodorder.other_activities.DonHangCanceled;
import com.example.foodorder.other_activities.MyNotification;
import com.example.foodorder.R;
import com.example.foodorder.model.OrderListItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderListItemAdapter extends FirebaseRecyclerAdapter<OrderListItem,OrderListItemAdapter.OrderListViewholder> {

    private List<OrderListItem> orderListItemList;
    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));

    public OrderListItemAdapter(@NonNull FirebaseRecyclerOptions<OrderListItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderListItemAdapter.OrderListViewholder holder, int position, @NonNull OrderListItem model) {
        holder.id.setText(model.getId());
        holder.total.setText(model.getTotal_money());
        holder.delivery.setText(model.getDelivery_money());
        holder.totalBill.setText(model.getTotal_bill_money());
        holder.orderTime.setText(model.getTime_order());

        String id = model.getId();

        // ON CLICK LAYOUT
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DonHang.class);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                intent.putExtra("id",id);
                activity.startActivity(intent);
            }
        });

        // ON CLICK CANCEL
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                AlertDialog.Builder checkDialog = new AlertDialog.Builder(view.getContext());
                checkDialog.setTitle("CANCEL");
                checkDialog.setMessage("Do you want to cancel?");

                checkDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String path = "Users/" + user + "/Orders/" + id;
                        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(path);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        mRef.child("time_cancel").setValue(dateFormat.format(date));

                        mRef.child("isCanceled").setValue("true");

                        // SEND NOTIFICATION
                        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);

                        // Create an Intent for the activity you want to start
                        Intent resultIntent = new Intent(view.getContext(), DonHangCanceled.class);
                        resultIntent.putExtra("id", id);
                        // Create the TaskStackBuilder and add the intent, which inflates the back stack
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(view.getContext());
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        // Get the PendingIntent containing the entire back stack
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(getNotificationID(), PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification notification = new NotificationCompat.Builder(view.getContext(), MyNotification.CHANNEL_ID)
                                .setContentTitle("Cancel successfully")
                                .setContentText("Your order " + id + " was canceled!")
                                .setSmallIcon(R.drawable.default_avatar)
                                .setLargeIcon(bitmap)
                                .setColor(activity.getResources().getColor(R.color.main_color))
                                .setContentIntent(resultPendingIntent)
                                .setAutoCancel(true)
                                .build();

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(view.getContext());
                        notificationManagerCompat.notify(getNotificationID(), notification);

                        // UPDATE NOTIFY
                        String path_notify = "Users/" + user + "/Notify";
                        String time = dateFormat.format(date);
                        DatabaseReference mRefNotify = FirebaseDatabase.getInstance().getReference(path_notify);
                        mRefNotify.child(time).child("id").setValue(id);
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

    private int getNotificationID() {
        return (int) new Date().getTime();
    }

    @NonNull
    @Override
    public OrderListItemAdapter.OrderListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_item,parent,false);

        return new OrderListItemAdapter.OrderListViewholder(view);
    }

    public class OrderListViewholder extends RecyclerView.ViewHolder {
        TextView id, total, delivery, totalBill, orderTime;
        ConstraintLayout layout;
        Button cancel;
        public OrderListViewholder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            total = itemView.findViewById(R.id.total);
            delivery = itemView.findViewById(R.id.delivery);
            totalBill = itemView.findViewById(R.id.totalBill);
            orderTime = itemView.findViewById(R.id.orderTime);
            layout = itemView.findViewById(R.id.item_recycler_view);
            cancel = itemView.findViewById(R.id.btnCancel);

        }
    }
}
