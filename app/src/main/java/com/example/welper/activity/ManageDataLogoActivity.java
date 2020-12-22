package com.example.welper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.welper.base.Logo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManageDataLogoActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 111;
    private static ArrayList<String> listUriImg = new ArrayList<>();
    private static final String TAG = "halo";
    Uri uriPicture;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Bitmap bitmap;

    CollectionReference dbLogo = FirebaseFirestore.getInstance().collection("Logo");
    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("Image");

    final static int defaultColorSelector = 0xFF03DAC5;
    static String vColor = "Monochrome";
    static String vModel = "Circle";
    static String vType = "Text";

    ViewPager viewPager;
    ImagePagerAdaptorBuilding imgPgAdBuilding;

    ImageView logoPic;
    Button logoAdd, logoSubmit;
    EditText LogoName;
    TextView colMono, colFull, colNeon, modCir, modSqr, typeText, typeImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data_logo);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        logoPic = findViewById(R.id.logo_picture);
        LogoName = findViewById(R.id.logo_name);

        colMono = findViewById(R.id.color_monochrome);
        colNeon = findViewById(R.id.color_neon);
        colFull = findViewById(R.id.color_full_color);
        modCir = findViewById(R.id.model_circle);
        modSqr = findViewById(R.id.model_square);
        typeImg = findViewById(R.id.type_image);
        typeText = findViewById(R.id.type_text);

        logoAdd = findViewById(R.id.logo_picture_add);
        logoSubmit = findViewById(R.id.logo_submit);

        viewPager = findViewById(R.id.image_viewPager);

        if(listUriImg.size() > 1){
            logoPic.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }


        loadItem();

        colMono.setOnClickListener(view -> {
            setColor("Monochrome");
        });

        colNeon.setOnClickListener(view -> {
            setColor("Neon");
        });

        colFull.setOnClickListener(view -> {
            setColor("Full Color");
        });

        modCir.setOnClickListener(view -> {
            setModel("Circle");;
        });

        modSqr.setOnClickListener(view -> {
            setModel("Square");
        });

        typeImg.setOnClickListener(view -> {
            setType("Image");
        });

        typeText.setOnClickListener(view -> {
            setType("Text");
        });

        logoAdd.setOnClickListener(view -> {
            String LogoNameValue = LogoName.getText().toString();
            if (LogoNameValue.isEmpty()) {
                LogoName.setError("need to set Building name");
                LogoName.requestFocus();
                return;
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, CHOOSE_IMAGE);
            }
        });

        // add data Logo for management add --C--
        logoSubmit.setOnClickListener(view -> {
            String logoNameValue = LogoName.getText().toString();
            String ts = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            if (logoNameValue.isEmpty() || logoNameValue.equalsIgnoreCase("")) {
                LogoName.setError("need to set Logo name");
                LogoName.requestFocus();
                return;
            }

            final String timeStamp = Long.toString(new Date().getTime());
            Image image = new Image(null, logoNameValue, listUriImg, timeStamp, "Logo");
            dbImage.document(timeStamp).set(image)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Added to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "saveToDatabase: " + e);
                    });

            Logo logo = new Logo(logoNameValue, vColor, vModel, vType,
                    timeStamp, ts);

            Intent intent = getIntent();
            // this condition for update purpose
            if (intent.getStringExtra("id") != null &&
                    !intent.getStringExtra("id").equalsIgnoreCase("")) {
                logo.setLogoId(intent.getStringExtra("id"));
                addToDatabase(logo, intent.getStringExtra("id"));
            }
            else{
                addToDatabase(logo, "");
            }
        });
    }

    //setup color
    private void setColor(String color) {
        Drawable defaultColor = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.border_thin);
        if (color.equalsIgnoreCase("Monochrome")) {
            colMono.setBackgroundColor(defaultColorSelector);
            colNeon.setBackground(defaultColor);
            colFull.setBackground(defaultColor);
        }
        if (color.equalsIgnoreCase("Full Color")) {
            colFull.setBackgroundColor(defaultColorSelector);
            colMono.setBackground(defaultColor);
            colNeon.setBackground(defaultColor);
        }
        if (color.equalsIgnoreCase("Neon")) {
            colNeon.setBackgroundColor(defaultColorSelector);
            colMono.setBackground(defaultColor);
            colFull.setBackground(defaultColor);
        }
        vColor = color;
    }

    // setup model
    private void setModel(String model) {
        Drawable defaultColor = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.border_thin);
        if (model.equalsIgnoreCase("Circle")) {
            modCir.setBackgroundColor(defaultColorSelector);
            modSqr.setBackground(defaultColor);
        }
        if (model.equalsIgnoreCase("Square")) {
            modSqr.setBackgroundColor(defaultColorSelector);
            modCir.setBackground(defaultColor);
        }
        vModel = model;
    }

    //setup type
    private void setType(String type) {
        Drawable defaultColor = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.border_thin);
        if (type.equalsIgnoreCase("Image")) {
            typeImg.setBackgroundColor(defaultColorSelector);
            typeText.setBackground(defaultColor);
        }
        if (type.equalsIgnoreCase("text")) {
            typeText.setBackgroundColor(defaultColorSelector);
            typeImg.setBackground(defaultColor);
        }
        vType = type;
    }

    // add data Logo to database --C--
    private void addToDatabase(Logo logo, String id) {
        if(id.equalsIgnoreCase("")){
            dbLogo.document().set(logo)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        this.finish();
                        Toast.makeText(getApplicationContext(), "Added to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "addToDatabase: " + e);
                    });
        } else{
            dbLogo.document(id).set(logo)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        this.finish();
                        Toast.makeText(getApplicationContext(), "Updated to Database",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "addToDatabase: " + e);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listUriImg.clear();
        logoPic.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
    }

    // load data Logo from Database --C--
    private void loadItem() {
        Intent intent = getIntent();
        if (intent.getStringExtra("id") != null &&
                !intent.getStringExtra("id").equalsIgnoreCase("")) {
            String id = intent.getStringExtra("id");
            dbLogo.document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Logo logo = documentSnapshot.toObject(Logo.class);
                        setupLogo(logo);
                        Toast.makeText(getApplicationContext(),
                                "successfully Load logo information",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "onCreate: " + e);
                    });

        }
    }

    //setup logo
    private void setupLogo(Logo logo) {
        setColor(logo.getColor());
        setModel(logo.getModel());
        setType(logo.getType());
        LogoName.setText(logo.getName());
        refreshListImage(logo);
    }

    // upload new image input into database fire storage
    private void uploadImageToFirebaseStorage(byte[] img) {
        String logoNameDir = LogoName.getText().toString();
        final String timeStamp = Long.toString(new Date().getTime());
        final StorageReference riversRef = mStorageRef.child("Logo/" +
                logoNameDir + "/" + timeStamp + ".jpg");

        riversRef.putBytes(img)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        listUriImg.add(uri.toString());
                        imgPgAdBuilding = new ImagePagerAdaptorBuilding(this, listUriImg);
                        logoPic.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);
                        viewPager.setAdapter(imgPgAdBuilding);
                        Log.d(TAG, "uploadImageToFirebaseStorage uri: " + uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



    //change picture name on Logo into ID of the database Image
    public void refreshListImage(Logo logo){
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
        logoPic.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(imgPgAdBuilding);
    }

    // for handling activity take picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
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