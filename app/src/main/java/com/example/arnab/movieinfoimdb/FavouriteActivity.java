package com.example.arnab.movieinfoimdb;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouriteActivity extends AppCompatActivity {

    FirebaseAuth firebaseauthenticator;
    FirebaseUser user;
    FirebaseFirestore db;
    ListView list;
    String user_id = "";
    String name = "";
    CollectionReference collectionReference;
    View currentselectedview;
    ArrayList<String> MovieNames = new ArrayList<String>() ;
    //ArrayList<String> MovieInfo = new ArrayList<String>();
    Map<String,Object> MovieInfo = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        firebaseauthenticator = FirebaseAuth.getInstance();
        user = firebaseauthenticator.getCurrentUser();
        user_id = user.getUid();
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("UserFavourites").document(user_id).collection("Favourites");

        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MovieNames.add(document.getId());
                                MovieInfo.put(document.getId(),document.getData());
                                Log.i("Chandrika", document.getId() + " => " + document.getData());
                            }
                            ListView movie_history = findViewById(R.id.MovieListView);
                            movie_history.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FavouriteActivity.this,android.R.layout.simple_list_item_1,MovieNames);
                            movie_history.setAdapter(arrayAdapter);
                            movie_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selected = MovieInfo.get(MovieNames.get(position)).toString();
                                    selected = selected.substring(1,selected.length()-1);
                                    new AlertDialog.Builder(FavouriteActivity.this)
                                            .setIcon(android.R.drawable.sym_def_app_icon)
                                            .setTitle("Information")
                                            .setMessage(selected)
                                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    closeContextMenu();
                                                }
                                            })
                                            .show();
                                    //Toast.makeText(HistoryActivity.this, "position"+selected, Toast.LENGTH_SHORT).show();
                                }
                            });
                            movie_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    name = MovieNames.get(position);
                                    currentselectedview = view;
                                    highlightCurrentRow(currentselectedview);

                                    return false;
                                }
                            });
                        } else {
                            //Log.i("Chandrika", "Error getting documents: ", task.getException());
                            Toast.makeText(FavouriteActivity.this, "Nothing To show!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });





    }
    private void highlightCurrentRow(View rowView) {
        rowView.setBackgroundColor(Color.GRAY);
    }
    public void removeMethod(View view){

            if(name == ""){
                Toast.makeText(FavouriteActivity.this, "Long press any movie to delete it from favourites", Toast.LENGTH_SHORT).show();
                return;
            }


            db.collection("UserFavourites").document(user_id).collection("Favourites").document(name)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(FavouriteActivity.this, "deleted!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FavouriteActivity.this, "Unsuccessful!", Toast.LENGTH_SHORT).show();
                        }
                    });


        Intent intent = getIntent();
        finish();
        startActivity(intent);
        /*if (deleted == MovieNames.size()){
            Toast.makeText(HistoryActivity.this, "History deleted!", Toast.LENGTH_SHORT).show();
        }*/
    }

}
