package com.example.welper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.welper.R;
import com.example.welper.fragment.ChatListFragment;
import com.example.welper.fragment.HomeAdminFragment;
import com.example.welper.fragment.OverviewAdminFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageView home, chat, overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Admin Page");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        home = findViewById(R.id.img_home_admin);
        chat = findViewById(R.id.img_chat_admin);
        overview = findViewById(R.id.img_find_admin);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeAdminFragment()).commit();

        home.setOnClickListener(view -> { setViewPager(new HomeAdminFragment()); });
        chat.setOnClickListener(view -> { setViewPager(new ChatListFragment()); });
        overview.setOnClickListener(view -> { setViewPager(new OverviewAdminFragment()); });
    }
    public void setViewPager(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

    public void intentFragment(Class activity){
        startActivity(new Intent(getApplicationContext(), activity));
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, AuthenticationActivity.class));
                break;
            case R.id.menuProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return true;
    }
}