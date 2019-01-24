package com.example.arnab.movieinfoimdb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton;
    private EditText emailTextView;
    private EditText passwordTextView;
    private TextView signInTextView;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpButton = findViewById(R.id.SignUpButton);
        emailTextView = findViewById(R.id.TextViewEmail);
        passwordTextView = findViewById(R.id.TextViewPassword);
        signInTextView = findViewById(R.id.SigninLink);
        firebaseAuthenticator = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        if(firebaseAuthenticator.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == signUpButton){
            //SignUp Procedure!
            registerUser();
        }
        else if(view == signInTextView){
            //SignInProcedure!
            finish();
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
        }
    }

    private void registerUser() {

        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return;
        }

            progressDialog.setMessage("Registering.Wait up!");
            progressDialog.show();

            firebaseAuthenticator.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "User Registration Successful!", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = firebaseAuthenticator.getCurrentUser();
                                ((FirebaseUser) user).sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                finish();
                                                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                                            }
                                        });

                            }
                            else{
                                Toast.makeText(MainActivity.this, "Sorry Registration unsuccessful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


    }


}
