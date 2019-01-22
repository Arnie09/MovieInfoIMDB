package com.example.arnab.movieinfoimdb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    protected TextView welcomemessage;
    protected Button log_out;
    protected Button search_button;
    protected FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        welcomemessage = findViewById(R.id.WelcomeTextView);
        log_out = findViewById(R.id.LogOutButton);
        search_button = findViewById(R.id.SearchButton);
        firebaseAuthenticator = FirebaseAuth.getInstance();
        if(firebaseAuthenticator.getCurrentUser()==null){
            finish();
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
        }
        String email = firebaseAuthenticator.getCurrentUser().getEmail();
        welcomemessage.setText("welcome "+email);


    }

    public void searchMethod(View view){
        finish();
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    public void logOutMethod(View view){

        firebaseAuthenticator.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(),LogInActivity.class));

    }
}