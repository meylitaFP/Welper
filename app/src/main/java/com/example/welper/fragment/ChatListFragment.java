package com.example.welper.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.welper.R;
import com.example.welper.activity.AdminActivity;
import com.example.welper.activity.AdminChatActivity;
import com.example.welper.adaptor.UserAdaptor;
import com.example.welper.base.Profile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdaptor userAdaptor;
    private static List<Profile> listProfile  = new ArrayList<>();

    ListenerRegistration registration;
    CollectionReference dbProfile = FirebaseFirestore.getInstance().collection("user");

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_customer, container, false);

        recyclerView = v.findViewById(R.id.recycler_image_colletion_customer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadUserChat();
        return v;
    }

    // listed all user for Admin user list view
    private void loadUserChat() {
        registration = dbProfile.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if(profile.getLevel() != 0)
                        listProfile.add(profile);
                }
                userAdaptor = new UserAdaptor(getContext(), listProfile);
                recyclerView.setAdapter(userAdaptor);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
        listProfile.clear();
    }
}
