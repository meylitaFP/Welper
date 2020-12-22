package com.example.welper.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.welper.R;
import com.example.welper.activity.AdminActivity;
import com.example.welper.activity.AuthenticationActivity;
import com.example.welper.activity.MainActivity;
import com.example.welper.base.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private TextView textViewSignUp, forgetPass;
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("user");

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        textViewSignUp = v.findViewById(R.id.text_view_signUp);
        buttonSignIn = v.findViewById(R.id.button_signin);
        editTextEmail = v.findViewById(R.id.edit_text_login_email);
        editTextPassword = v.findViewById(R.id.edit_text_login_password);
        forgetPass = v.findViewById(R.id.forget_password);

        buttonSignIn.setOnClickListener(v1 -> {
            login();
        });

        textViewSignUp.setOnClickListener(v1 -> {
            ((AuthenticationActivity)getActivity()).setViewPager(new SetupUserFragment());
        });

        forgetPass.setOnClickListener(view -> {
            passwordRecovery();
        });

        return v;
    }

    //code for handle login feature
    private void login(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
        }

//
//        if(password == "root" && email =="root@root.com"){
//            ((AuthenticationActivity)getActivity()).intentFragment(MainActivity.class);
//        }
//        else{
//            ((AuthenticationActivity)getActivity()).intentFragment(AdminActivity.class);
//        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    collectionReference.document(mAuth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Profile profile = documentSnapshot.toObject(Profile.class);
                                if(profile.getLevel() == 0){
                                    ((AuthenticationActivity)getActivity())
                                            .intentFragment(AdminActivity.class);
                                }else{
                                    ((AuthenticationActivity)getActivity())
                                            .intentFragment(MainActivity.class);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "load info failed",
                                        Toast.LENGTH_LONG).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // code for handle user access
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            collectionReference.document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        if(profile.getLevel() == 0){
                            ((AuthenticationActivity)getActivity())
                                    .intentFragment(AdminActivity.class);
                        }else{
                            ((AuthenticationActivity)getActivity())
                                    .intentFragment(MainActivity.class);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "load info failed",
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    //code for forget Password
    private void passwordRecovery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_forget_password, null);
        builder.setView(dialogView);

        final EditText emailReset = dialogView.findViewById(R.id.email_forget);
        final Button buttonEmail = dialogView.findViewById(R.id.email_reset);

        builder.setTitle("Reset Password");
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        buttonEmail.setOnClickListener(v -> {
            final String email = emailReset.getText().toString();
            if (email.isEmpty()){
                emailReset.setError("need email address");
                emailReset.requestFocus();
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "reset password has been seen to email",
                        Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }
}
