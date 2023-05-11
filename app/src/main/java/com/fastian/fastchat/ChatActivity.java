package com.fastian.fastchat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.fastian.fastchat.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderUid;
    String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        messages = new ArrayList<>();
        Objects.requireNonNull(getSupportActionBar()).hide();

        String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        binding.name.setText(name);
        if(!Objects.equals(profile, "No Image"))
            Glide.with(ChatActivity.this).load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.imageView2.setOnClickListener(v -> finish());

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            assert message != null;
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(v -> {
            String messageTxt = binding.messageBox.getText().toString();

            Date date = new Date();
            Message message = new Message(messageTxt, senderUid, date.getTime());
            binding.messageBox.setText("");

            String randomKey = database.getReference().push().getKey();

            HashMap<String, Object> lastMsgObj = new HashMap<>();
            lastMsgObj.put("lastMsg", message.getMessage());
            lastMsgObj.put("lastMsgTime", date.getTime());

            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

            assert randomKey != null;
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message).addOnSuccessListener(aVoid1 -> {
                            }));
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
