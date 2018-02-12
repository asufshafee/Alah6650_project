package com.example.amal.linkbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {


    private EditText LoginEmail;
    private EditText LoginPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //creating references to xml file id's
        //initialise view objects through id
        LoginEmail = (EditText) findViewById(R.id.userEmail);
        LoginPassword = (EditText) findViewById(R.id.userPassword);

        progressDialog = new ProgressDialog(this);
        //getting instance of firebase auth for current user
        firebaseAuth = FirebaseAuth.getInstance();

        //to check if the user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //directly jump to main Activity page
            if (firebaseAuth.getCurrentUser().isEmailVerified())
            {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();

            }


        }

    }

    //regisetr click
    public void regis(View view) {

        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }

    //login click
    public void login(View view) {

        userLogin();
    }
    //forget password click
    public void Forget(View view) {

        startActivity(new Intent(Login.this, Forget_Password.class));


    }

    private void userLogin() {
        //get email and password form the user
        String email = LoginEmail.getText().toString().trim();
        String password = LoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email field is empty
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password field is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6) {
            //password field is empty
            Toast.makeText(this, "password size is less the 6 ", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Login In...");
        progressDialog.show();

        //takes two string arguments- parameter, first the activity context and the second listener
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            //this method will be called when the login is complete

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    //checking the userEmail is varified or not
                    if (firebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        finish();
                        startActivity(new Intent(Login.this, MainActivity.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "Verify your email to login", Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Wrong Email Or Password", Toast.LENGTH_LONG).show();
                    Log.d(" Error", "No access", task.getException());

                }

            }

        });


    }


}
