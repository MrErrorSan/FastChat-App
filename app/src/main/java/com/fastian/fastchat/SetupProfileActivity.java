package com.fastian.fastchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.fastian.fastchat.databinding.ActivitySetupProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    FirebaseStorage storage;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        database= FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        //noinspection deprecation
        dialog = new ProgressDialog(this);
        //noinspection deprecation
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            //noinspection deprecation
            startActivityForResult(intent,45);
        });
        binding.edit.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            //noinspection deprecation
            startActivityForResult(intent,45);
        });
        binding.continueBtn.setOnClickListener(view -> {
            dialog.show();
            String name=binding.nameBox.getText().toString();
            if(name.isEmpty())
            {
                binding.nameBox.setError("Please type a Name");
                return;
            }
            if(selectedImage != null){
                StorageReference reference= storage.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
                reference.putFile(selectedImage).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            String uid = auth.getUid();
                            String phone = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                            String name1 = binding.nameBox.getText().toString();
                            User user = new User(uid, name1, phone,getIntent().getStringExtra("email"), imageUrl);

                            database.getReference().child("users").child(uid).setValue(user)
                                    .addOnSuccessListener(unused -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(SetupProfileActivity.this, SetupProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(SetupProfileActivity.this, "Failed to Setup Profile", Toast.LENGTH_LONG).show();
                                    });
                        });
                    }
                });
            }else{
                String uid = auth.getUid();
                String phone = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                String name1 = binding.nameBox.getText().toString();
                String pic="No Image";
                User user = new User(uid, name1, phone,getIntent().getStringExtra("email"), pic);

                assert uid != null;
                database.getReference().child("users").child(uid).setValue(user)
                        .addOnSuccessListener(unused -> {
                            dialog.dismiss();
                            Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Intent intent = new Intent(SetupProfileActivity.this, SetupProfileActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(SetupProfileActivity.this, "Failed to Setup Profile", Toast.LENGTH_LONG).show();
                        });
            }

        });
    }
    @Override
    protected void onActivityResult(int resultCode, int requestCode, @Nullable Intent data){
        super.onActivityResult(resultCode, requestCode, data);

        if(data != null){
            if(data.getData() != null){
                binding.imageView.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }

    }

}
