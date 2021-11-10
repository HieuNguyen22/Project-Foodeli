package com.example.foodorder.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.other_activities.DishList;
import com.example.foodorder.R;
import com.example.foodorder.model.Categories;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private Context mContext;
    private List<Categories> CategoriesList;

    public CategoriesAdapter(Context mContext, List<Categories> CategoriesList) {
        this.mContext = mContext;
        this.CategoriesList = CategoriesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        ImageView igImage;
        ConstraintLayout color;
        public ViewHolder(View itemView) {
            super(itemView);

            igImage = itemView.findViewById(R.id.image_category);
            tvName = itemView.findViewById(R.id.name_category);
            tvAmount = itemView.findViewById(R.id.amount);
            color = itemView.findViewById(R.id.category_recycler_view);

        }
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_categories,parent,false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        Categories category = CategoriesList.get(position);

        holder.igImage.setImageResource(category.getImage());
        holder.tvName.setText(category.getName_category());
        holder.tvAmount.setText(category.getAmount());
        holder.color.setBackgroundResource(category.getColor());



        // EVENT LAYOUT
        holder.color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = category.getName_category();
//                String id = String.valueOf(holder.getAdapterPosition());
                Toast.makeText(view.getContext(), "Name: "+ name, Toast.LENGTH_SHORT).show();
                Fragment fragment = new DishList();
                //
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
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

    @Override
    public int getItemCount() {
        return CategoriesList.size();
    }

}
