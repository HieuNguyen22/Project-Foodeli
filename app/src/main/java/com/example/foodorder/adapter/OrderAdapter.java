package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.model.OrderItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class OrderAdapter extends FirebaseRecyclerAdapter<OrderItems,OrderAdapter.OrderViewholder> {
    private List<OrderItems> orderItemsList;

    public OrderAdapter(@NonNull FirebaseRecyclerOptions<OrderItems> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderAdapter.OrderViewholder holder, int position, @NonNull OrderItems model) {
        holder.tvName.setText(model.getName());
        holder.tvAmount.setText(String.valueOf(model.getQty()));
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.oder_dishes_items,parent,false);

        return new OrderAdapter.OrderViewholder(view);
    }

    public class OrderViewholder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        public OrderViewholder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.name_order);
            tvAmount = itemView.findViewById(R.id.qty_order);
        }
    }






//    private Context mContext;
//    private List<OrderItems> Orderlist;
//
//    public OrderAdapter(Context mContext, List<OrderItems> Orderlist) {
//        this.mContext = mContext;
//        this.Orderlist = Orderlist;
//    }
//
//    @Override
//    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View view = inflater.inflate(R.layout.oder_dishes_items, parent, false);
//
//        ViewHolder vh = new ViewHolder(view);
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
//        OrderItems order = Orderlist.get(position);
//
//        holder.tvName.setText(order.getName());
//        holder.tvAmount.setText(String.valueOf(order.getQty()));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return Orderlist.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvName, tvAmount;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            tvName = itemView.findViewById(R.id.name_order);
//            tvAmount = itemView.findViewById(R.id.qty_order);
//
//        }
//    }
}
