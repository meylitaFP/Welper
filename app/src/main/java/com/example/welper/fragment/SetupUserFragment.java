package com.example.welper.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.welper.R;
import com.example.welper.activity.AuthenticationActivity;
import com.example.welper.activity.MainActivity;
import com.example.welper.base.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SetupUserFragment extends Fragment {

    private TextView textViewSignIn;
    private Button buttonSignUp;
    private EditText editTextEmail, userName, dateBirth;
    private EditText editTextPassword, editTextPasswordValid;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("user");
    private DatePickerDialog datePickerDialog;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        textViewSignIn = v.findViewById(R.id.text_view_signIn);
        buttonSignUp = v.findViewById(R.id.button_signup);
        editTextEmail = v.findViewById(R.id.edit_text_signup_email);
        editTextPassword = v.findViewById(R.id.edit_text_signup_password);
        editTextPasswordValid = v.findViewById(R.id.edit_text_signup_password_validation);
        userName = v.findViewById(R.id.user_name);
        dateBirth = v.findViewById(R.id.date_of_birth);

        dateBirth.setOnClickListener(v1 -> {
            Calendar newCalendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        dateBirth.setText(dateFormatter.format(newDate.getTime()));
                    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        buttonSignUp.setOnClickListener(v1 -> {
            SignUp();
        });

        textViewSignIn.setOnClickListener(v1 -> {
            ((AuthenticationActivity)getActivity()).setViewPager(new LoginFragment());
        });

        return v;
    }

    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }

    //handling setup new user
    private void SignUp(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String passwordValid = editTextPasswordValid.getText().toString().trim();
        final String name = userName.getText().toString().trim();
        final String date = dateBirth.getText().toString().trim();

        if (name.isEmpty()) {
            userName.setError("need to set user name");
            userName.requestFocus();
            return;
        }

        if (date.isEmpty()){
            editTextEmail.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("need email");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("need a correct email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("need password");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("need password length at lest 6 char");
            return;
        }

        if(passwordValid.isEmpty()){
            editTextPasswordValid.setError("need password");
            editTextPasswordValid.requestFocus();
            return;
        }
        if(passwordValid.length() < 6){
            editTextPasswordValid.setError("need password length at lest 6 char");
            return;
        }

        if (!password.equalsIgnoreCase(passwordValid)){
            editTextPasswordValid.setError("password not correct");
            editTextPasswordValid.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(
                new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        user = mAuth.getCurrentUser();
                        saveToDatabase(name, email, date);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // handling update user into database
    private void saveToDatabase(String name, String email, String date){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "saveToDatabase: succes");
                });
        Profile profile = new Profile(user.getUid(),name, email, date, null, null, null, 1);
        collectionReference.document(user.getUid()).set(profile)
                .addOnSuccessListener(documentReference -> {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    Toast.makeText(getActivity(), "User has successfully signup",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "saveToDatabase: " + e);
                });
    }
}
