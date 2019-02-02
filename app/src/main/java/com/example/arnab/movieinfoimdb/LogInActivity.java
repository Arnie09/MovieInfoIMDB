package com.example.arnab.movieinfoimdb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    private Button SignInButton;
    private EditText emailTextView;
    private EditText passwordTextView;
    private TextView signUpTextView;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SignInButton = findViewById(R.id.SignInButton);
        emailTextView = findViewById(R.id.TextViewEmail);
        passwordTextView = findViewById(R.id.TextViewPassword);
        signUpTextView = findViewById(R.id.SignUpLink);
        firebaseAuthenticator = FirebaseAuth.getInstance();

        emailTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailTextView.setBackgroundColor(Color.parseColor("#A4A4A4"));
                passwordTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                return false;
            }
        });

        passwordTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                passwordTextView.setBackgroundColor(Color.parseColor("#A4A4A4"));
                return false;
            }
        });

        SignInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        if(firebaseAuthenticator.getCurrentUser()!=null && firebaseAuthenticator.getCurrentUser().isEmailVerified()== true){
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == SignInButton){
            //SignIn Procedure!
            signInUser();
        }
        else if(view == signUpTextView){
            //SignInProcedure!
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

    private void signInUser() {

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

        progressDialog.setMessage("Logging in.Wait up!");
        progressDialog.show();

        firebaseAuthenticator.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //Toast.makeText(LogInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                            firebaseAuthenticator.getCurrentUser().reload();
                            if(firebaseAuthenticator.getCurrentUser().isEmailVerified()) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            }
                            else{
                                Toast.makeText(LogInActivity.this, "Please check email and verify sign up!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(LogInActivity.this, "Login unsuccessful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
