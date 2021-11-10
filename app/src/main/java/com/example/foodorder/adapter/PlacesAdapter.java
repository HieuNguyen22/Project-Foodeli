package com.example.foodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.model.Places;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private Context mContext;
    private List<Places> placesList;

    public PlacesAdapter(Context mContext, List<Places> placesList) {
        this.mContext = mContext;
        this.placesList = placesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePlace, tvDetail,tvDistance,tvTime;
        ImageView igImage;
        public ViewHolder(View itemView) {
            super(itemView);

            igImage = itemView.findViewById(R.id.image_place);
            tvNamePlace = itemView.findViewById(R.id.name_place);
            tvDetail = itemView.findViewById(R.id.detail_place);
            tvDistance = itemView.findViewById(R.id.distance_place);
            tvTime = itemView.findViewById(R.id.time_place);

        }
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_places,parent,false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.ViewHolder holder, int position) {
        Places place = placesList.get(position);

        holder.igImage.setImageResource(place.getImage());
        holder.tvNamePlace.setText(place.getName_place());
        holder.tvDetail.setText(place.getDetail());
        holder.tvDistance.setText(place.getDistance());
        holder.tvTime.setText(place.getTime());

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

}
