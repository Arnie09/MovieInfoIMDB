package com.example.arnab.movieinfoimdb;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        final FirebaseUser user = firebaseAuthenticator.getCurrentUser();
        String user_uid =  user.getUid();

        FirebaseFirestore DB = FirebaseFirestore.getInstance();
        DocumentReference mDB = DB.collection("UserDatabase").document(user_uid);

        if(firebaseAuthenticator.getCurrentUser()==null){
            finish();
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
        }

        mDB.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("Name");
                    welcomemessage.setText("welcome "+name);
                }
            }
        });
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
    public void historyMethod(View view){
        //finish();
        startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
    }
    public void toWatchMethod(View view){
        startActivity(new Intent(getApplicationContext(),ToWatchActivity.class));
    }
    public void favouritesMethod(View view){
        startActivity(new Intent(getApplicationContext(),FavouriteActivity.class));
    }
}