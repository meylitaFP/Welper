package com.example.welper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.welper.R;
import com.example.welper.adaptor.ManageDataHouseAdaptor;
import com.example.welper.adaptor.ManageDataLogoAdaptor;
import com.example.welper.adaptor.UserDataListAdaptor;
import com.example.welper.adaptor.UserDataListLogoAdaptor;
import com.example.welper.base.House;
import com.example.welper.base.Logo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListProductActivity extends AppCompatActivity {

    private static final String TAG = "Testing";
    ImageView linierLayot, gridLayout, searchLayout;
    LinearLayout linearLayout;
    EditText searchText;
    ImageButton searchBtn;
    private UserDataListAdaptor userHouseAdaptor;
    private UserDataListLogoAdaptor userLogoAdaptor;;
    static String defaultLayout = "linier";
    static String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        linierLayot = findViewById(R.id.view_model_list);
        gridLayout = findViewById(R.id.view_model_grid);
        linearLayout = findViewById(R.id.linear_search);
        searchText = findViewById(R.id.editText_message_search);
        searchBtn = findViewById(R.id.btn_search);
        searchLayout = findViewById(R.id.view_model_search);

        searchLayout.setOnClickListener(view -> {
            if(linearLayout.getVisibility() == View.VISIBLE)
                linearLayout.setVisibility(View.GONE);
            else
                linearLayout.setVisibility(View.VISIBLE);
        });

        searchBtn.setOnClickListener(view -> {
            String searchValue = searchText.getText().toString();
            if(!searchValue.equalsIgnoreCase("") || searchValue != null){
                Intent intent = getIntent();
                Log.d(TAG, "onBindViewHolder: " + searchValue);
                if (intent.getStringExtra("type") != null &&
                        !intent.getStringExtra("type").equalsIgnoreCase("")) {
                    try{
                        userHouseAdaptor.stopListening();
                    }catch (Exception e){
                        userLogoAdaptor.stopListening();
                    }
                    Log.d(TAG, "onBindViewHolder: " + searchValue);
                    type = intent.getStringExtra("type");
                    setAllRecycleView(type, defaultLayout, searchValue);
                    if(type.equalsIgnoreCase("house")){
                        userHouseAdaptor.startListening();
                    }else{
                        userLogoAdaptor.startListening();
                    }
                }
            }
        });

        Intent intent = getIntent();
        if (intent.getStringExtra("type") != null &&
                !intent.getStringExtra("type").equalsIgnoreCase("")) {
            type = intent.getStringExtra("type");
            setAllRecycleView(intent.getStringExtra("type"), defaultLayout, "");
        }
        Log.d(TAG, "onCreatebb: " + intent.getStringExtra("type"));


        //Handel multiple adaptor
        linierLayot.setOnClickListener(view -> {
            try{
                userHouseAdaptor.stopListening();
            }catch (Exception e){
                userLogoAdaptor.stopListening();
            }
            defaultLayout = "linier";
            Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_SHORT).show();
            setAllRecycleView(type, "linier", "");
            if(type.equalsIgnoreCase("house")){
                userHouseAdaptor.startListening();
                Toast.makeText(getApplicationContext(), "Reload House", Toast.LENGTH_SHORT).show();
            }else{
                userLogoAdaptor.startListening();
                Toast.makeText(getApplicationContext(), "Reload Logo", Toast.LENGTH_SHORT).show();
            }
        });

        gridLayout.setOnClickListener(view -> {
            try{
                userHouseAdaptor.stopListening();
            }catch (Exception e){
                userLogoAdaptor.stopListening();
            }
            defaultLayout = "grid";
            setAllRecycleView(type, "grid", "");
            if(type.equalsIgnoreCase("house")){
                userHouseAdaptor.startListening();
            }else{
                userLogoAdaptor.startListening();
            }
        });
    }

    private void setAllRecycleView(String type, String layout, String search) {
        Query queryHouse;
        Query queryLogo;
        if (type.equalsIgnoreCase("house")) {
            if(search.equalsIgnoreCase(""))
                queryHouse = FirebaseFirestore.getInstance().collection("House");
            else
                queryHouse = FirebaseFirestore.getInstance()
                        .collection("House")
                        .orderBy("name")
                        .startAt(search)
                        .endAt(search + "\uf8ff");

            FirestoreRecyclerOptions<House> optionsHouse = new FirestoreRecyclerOptions.Builder<House>()
                    .setQuery(queryHouse, House.class)
                    .build();

            userHouseAdaptor = new UserDataListAdaptor(optionsHouse, this);
            RecyclerView recyclerView = findViewById(R.id.recycler_image_colletion_user);
            recyclerView.setHasFixedSize(true);
            if(layout.equalsIgnoreCase("linier")){
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
            else{
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }
            recyclerView.setAdapter(userHouseAdaptor);

            userHouseAdaptor.setOnItemClickListener((documentSnapshot, position) -> {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getApplicationContext(), DetailItemActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "house");
                startActivity(intent);
            });

        } else {
            if(search.equalsIgnoreCase(""))
                queryLogo = FirebaseFirestore.getInstance().collection("Logo");
            else
                queryLogo = FirebaseFirestore.getInstance()
                        .collection("Logo")
                        .orderBy("name")
                        .startAt(search)
                        .endAt(search + "\uf8ff");

            FirestoreRecyclerOptions<Logo> optionsLogo = new FirestoreRecyclerOptions.Builder<Logo>()
                    .setQuery(queryLogo, Logo.class)
                    .build();
            userLogoAdaptor = new UserDataListLogoAdaptor(optionsLogo, this);
            RecyclerView recyclerView = findViewById(R.id.recycler_image_colletion_user);
            recyclerView.setHasFixedSize(true);
            if(layout.equalsIgnoreCase("linier")){
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
            else{
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }
            recyclerView.setAdapter(userLogoAdaptor);

            userLogoAdaptor.setOnItemClickListener((documentSnapshot, position) -> {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getApplicationContext(), DetailItemActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "logo");
                startActivity(intent);
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try{
            userHouseAdaptor.startListening();
        }catch (Exception e){
            userLogoAdaptor.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            userHouseAdaptor.stopListening();
        }catch (Exception e){
            userLogoAdaptor.stopListening();
        }
    }
}