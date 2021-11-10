package com.example.foodorder.other_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityRegisterBinding;
import com.example.foodorder.model.UsersHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth fAuth;
    FirebaseDatabase fDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        fAuth = FirebaseAuth.getInstance();

        binding.alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = binding.fullname.getText().toString().trim();
                String pass = binding.password.getText().toString().trim();
                String confirmPass = binding.confirmPassword.getText().toString().trim();
                String phone = binding.phone.getText().toString().trim();
                String mail = binding.email.getText().toString().trim();
                String default_avt = "https://firebasestorage.googleapis.com/v0/b/food-ordering-app-695b2.appspot.com/o/default_avatar.png?alt=media&token=27254a19-b846-4cf0-bf0e-0198818f4d27";


                binding.progressBar.setVisibility(View.VISIBLE);

                // CHECK FULLNAME
                if(fullname.isEmpty() == true) {
                    binding.fullname.setError("Fullname is required!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // CHECK USERNAME
//                if(username.isEmpty() == true) {
//                    binding.username.setError("Username is required!");
//                    binding.progressBar.setVisibility(View.INVISIBLE);
//                    return;
//                }

                // CHECK MAIL
                if(mail.isEmpty() == true) {
                    binding.email.setError("Email is required!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // CHECK PASSWORD
                if(pass.isEmpty() == true) {
                    binding.password.setError("Password is required!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(pass.length() < 6) {
                    binding.password.setError("Password must be >= 6 characters");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // CHECK CONFIRM PASSWORD
                if(confirmPass.isEmpty() == true) {
                    binding.confirmPassword.setError("Confirm Password is required!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(confirmPass.equals(pass) == false){
                    binding.confirmPassword.setError("Confirm password is different from the password!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // CHECK PHONE
                if(phone.isEmpty()) {
                    binding.phone.setError("Phone number is required!");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                //CREATE USERNAME
                int index = mail.indexOf('@');
                String username = mail.substring(0,index);

                // AUTHENTICATE EMAIL
                fAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Sign up successfully!", Toast.LENGTH_LONG).show();

                            // SAVE DATA IN FIREBASE
                            fDatabase = FirebaseDatabase.getInstance();
                            reference = fDatabase.getReference("Users");

                            UsersHelper user = new UsersHelper(fullname,username,mail,phone,default_avt);
                            reference.child(username).setValue(user);

                            // UPDATE CHATS
                            String path_user = "Users/hieunt2207";
                            DatabaseReference mRefUser = FirebaseDatabase.getInstance().getReference(path_user);
                            DatabaseReference mRefUser2 = FirebaseDatabase.getInstance().getReference("Users/"+username);

                            DatabaseReference mRefChat = FirebaseDatabase.getInstance().getReference("Chats");

                            mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String avt = snapshot.child("avatar").getValue(String.class);
                                    String name = snapshot.child("fullname").getValue(String.class);

                                    mRefChat.child(username).child("hieunt2207").child("name").setValue(name);
                                    mRefChat.child(username).child("hieunt2207").child("username").setValue("hieunt2207");
                                    mRefChat.child(username).child("hieunt2207").child("avatar").setValue(avt);

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            mRefUser2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String avt = snapshot.child("avatar").getValue(String.class);
                                    String name = snapshot.child("fullname").getValue(String.class);

                                    mRefChat.child("hieunt2207").child(username).child("name").setValue(name);
                                    mRefChat.child("hieunt2207").child(username).child("username").setValue(username);
                                    mRefChat.child("hieunt2207").child(username).child("avatar").setValue(avt);

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            // CHANGE TO LOGIN SCREEN
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Register.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            binding.progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });
    }
}