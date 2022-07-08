package com.example.foodorder.other_activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();


        // AUTOMATIC SIGN IN
        if(fAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), NavigateActivity.class);
            startActivity(intent);
            finish();
        }

        //CREATE ACCOUNT
        binding.createAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        // SIGN IN
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = binding.email.getText().toString().trim();
                String pass = binding.password.getText().toString().trim();
                String mail = user;

                if(user.isEmpty() == true) {
                    binding.email.setError("Email is required!");
                    return;
                }

                if(pass.isEmpty() == true) {
                    binding.password.setError("Password is required!");
                    return;
                }

                if(pass.length() < 6) {
                    binding.password.setError("Password must be >= 6 characters");
                    return;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                // AUTHENTICATE THE USER USING EMAIL
                fAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(getApplicationContext(), NavigateActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            binding.progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        // FORGOT PASSWORD
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                resetMail.setMaxLines(1);
                AlertDialog.Builder resetPasswordDialog = new AlertDialog.Builder(view.getContext());
                resetPasswordDialog.setTitle("Reset Password?");
                resetPasswordDialog.setMessage("Enter your email to received reset link.");
                resetPasswordDialog.setView(resetMail);

                resetPasswordDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Reset link was sent to your email.", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error!"+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                resetPasswordDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Close the dialog

                    }
                });

                resetPasswordDialog.create().show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (fAuth.getCurrentUser().getEmail() != null) {
//            Intent intent = new Intent(this,test.class);
//            startActivity(intent);
//        }
    }
}
