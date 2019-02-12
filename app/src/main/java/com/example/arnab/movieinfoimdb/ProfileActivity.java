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
     Button log_out;
     Button search_button;
     Button favourite_button;
     Button history_button;
     Button todo_button;
    protected FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        welcomemessage = findViewById(R.id.Intromessage);
        log_out = (Button)findViewById(R.id.LogOutButton);
        search_button = (Button) findViewById(R.id.SearchButton);
        favourite_button = (Button) findViewById(R.id.FavouritesButton);
        history_button = (Button) findViewById(R.id.HistoryButton);
        todo_button = (Button) findViewById(R.id.ToWatchButton);
        welcomemessage.setVisibility(View.INVISIBLE);
        log_out.setVisibility(View.INVISIBLE);
        search_button.setVisibility(View.INVISIBLE);
        favourite_button.setVisibility(View.INVISIBLE);
        history_button.setVisibility(View.INVISIBLE);
        todo_button.setVisibility(View.INVISIBLE);
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
                    welcomemessage.setText("Welcome "+name);
                    welcomemessage.setVisibility(View.VISIBLE);
                    log_out.setVisibility(View.VISIBLE);
                    search_button.setVisibility(View.VISIBLE);
                    favourite_button.setVisibility(View.VISIBLE);
                    history_button.setVisibility(View.VISIBLE);
                    todo_button.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void searchMethod(View view){

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