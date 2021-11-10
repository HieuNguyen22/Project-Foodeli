package com.example.foodorder.other_activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityProfileBinding;
import com.example.foodorder.tab_fragment.TabOrderActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    Button btn;
    ActivityProfileBinding binding;
    FirebaseAuth fAuth;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String email = User.getEmail();
    String user = email.substring(0,email.indexOf("@"));

    DatabaseReference reference;
    ValueEventListener mListener;

    String path = "Avatars/"+user+"/avatar_new.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        fAuth = FirebaseAuth.getInstance();

//        Intent intent = getIntent();
//        String user = intent.getStringExtra("user");
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String email = User.getEmail();
        String user = email.substring(0,email.indexOf("@"));

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullnameFromDB = snapshot.child(user).child("fullname").getValue(String.class);
                String emailFromDB = snapshot.child(user).child("email").getValue(String.class);
                String phoneFromDB = snapshot.child(user).child("phone").getValue(String.class);
                String imageFromDB = snapshot.child(user).child("avatar").getValue(String.class);
                String locationFromDB = snapshot.child(user).child("location").getValue(String.class);

                binding.profile.setText(fullnameFromDB);
                binding.fullname.setText(fullnameFromDB);
                binding.mail.setText(emailFromDB);
                binding.phone.setText(phoneFromDB);
                binding.currentLocation.setText(locationFromDB);
                Picasso.get().load(imageFromDB).into(binding.avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        binding.yourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TabOrderActivity.class);
                startActivity(intent);
            }
        });

        // CHANGE AVATAR
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });

        // CHANGE INFO
        changeInfo();

        // ADD LOCATION
        addLocation();
        
        // CHANGE PASSWORD
        changePassword();
    }

    private void changePassword() {
        binding.changPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
    }

    private void addLocation() {
        binding.addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeInfo() {
        binding.updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.updateInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.updateInfo.setVisibility(View.INVISIBLE);
                        binding.undoChange.setVisibility(View.VISIBLE);
                        binding.fullname.setVisibility(View.INVISIBLE);
                        binding.phone.setVisibility(View.INVISIBLE);
                        binding.edtFullname.setVisibility(View.VISIBLE);
                        binding.edtPhone.setVisibility(View.VISIBLE);
                        binding.btnUpdate.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = binding.edtFullname.getText().toString().trim();
                String newPhone = binding.edtPhone.getText().toString().trim();

                if(newName.isEmpty()) {
                    binding.edtFullname.setError("Fullname is required!");
                    return;
                }
                if(newPhone.isEmpty()) {
                    binding.edtFullname.setError("Phone number is required!");
                    return;
                }

                // UPDATE TO USERS
                DatabaseReference mRefUpdate = FirebaseDatabase.getInstance().getReference("Users/"+user);
                mRefUpdate.child("fullname").setValue(newName);
                mRefUpdate.child("phone").setValue(newPhone);

                // UPDATE TO CHATS
                DatabaseReference mRefChat = FirebaseDatabase.getInstance().getReference("Chats/hieunt2207/"+user);
                mRefChat.child("name").setValue(newName);

                binding.updateInfo.setVisibility(View.VISIBLE);
                binding.undoChange.setVisibility(View.INVISIBLE);
                binding.fullname.setVisibility(View.VISIBLE);
                binding.phone.setVisibility(View.VISIBLE);
                binding.edtFullname.setVisibility(View.INVISIBLE);
                binding.edtPhone.setVisibility(View.INVISIBLE);
                binding.btnUpdate.setVisibility(View.GONE);
            }
        });

        binding.undoChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.updateInfo.setVisibility(View.VISIBLE);
                binding.undoChange.setVisibility(View.INVISIBLE);
                binding.fullname.setVisibility(View.VISIBLE);
                binding.phone.setVisibility(View.VISIBLE);
                binding.edtFullname.setVisibility(View.INVISIBLE);
                binding.edtPhone.setVisibility(View.INVISIBLE);
                binding.btnUpdate.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                // UPLOAD IMAGE TO FIREBASE
                StorageReference mStoreRef = FirebaseStorage.getInstance().getReference();
                StorageReference fileRef = mStoreRef.child(path);
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                         UPDATE URL TO DATABASE
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                String pathData = "Users/"+user+"/avatar";
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(pathData);
                                mRef.setValue(url);

                                // UPDATE TO CHATS
                                DatabaseReference mRefChat = FirebaseDatabase.getInstance().getReference("Chats/hieunt2207/"+user);
                                mRefChat.child("avatar").setValue(url);

                                Toast.makeText(Profile.this, "Uploaded "+url, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
//                        Toast.makeText(Profile.this, "Uploaded "+imageUri, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(mListener);
    }
}