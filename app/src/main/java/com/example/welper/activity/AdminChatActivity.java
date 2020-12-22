package com.example.welper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.welper.R;
import com.example.welper.adaptor.MessageAdaptor;
import com.example.welper.base.Message;
import com.example.welper.base.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminChatActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView profileName, messageEditTxt;
    ImageButton btnSend;

    FirebaseUser user;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    CollectionReference dbProfile = FirebaseFirestore.getInstance().collection("user");
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    static Profile profile;
    MessageAdaptor messageAdaptor;
    List<Message> messageList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        profilePic = findViewById(R.id.profile_picture_item);
        profileName = findViewById(R.id.username_item_name);
        messageEditTxt = findViewById(R.id.editText_message);
        btnSend = findViewById(R.id.btn_send);


        recyclerView = findViewById(R.id.messages_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // load profile information
        Intent intent = getIntent();
        if (intent.getStringExtra("userId") != null &&
                !intent.getStringExtra("userId").equalsIgnoreCase("")) {
            Log.d("TAG", "onCreate: " + 1);
            String profileId = intent.getStringExtra("userId");
            getProfileInfo(profileId);
            setupChat(profileId);
        }

        if (intent.getStringExtra("contact") != null &&
                !intent.getStringExtra("contact").equalsIgnoreCase("")) {
            Log.d("TAG", "onCreate: " + 2);
            String profileId = intent.getStringExtra("id");
            String itemId = intent.getStringExtra("contact");
            String type = intent.getStringExtra("type");
            String send = "@" + type + "/" + itemId;

            getProfileInfo(profileId);
            setupChat(profileId);
            sendMessageItem(send);
        }

        btnSend.setOnClickListener(view -> {
            if (!messageEditTxt.getText().toString().equalsIgnoreCase("")) {
                sendMessage(messageEditTxt.getText().toString());
            }
        });


    }

    //handle send message
    private void sendMessage(String toString) {
        Message message = new Message(user.getUid(), profile.getUserId(), toString);

        if (profile.getUserId().equals("sQvIx6CUeYQKCSR0HZI4O6hrltc2"))
            reference.child(user.getUid()).push().setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        messageEditTxt.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("TAG", "sendMessage: " + e);
                    });
        else
            reference.child(profile.getUserId()).push().setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        messageEditTxt.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("TAG", "sendMessage: " + e);
                    });
    }

    // handle send message with no profile
    private void sendMessageItem(String toString) {
        Message message = new Message(user.getUid(), toString);

        reference.child(user.getUid()).push().setValue(message)
                .addOnSuccessListener(aVoid -> {
                    messageEditTxt.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "sendMessage: " + e);
                });
    }

    //get all history message for specific userId
    private void setupChat(String profileId) {
        messageList = new ArrayList<>();
        reference.child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snapshotNew : snapshot.getChildren()) {
                    Message message = snapshotNew.getValue(Message.class);
                    messageList.add(message);
                    messageAdaptor = new MessageAdaptor(AdminChatActivity.this, messageList);
                    recyclerView.setAdapter(messageAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Get User information picture and Name of the Person
    private void getProfileInfo(String userId) {
        if (user.getUid().equals(userId)) {
            mStorageRef.child("profile/picture/sQvIx6CUeYQKCSR0HZI4O6hrltc2.jpg").getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri)
                                .into(profilePic);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    });

            dbProfile.document("sQvIx6CUeYQKCSR0HZI4O6hrltc2").get()
                    .addOnSuccessListener(documentSnapshot -> {
                        profile = documentSnapshot.toObject(Profile.class);
                        profileName.setText(profile.getProfileName());
                    })
                    .addOnFailureListener(e -> {
                        Log.d("TAG", "getProfileInfo: " + e);
                    });
        } else {
            mStorageRef.child("profile/picture/" + userId + ".jpg").getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri)
                                .into(profilePic);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    });

            dbProfile.document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        profile = documentSnapshot.toObject(Profile.class);
                        profileName.setText(profile.getProfileName());
                    })
                    .addOnFailureListener(e -> {
                        Log.d("TAG", "getProfileInfo: " + e);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}