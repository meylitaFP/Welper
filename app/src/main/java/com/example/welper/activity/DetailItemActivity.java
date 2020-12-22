package com.example.welper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.printservice.CustomPrinterIconCallback;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.welper.R;
import com.example.welper.adaptor.ImagePagerAdaptorBuilding;
import com.example.welper.base.House;
import com.example.welper.base.Image;
import com.example.welper.base.Logo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailItemActivity extends AppCompatActivity {

    private static final String TAG = "Testing";
    private static ArrayList<String> listUriImg = new ArrayList<>();
    CollectionReference dbHouse = FirebaseFirestore.getInstance().collection("House");
    CollectionReference dbLogo = FirebaseFirestore.getInstance().collection("Logo");
    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("Image");

    TextView title, model, size, level, room, color, type;
    LinearLayout vSize, vLevel, vRoom, vColor, vType;
    ImageView imgProduct;
    Button btnContact;
    static String id = "";
    private StorageReference mStorageRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ViewPager viewPager;
    ImagePagerAdaptorBuilding imgPgAdBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        title = findViewById(R.id.product_title);
        model = findViewById(R.id.product_model);
        size = findViewById(R.id.product_size);
        level = findViewById(R.id.product_level);
        room = findViewById(R.id.product_room);
        color = findViewById(R.id.product_color);
        type = findViewById(R.id.product_type);

        vSize = findViewById(R.id.gone_size);
        vLevel = findViewById(R.id.gone_level);
        vRoom = findViewById(R.id.gone_room);
        vColor = findViewById(R.id.gone_color);
        vType = findViewById(R.id.gone_type);
        imgProduct = findViewById(R.id.building_picture_detail);
        btnContact = findViewById(R.id.item_detail_contact_btn);

        viewPager = findViewById(R.id.image_viewPager);

        if(listUriImg.size() > 1){
            imgProduct.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();

        if (intent.getStringExtra("condition") != null &&
                !intent.getStringExtra("condition").equalsIgnoreCase("")) {
            btnContact.setVisibility(View.INVISIBLE);
        }

        laodData();


        // contact button to send Item information direct into the Chatting Room
        btnContact.setOnClickListener(view -> {
            if (intent.getStringExtra("id") != null &&
                    !intent.getStringExtra("id").equalsIgnoreCase("")) {

                id = intent.getStringExtra("id");
                Intent intentSend = new Intent(this, AdminChatActivity.class);

                if(intent.getStringExtra("type").equalsIgnoreCase("house"))
                    intentSend.putExtra("type", "house");
                else
                    intentSend.putExtra("type", "logo");

                intentSend.putExtra("contact", id);
                intentSend.putExtra("id", user.getUid());
                this.startActivity(intentSend);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listUriImg.clear();
        imgProduct.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
    }

    // load Data for user --C-- (edited)
    private void laodData() {
        Intent intent = getIntent();
        if (intent.getStringExtra("id") != null &&
                !intent.getStringExtra("id").equalsIgnoreCase("")) {
            id = intent.getStringExtra("id");
            if (intent.getStringExtra("type").equalsIgnoreCase("house")) {
                vColor.setVisibility(View.GONE);
                vType.setVisibility(View.GONE);
                dbHouse.document(id).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            House house = documentSnapshot.toObject(House.class);
                            title.setText(house.getName());
                            model.setText(house.getModel());
                            size.setText(String.valueOf(house.getSize()));
                            level.setText(String.valueOf(house.getLevel()));
                            room.setText(String.valueOf(house.getNumOfRoom()));
                            refreshListImageHouse(house);
                            Toast.makeText(getApplicationContext(), "Success load House", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "onCreate: " + e);
                        });
            } else {
                vSize.setVisibility(View.GONE);
                vLevel.setVisibility(View.GONE);
                vRoom.setVisibility(View.GONE);
                dbLogo.document(id).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Logo logo = documentSnapshot.toObject(Logo.class);
                            title.setText(logo.getName());
                            model.setText(logo.getModel());
                            color.setText(logo.getColor());
                            type.setText(logo.getType());

                            refreshListImageLogo(logo);
                            Toast.makeText(getApplicationContext(), "Success load Logo", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "onCreate: " + e);
                        });
            }

        }
    }

    //change picture name on House into ID of the database Image
    public void refreshListImageHouse(House house){
        dbImage.document(house.getPicName()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Image image = documentSnapshot.toObject(Image.class);
                    setupImage(image);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "refreshListImage: " + e);
                });
    }

    public void refreshListImageLogo(Logo logo){
        dbImage.document(logo.getPicName()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Image image = documentSnapshot.toObject(Image.class);
                    setupImage(image);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "refreshListImage: " + e);
                });
    }

    public void setupImage(Image image){
        listUriImg.addAll(image.getLocation());
        imgPgAdBuilding = new ImagePagerAdaptorBuilding(this, listUriImg);
        imgProduct.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(imgPgAdBuilding);
    }
}