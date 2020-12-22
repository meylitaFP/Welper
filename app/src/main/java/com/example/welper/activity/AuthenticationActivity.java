package com.example.welper.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.welper.R;
import com.example.welper.fragment.LoginFragment;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    public void setViewPager(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

    public void intentFragment(Class activity){
        startActivity(new Intent(getApplicationContext(), activity));
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}