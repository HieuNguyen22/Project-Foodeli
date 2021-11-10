package com.example.foodorder.other_activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    DatabaseReference mRef;
    ValueEventListener mListenerMap;

    // MAP
    // Initialize variable
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Assign variable
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

//        // Initialize fused location
//        client = LocationServices.getFusedLocationProviderClient(this);
//
//        // Check permission
//        if(ActivityCompat.checkSelfPermission(MapsActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            // When permission granted
//            // Call method
//            getCurentLocation();
//        }
//        else {
//            // When permission denied
//            // Request permission
//            ActivityCompat.requestPermissions(MapsActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
//        }

        onClickBack();

        onSearch();

        onSave();

    }

    private void onSave() {
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String email = User.getEmail();
                String user = email.substring(0,email.indexOf("@"));

                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users");

                mRef.child(user).child("location").setValue(binding.typeLocation.getText().toString());

//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result",binding.typeLocation.getText().toString());
//                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void onSearch() {
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = binding.typeLocation.getText().toString();
                List<Address> addressList = null;
                MarkerOptions options;

                if(!address.isEmpty()){
                    Geocoder geocoder = new Geocoder(getBaseContext());
                    try{
                        addressList = geocoder.getFromLocationName(address,6);

                        if(addressList != null){
                            for(int i = 0; i < addressList.size(); i++){
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                options = new MarkerOptions().position(latLng).title("I'm here");

                                mMap.addMarker(options);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                            }
                        }
                        else Toast.makeText(getBaseContext(), "Location is not found", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getBaseContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // GET CURENT LOCATION
//    private void getCurentLocation() {
//        //Initialize task location
//        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                // When success
//                if(location != null){
//                    // Sync map
//                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(@NonNull GoogleMap googleMap) {
//                            // Initialize lat lng
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            // Create marker options
//                            MarkerOptions options = new MarkerOptions().position(latLng).title("I'm here");
//
//                            // Zoom map
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                            // Add marker on map
//                            googleMap.addMarker(options);
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 44) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // When permission granted
//                // Call method
//                getCurentLocation();
//            }
//        }
//    }

    private void onClickBack() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));

        /////
        String path = "Users/"+user+"/location";
        mRef = FirebaseDatabase.getInstance().getReference(path);

        // SET LOCATION
        mRef.addListenerForSingleValueEvent(mListenerMap = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.getValue(String.class);
                List<Address> addressList = null;
                MarkerOptions options;

                if(address != null){
                    Geocoder geocoder = new Geocoder(getBaseContext());
                    try{
                        addressList = geocoder.getFromLocationName(address,1);

                        if(addressList != null){
                            for(int i = 0; i < addressList.size(); i++){
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                options = new MarkerOptions().position(latLng).title(address);

                                mMap.addMarker(options);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                            }
                        }
                        else Toast.makeText(getBaseContext(), "Location is not found", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getBaseContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show();
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
        mRef.removeEventListener(mListenerMap);
    }
}