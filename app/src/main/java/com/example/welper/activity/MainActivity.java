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
import com.example.welper.fragment.HomeUserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageView home, chat;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("User Page");


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        home = findViewById(R.id.img_home_user);
        chat = findViewById(R.id.img_chat_user);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeUserFragment()).commit();

        home.setOnClickListener(view -> { setViewPager(new HomeUserFragment()); });
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminChatActivity.class);
            intent.putExtra("userId", user.getUid());
            this.startActivity(intent);
        });
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