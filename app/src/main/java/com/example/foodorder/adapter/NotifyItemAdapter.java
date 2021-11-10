package com.example.foodorder.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.DonHang;
import com.example.foodorder.other_activities.DonHangCanceled;
import com.example.foodorder.R;
import com.example.foodorder.model.NotifyItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class NotifyItemAdapter extends FirebaseRecyclerAdapter<NotifyItems, NotifyItemAdapter.NotifyViewholder> {

    public NotifyItemAdapter(@NonNull FirebaseRecyclerOptions<NotifyItems> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotifyItemAdapter.NotifyViewholder holder, int position, @NonNull NotifyItems model) {

        String id = model.getId();
        String time_order = model.getTime_order();
        String time_cancel = model.getTime_cancel();

        if(time_order != null){
            holder.id.setText(model.getId());
            holder.time.setText(time_order);
            holder.tvTime.setText("Ordered at");

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DonHang.class);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    intent.putExtra("id",id);
                    activity.startActivity(intent);
                }
            });
        }
        else if(time_cancel != null){
            holder.id.setText(model.getId());
            holder.time.setText(time_cancel);
            holder.tvTime.setText("Canceled at");

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DonHangCanceled.class);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    intent.putExtra("id",id);
                    activity.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public NotifyItemAdapter.NotifyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_items,parent,false);
        return new NotifyItemAdapter.NotifyViewholder(view);
    }

    public class NotifyViewholder extends RecyclerView.ViewHolder {
        TextView id, time, tvTime;
        ConstraintLayout layout;
        public NotifyViewholder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            time = itemView.findViewById(R.id.time);
            tvTime = itemView.findViewById(R.id.tvTime);
            layout = itemView.findViewById(R.id.item_notify_recycler);

        }
    }
}
