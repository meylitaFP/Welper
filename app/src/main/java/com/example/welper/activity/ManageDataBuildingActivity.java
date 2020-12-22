package com.example.welper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.welper.R;
import com.example.welper.adaptor.ImagePagerAdaptorBuilding;
import com.example.welper.base.House;
import com.example.welper.base.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManageDataBuildingActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 111;
    private static ArrayList<String> listUriImg = new ArrayList<>();
    private static final String TAG = "Check";
    Uri uriPicture;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Bitmap bitmap;

    CollectionReference dbHouse = FirebaseFirestore.getInstance().collection("House");
    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("Image");

    final static int defaultColorSelector = 0xFF03DAC5;
    ImageView buildingPicture, lvlMin, lvlPlus, roomMin, roomPlus;
    EditText buildingName, levelValue, roomValue, buildingSize;
    TextView opMinimalism, opClassic, opModern;

    ViewPager viewPager;
    ImagePagerAdaptorBuilding imgPgAdBuilding;
    Button pictureAdd, buildingSubmit;
    static String valueType = "Minimalism";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        buildingPicture = findViewById(R.id.building_picture);
        lvlMin = findViewById(R.id.building_level_min);
        lvlPlus = findViewById(R.id.building_level_plus);
        roomMin = findViewById(R.id.building_room_min);
        roomPlus = findViewById(R.id.building_room_plus);
        buildingName = findViewById(R.id.building_name);
        levelValue = findViewById(R.id.building_level_value);
        roomValue = findViewById(R.id.building_room_value);
        opMinimalism = findViewById(R.id.model_minilism);
        opClassic = findViewById(R.id.model_classic);
        opModern = findViewById(R.id.model_modern);
        pictureAdd = findViewById(R.id.building_picture_add);
        buildingSubmit = findViewById(R.id.building_submit);
        buildingSize = findViewById(R.id.building_size);

        viewPager = findViewById(R.id.image_viewPager);

        if(listUriImg.size() > 1){
            buildingPicture.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }


        loadItem();

        lvlPlus.setOnClickListener(view -> {
            int levelValueNum = Integer.parseInt(levelValue.getText().toString());
            if (levelValueNum < 6) {
                levelValueNum++;
            }
            levelValue.setText(String.valueOf(levelValueNum));
        });

        lvlMin.setOnClickListener(view -> {
            int levelValueNum = Integer.parseInt(levelValue.getText().toString());
            if (levelValueNum > 1) {
                levelValueNum--;
            }
            levelValue.setText(String.valueOf(levelValueNum));
        });

        roomPlus.setOnClickListener(view -> {
            int roomValueNum = Integer.parseInt(roomValue.getText().toString());
            roomValueNum++;
            roomValue.setText(String.valueOf(roomValueNum));
        });

        roomMin.setOnClickListener(view -> {
            int roomValueNum = Integer.parseInt(roomValue.getText().toString());
            if (roomValueNum > 1) {
                roomValueNum--;
            }
            roomValue.setText(String.valueOf(roomValueNum));
        });

        opClassic.setOnClickListener(view -> {
            setModel("Classic");
        });

        opModern.setOnClickListener(view -> {
            setModel("Modern");
        });

        opMinimalism.setOnClickListener(view -> {
            setModel("Minimalism");
        });

        pictureAdd.setOnClickListener(view -> {
            String buildName = buildingName.getText().toString();
            if (buildName.isEmpty()) {
                buildingName.setError("need to set Building name");
                buildingName.requestFocus();
                return;
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, CHOOSE_IMAGE);
            }
        });

        // add data house for management add --C--
        buildingSubmit.setOnClickListener(view -> {
            String houseName = buildingName.getText().toString();
            int levelValueNum = Integer.parseInt(levelValue.getText().toString());
            int roomValueNum = Integer.parseInt(roomValue.getText().toString());
            int buildingSizeNum = Integer.parseInt(buildingSize.getText().toString());
            String ts = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            if (houseName.isEmpty() || houseName.equalsIgnoreCase("")) {
                buildingName.setError("need to set house name");
                buildingName.requestFocus();
                return;
            }

            final String timeStamp = Long.toString(new Date().getTime());
            Image image = new Image(null, houseName, listUriImg, timeStamp, "House");
            dbImage.document(timeStamp).set(image)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Added to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "saveToDatabase: " + e);
                    });

            House house = new House(houseName, valueType, buildingSizeNum,
                    levelValueNum, roomValueNum, timeStamp, ts);

            Intent intent = getIntent();
            if (intent.getStringExtra("id") != null &&
                    !intent.getStringExtra("id").equalsIgnoreCase("")) {
                house.setHouseId(intent.getStringExtra("id"));
                addToDatabase(house,image, intent.getStringExtra("id"));
            } else {
                addToDatabase(house,image, "");
            }
        });
    }

    //setup model
    private void setModel(String model) {
        Drawable defaultColor = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.border_thin);
        if (model.equalsIgnoreCase("Classic")) {
            opClassic.setBackgroundColor(defaultColorSelector);
            opModern.setBackground(defaultColor);
            opMinimalism.setBackground(defaultColor);
        }
        if (model.equalsIgnoreCase("Minimalism")) {
            opMinimalism.setBackgroundColor(defaultColorSelector);
            opModern.setBackground(defaultColor);
            opClassic.setBackground(defaultColor);
        }
        if (model.equalsIgnoreCase("Modern")) {
            opModern.setBackgroundColor(defaultColorSelector);
            opClassic.setBackground(defaultColor);
            opMinimalism.setBackground(defaultColor);
        }
        valueType = model;
    }

    // add data House to database --C--
    private void addToDatabase(House houses, Image image, String id) {
        if (id.equalsIgnoreCase("")) {
            dbHouse.document().set(houses)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        this.finish();
                        Toast.makeText(getApplicationContext(), "Added to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "saveToDatabase: " + e);
                    });

        } else {
            dbHouse.document(id).set(houses)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        this.finish();
                        Toast.makeText(getApplicationContext(), "Updated to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "saveToDatabase: " + e);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listUriImg.clear();
        buildingPicture.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
    }

    // load data House for database --C--
    private void loadItem() {
        Intent intent = getIntent();
        if (intent.getStringExtra("id") != null &&
                !intent.getStringExtra("id").equalsIgnoreCase("")) {
            String id = intent.getStringExtra("id");
            dbHouse.document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        House houses = documentSnapshot.toObject(House.class);
                        setupHouse(houses);
                        Toast.makeText(getApplicationContext(),
                                "successfully Load house information",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "onCreate: " + e);
                    });
        }
    }

    // setup house
    private void setupHouse(House house) {
        levelValue.setText(String.valueOf(house.getLevel()));
        roomValue.setText(String.valueOf(house.getNumOfRoom()));
        setModel(house.getModel());
        buildingSize.setText(String.valueOf(house.getSize()));
        buildingName.setText(house.getName());
        refreshListImage(house);
    }

    // upload new image input into database firestorage --C--
    private void uploadImageToFirebaseStorage(byte[] img) {
        String buildingNameDir = buildingName.getText().toString();
        final String timeStamp = Long.toString(new Date().getTime());
        final StorageReference riversRef = mStorageRef.child("House/" +
                buildingNameDir + "/" + timeStamp + ".jpg");

        riversRef.putBytes(img)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        listUriImg.add(uri.toString());
                        imgPgAdBuilding = new ImagePagerAdaptorBuilding(this, listUriImg);
                        buildingPicture.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);
                        viewPager.setAdapter(imgPgAdBuilding);
                        Log.d(TAG, "uploadImageToFirebaseStorage uri: " + uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    //change picture name on House into ID of the database Image
    public void refreshListImage(House house){
        dbImage.document(house.getPicName()).get()
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
        buildingPicture.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(imgPgAdBuilding);
    }

    // for handling activity take picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriPicture = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext()
                        .getContentResolver(), uriPicture);
                uploadImageToFirebaseStorage(imageResize(bitmap));
                deleteCache(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] imageResize(Bitmap bitmap) {
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();
        resize.recycle();
        return data;
    }

    // clearing cache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            Log.d(TAG, "deleteCache: " + e);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}