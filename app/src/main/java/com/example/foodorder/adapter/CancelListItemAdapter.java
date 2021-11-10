package com.example.foodorder.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.DonHangCanceled;
import com.example.foodorder.R;
import com.example.foodorder.model.CanceledListItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CancelListItemAdapter extends FirebaseRecyclerAdapter<CanceledListItem,CancelListItemAdapter.CancelViewholder> {

    String id;
    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));

    public CancelListItemAdapter(@NonNull FirebaseRecyclerOptions<CanceledListItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CancelListItemAdapter.CancelViewholder holder, int position, @NonNull CanceledListItem model) {
        holder.id.setText(model.getId());
        holder.total.setText(model.getTotal_money());
        holder.delivery.setText(model.getDelivery_money());
        holder.totalBill.setText(model.getTotal_bill_money());
        holder.orderTime.setText(model.getTime_order());
        holder.cancelTime.setText(model.getTime_cancel());

        id = model.getId();

        // ON CLICK LAYOUT
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(view.getContext(), DonHangCanceled.class);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                intent.putExtra("id",id);
                activity.startActivity(intent);
            }
        });

        holder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "This function is still developed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public CancelListItemAdapter.CancelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.canceled_list_item,parent,false);

        return new CancelListItemAdapter.CancelViewholder(view);
    }

    public class CancelViewholder extends RecyclerView.ViewHolder {
        TextView id, total, delivery, totalBill, orderTime,cancelTime;
        ConstraintLayout layout;
        Button order;
        public CancelViewholder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            total = itemView.findViewById(R.id.total);
            delivery = itemView.findViewById(R.id.delivery);
            totalBill = itemView.findViewById(R.id.totalBill);
            orderTime = itemView.findViewById(R.id.orderTime);
            cancelTime = itemView.findViewById(R.id.cancelTime);
            layout = itemView.findViewById(R.id.item_recycler_view);
            order = itemView.findViewById(R.id.btnOrder);
        }
    }
}
