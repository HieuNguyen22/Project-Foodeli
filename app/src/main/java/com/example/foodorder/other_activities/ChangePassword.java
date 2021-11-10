package com.example.foodorder.other_activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    ActivityChangePasswordBinding binding;

    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
    String emailUser = User.getEmail();
    String user = emailUser.substring(0,emailUser.indexOf("@"));

    String email, password, newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        // ON CLICK UPDATE
        onClickUpdate();

        // ON CLICK BACK
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickUpdate() {
        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = binding.email.getText().toString().trim();
                password = binding.password.getText().toString().trim();
                newPassword = binding.newPassword.getText().toString().trim();

                // CHECK EMAIL
                if(email.isEmpty()) {
                    binding.email.setError("Email is required!");
                    return;
                }
                if(!email.equals(emailUser)) {
                    binding.email.setError("Email is wrong!");
                    return;
                }

                // CHECK PASSWORD
                if(password.isEmpty()) {
                    binding.password.setError("Password is required!");
                    return;
                }

                // CHECK NEWPASSWORD
                if(newPassword.isEmpty()) {
                    binding.newPassword.setError("New password is required!");
                    return;
                }

                //  CHECK ACCOUNT
                checkAccount();
            }
        });
    }

    private void checkAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // UPDATE PASSWORD
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getBaseContext(), "Updated password", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Wrong email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}