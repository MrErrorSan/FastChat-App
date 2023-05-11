package com.fastian.fastchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fastian.fastchat.databinding.ActivityPhoneNoBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class PhoneNoActivity extends AppCompatActivity {

    ActivityPhoneNoBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPhoneNoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth= FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
        {
            Intent intent = new Intent(PhoneNoActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.emailBox.requestFocus();
        binding.continueBtn.setOnClickListener(v -> {

            String email=binding.emailBox.getText().toString();
            String phone=binding.phoneBox.getText().toString();
            if(email.isEmpty())
            {
                binding.emailBox.setError("Please type Email");
                return;
            }
            if(phone.isEmpty())
            {
                binding.phoneBox.setError("Please type phoneNo.");
                return;
            }
            Intent intent = new Intent(PhoneNoActivity.this,OTPActivity.class);
            intent.putExtra("phoneNumber",binding.phoneBox.getText().toString());
            intent.putExtra("email",binding.emailBox.getText().toString());

            startActivity(intent);
        });
    }
}
