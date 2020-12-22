package com.example.welper.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.welper.R;
import com.example.welper.base.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 111;
    private static final String TAG = "halo";
    Uri uriProfil;
    ImageView pictureProfile;
    EditText countryProfile, genderProfile, dateProfile;
    EditText emailProfile, fullNameProfile, nameProfile;
    Button buttonUpdateName, changePassword;
    private DatePickerDialog datePickerDialog;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("user");
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        pictureProfile = findViewById(R.id.new_profile_picture);
        nameProfile = findViewById(R.id.new_profile_name);
        dateProfile = findViewById(R.id.new_date_of_birth);
        countryProfile = findViewById(R.id.country);
        genderProfile = findViewById(R.id.gender);
        emailProfile = findViewById(R.id.email);
        fullNameProfile = findViewById(R.id.full_name);
        buttonUpdateName = findViewById(R.id.profile_update_name);
        changePassword = findViewById(R.id.change_password);

        loadUserInfoAuth();

        pictureProfile.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, CHOOSE_IMAGE);
        });

        dateProfile.setOnClickListener(v -> {
            Calendar newCalendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                dateProfile.setText(dateFormatter.format(newDate.getTime()));
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        changePassword.setOnClickListener(view -> {
            changePassword();
        });

        buttonUpdateName.setOnClickListener(v -> {
            String name = nameProfile.getText().toString().trim();
            String email = emailProfile.getText().toString();
            String date = dateProfile.getText().toString();
            String gender = genderProfile.getText().toString();
            String fullName = fullNameProfile.getText().toString();
            String country = countryProfile.getText().toString();
            if (name.isEmpty()) {
                nameProfile.setError("need to set profile name");
                nameProfile.requestFocus();
                return;
            }

            if (user != null) {
                UserProfileChangeRequest profilUser = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(uriProfil)
                        .build();
                user.updateProfile(profilUser).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "profile has updated",
                            Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
            if (date.isEmpty()) {
                dateProfile.setError("need to set profile date");
                dateProfile.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                emailProfile.setError("email need to be set");
                emailProfile.requestFocus();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("profileName", name);
            data.put("email", email);
            data.put("date", date);
            data.put("gender", gender);
            data.put("country", country);
            data.put("fullName", fullName);
            collectionReference.document(user.getUid()).update(data)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Success updateModel",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "updateNamePicture: " + e);
                    });
        });
    }

    // handling change password using alertdialog
    private void changePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText currentEmail = dialogView.findViewById(R.id.password_email);
        EditText currentPassword = dialogView.findViewById(R.id.password_current);
        EditText newPassword = dialogView.findViewById(R.id.password);
        EditText newConfirmPassword = dialogView.findViewById(R.id.confirm_password);
        Button buttonPassword = dialogView.findViewById(R.id.password_button);

        builder.setTitle("Changing Password");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        currentEmail.setText(user.getEmail());

        buttonPassword.setOnClickListener(v -> {
            final String password = newPassword.getText().toString();
            final String confirmPassword = newConfirmPassword.getText().toString();
            final String cEmail = currentEmail.getText().toString();
            final String cPassword = currentPassword.getText().toString();
            if (!password.equalsIgnoreCase(confirmPassword)) {
                newConfirmPassword.setError("password not correct");
                newConfirmPassword.requestFocus();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(cEmail, cPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User re-authenticated.");
                        user.updatePassword(password)
                                .addOnSuccessListener(aVoid1 -> {
                                    Log.d(TAG, "User password updated.");
                                    Toast.makeText(getApplicationContext(), "Success set password",
                                            Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "changingPassword: " + e);
                                });
                    }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                currentPassword.setError("password not correct");
                currentPassword.requestFocus();
                Log.d(TAG, "changingPassword: "+ e);
            });
        });
    }

    //for load all user information for database
    private void loadUserInfoAuth() {
        if (user != null) {
            mStorageRef.child("profile/picture/" + user.getUid() + ".jpg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri.toString()).into(pictureProfile);
                            Log.d(TAG, "Success to load profile picture: " + uri.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "Failed to load profile picture");
                        }
                    });
            if (user.getDisplayName() != null) {
                collectionReference.document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            nameProfile.setText(profile.getProfileName());
                            dateProfile.setText(profile.getDate());
                            countryProfile.setText(profile.getCountry());
                            genderProfile.setText(profile.getGender());
                            emailProfile.setText(profile.getEmail());
                            fullNameProfile.setText(profile.getFullName());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "load info failed",
                                    Toast.LENGTH_LONG).show();
                        });
                Log.d(TAG, "loadUserInfo: " + user.getDisplayName());
            } else {
                Log.d(TAG, "loadUserInfoAuth: User name not found");
            }
        } else {
            Log.d(TAG, "loadUserInfoAuth: User Session not found");
        }
    }

    // upload new image input into database fire storage
    private void uploadImageToFirebaseStorage() {
        StorageReference riversRef = mStorageRef.child("profile/picture/" + user.getUid() + ".jpg");

        if (uriProfil != null) {
            riversRef.putFile(uriProfil).addOnSuccessListener(taskSnapshot -> {
                mStorageRef.child("profile/picture/" + user.getUid() + ".jpg").getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            uriProfil = uri;
                        }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
                Toast.makeText(getApplicationContext(), "succes input to firestore",
                        Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }

    // for handling activity take picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfil = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriProfil);
                pictureProfile.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
                deleteCache(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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