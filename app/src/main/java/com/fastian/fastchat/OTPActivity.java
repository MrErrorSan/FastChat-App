package com.fastian.fastchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.fastian.fastchat.databinding.ActivityOTPBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;

public class OTPActivity extends AppCompatActivity {
    ActivityOTPBinding binding;
    FirebaseAuth auth;
    String verificationId;
    String Email;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();

        Objects.requireNonNull(getSupportActionBar()).hide();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        Email= getIntent().getStringExtra("email");
        binding.phoneLbl.setText("Verify " + phoneNumber);

        binding.otpView.requestFocus();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {}
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {}
                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        verificationId = verifyId;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {}
            @Override
            public void onOTPComplete(String otp) {
                PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(verificationId, otp);
                auth.signInWithCredential(credentials).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(OTPActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                        intent.putExtra("email",Email);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Toast.makeText(OTPActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
