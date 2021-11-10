package com.example.foodorder.main_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.model.Categories;
import com.example.foodorder.other_activities.DishDetail;
import com.example.foodorder.other_activities.MapsActivity;
import com.example.foodorder.other_activities.Profile;
import com.example.foodorder.R;
import com.example.foodorder.adapter.CategoriesAdapter;
import com.example.foodorder.adapter.DishAdapter;
import com.example.foodorder.adapter.PlacesAdapter;
import com.example.foodorder.databinding.FragmentHomeBinding;
import com.example.foodorder.model.Dishes;
import com.example.foodorder.model.Places;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    int LAUNCH_MAP_ACTIVITY = 1;

    FragmentHomeBinding binding;

    List<String> listDishes = new ArrayList<String>();
    List<Places> listPlaces;
    List<Categories> listCategories;

    PlacesAdapter adapterPlaces;
    CategoriesAdapter adapterCategories;
    DishAdapter adapterPopular, adapterRecommend, adapterNew;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, mRefLocation, mRefName;
    ValueEventListener mListerLocation,mListenerName;

    DatabaseReference mRefAvatar;
    ValueEventListener avatarEvent;


    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));



    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        // SET LOCATION
        setViewLocation();

        // SET PROFILE
        setViewProfile();


        // CLICK ON PROFILE
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), Profile.class);
                intent.putExtra("user",user);
                startActivity(intent);

            }
        });


        // CLICK ON LOCATION

        binding.pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MapsActivity.class);
//                startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);
                startActivity(intent);
            }
        });


        //RECYCLER VIEW ----------------------------------------------

        placesView();
        categoriesView();
        newView();
        popularView();
        recommendView();


        // SEARCH
        searchDish();
        int index;
        ArrayAdapter adapterDishes = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,listDishes);

        binding.search.setAdapter(adapterDishes);
        binding.search.setThreshold(1);

        binding.search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dishCheck = binding.search.getText().toString();
                int id = listDishes.indexOf(dishCheck);
                if (id >= 0) {
                    // CHANGE TO DETAIL
                    Toast.makeText(getContext(), "Vi tri: " + id, Toast.LENGTH_SHORT).show();
                    Fragment fragment = new DishDetail();
                    //
                    Bundle bundle = new Bundle();
                    bundle.putString("pos", String.valueOf(id));
                    fragment.setArguments(bundle);
                    // load fragment
                    AppCompatActivity activity = (AppCompatActivity) getContext();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return root;
    }

    private void setViewProfile() {
        // SET AVATAR
        String path_avatar = "Users/"+user+"/avatar";
        mRefAvatar = FirebaseDatabase.getInstance().getReference(path_avatar);
        mRefAvatar.addValueEventListener(avatarEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = snapshot.getValue(String.class);
                if(url != null)
                    Picasso.get().load(url).into(binding.profile);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // SET NAME
        String path_name = "Users/"+user+"/fullname";
        mRefName = FirebaseDatabase.getInstance().getReference(path_name);
        mRefName.addValueEventListener(mListenerName = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if(name!= null)
                    binding.userName.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setViewLocation() {
        final String[] str = new String[1];
        String path = "Users/"+user+"/location";
        mRefLocation = FirebaseDatabase.getInstance().getReference(path);
        mRefLocation.addValueEventListener(mListerLocation = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String location = snapshot.getValue(String.class);
                if(location!= null)
                    binding.pos.setText(location);
                else binding.pos.setText("Error!!");;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void searchDish() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Dishes");
        Query mList = mRef.orderByChild("name");

        mList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dish = dataSnapshot.child("name").getValue().toString();
                    if(!listDishes.contains(dish))  listDishes.add(dish);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mRefLocation.removeEventListener(mListerLocation);
        mRefAvatar.removeEventListener(avatarEvent);
        mRefName.removeEventListener(mListenerName);
    }

    @Override public void onStart()
    {
        super.onStart();
        adapterPopular.startListening();
        adapterRecommend.startListening();
        adapterNew.startListening();
    }

//    @Override public void onStop()
//    {
//        super.onStop();
//
//    }


    public void placesView(){
        RecyclerView viewPlaces;
        viewPlaces = binding.recyclerPopularNear;
        listPlaces = new ArrayList<>();
        addPlaces();
        adapterPlaces = new PlacesAdapter(getContext(), listPlaces);

        viewPlaces.setAdapter(adapterPlaces);
        viewPlaces.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    public void categoriesView(){
        RecyclerView viewCategories;
        viewCategories = binding.recyclerCategories;
        listCategories = new ArrayList<>();
        addCategories();
        adapterCategories = new CategoriesAdapter(getContext(), listCategories);

        viewCategories.setAdapter(adapterCategories);
        viewCategories.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    public void popularView(){
        RecyclerView viewPopular;

        FirebaseRecyclerOptions<Dishes> options_popular;
        reference = FirebaseDatabase.getInstance().getReference("Popular");
        viewPopular = binding.recyclerPopular;

        viewPopular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        options_popular = new FirebaseRecyclerOptions.Builder<Dishes>()
                .setQuery(reference,Dishes.class)
                .build();

        adapterPopular = new DishAdapter(options_popular);

        viewPopular.setAdapter(adapterPopular);
    }


    public void recommendView(){
        RecyclerView viewRecommend;

        FirebaseRecyclerOptions<Dishes> options_recommend;
        reference = FirebaseDatabase.getInstance().getReference("Recommend");
        viewRecommend = binding.recyclerRecommend;

        viewRecommend.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        options_recommend = new FirebaseRecyclerOptions.Builder<Dishes>()
                .setQuery(reference,Dishes.class)
                .build();

        adapterRecommend = new DishAdapter(options_recommend);

        viewRecommend.setAdapter(adapterRecommend);
    }

    public void newView(){
        RecyclerView viewNew;

        FirebaseRecyclerOptions<Dishes> options_new;
        reference = FirebaseDatabase.getInstance().getReference("New");
        viewNew = binding.recyclerNew;

        viewNew.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        options_new = new FirebaseRecyclerOptions.Builder<Dishes>()
                .setQuery(reference,Dishes.class)
                .build();

        adapterNew = new DishAdapter(options_new);

        viewNew.setAdapter(adapterNew);
    }





    //______________________________DATA_______________________________
    protected void addPlaces() {
        listPlaces.add((new Places(R.drawable.place_1, "Mc Donald's - Thai Ha", "Western cuisine, Fast Food, Burger", "300m", "28'")));
        listPlaces.add((new Places(R.drawable.place_2, "Pizza Hut - Nguyen Trai", "Western cuisine, Fast Food, Pizza", "1000m", "50'")));
        listPlaces.add((new Places(R.drawable.place_3, "Pizza Hut - Nguyen Trai", "Western cuisine, Fast Food, Pizza", "1000m", "50'")));

    }

    protected void addCategories() {
        listCategories.add((new Categories("Rice", "55 Places", R.drawable.icon_rice, R.drawable.ic_bg_red)));
        listCategories.add((new Categories("Street food", "55 Places", R.drawable.icon_street_food, R.drawable.ic_bg_green)));
        listCategories.add((new Categories("Burger", "55 Places", R.drawable.icon_burger, R.drawable.ic_bg_yellow)));
        listCategories.add((new Categories("Drink", "55 Places", R.drawable.icon_drink, R.drawable.ic_bg_blue)));
        listCategories.add((new Categories("Cusine", "55 Places", R.drawable.icon_cuisine, R.drawable.ic_bg_purple)));
        listCategories.add((new Categories("Dinner", "55 Places", R.drawable.icon_dinner, R.drawable.ic_bg_brown)));

    }

}