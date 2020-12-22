package com.example.welper.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.welper.R;
import com.example.welper.activity.AdminChatActivity;
import com.example.welper.base.Image;
import com.example.welper.base.Profile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.ViewHolder> {

    private Context context;
    private List<Profile> mProfile;

    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("profile");
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public UserAdaptor(Context context, List<Profile> mProfile){
        this.context = context;
        this.mProfile = mProfile;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
            return new UserAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile profile = mProfile.get(position);
        holder.username.setText(profile.getProfileName());

        mStorageRef.child("profile/picture/" + profile.getUserId() + ".jpg").getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(holder.itemView)
                            .load(uri)
                            .into(holder.profilePicture);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AdminChatActivity.class);
            intent.putExtra("userId", profile.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_item);
            profilePicture = itemView.findViewById(R.id.profile_picture_item);
        }
    }
}
