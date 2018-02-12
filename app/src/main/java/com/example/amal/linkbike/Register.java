package com.example.amal.linkbike;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.amal.linkbike.Objects.MyAppUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class Register extends AppCompatActivity {


    private EditText et_user;
    private EditText et_email;
    private EditText et_password;
    private EditText et_cpassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;

    RadioButton AgreeCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_user = (EditText) findViewById(R.id.userName);
        et_email = (EditText) findViewById(R.id.userEmail);
        et_password = (EditText) findViewById(R.id.userPassword);
        et_cpassword = (EditText) findViewById(R.id.userCPassword);


        AgreeCondition = (RadioButton) findViewById(R.id.AgreeCondition);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //if  user is already logged in directly jump to welcome page
            finish();
            startActivity(new Intent(Register.this, MainActivity.class));
        }
    }

    public void register(View view) {
        registerUser();
    }

    private void registerUser() {
        //get email as input from the user and covert it into object
        final String user = et_user.getText().toString().trim();
        final String email = et_email.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        String cpassword = et_cpassword.getText().toString().trim();


        if (TextUtils.isEmpty(user)) {
            //name field is empty
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if (!cpassword.equals(password)) {
            //match
            Toast.makeText(this, "password not Match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (AgreeCondition.isChecked() == false) {
            //match
            Toast.makeText(this, "Conform Teams & Condition", Toast.LENGTH_SHORT).show();
            return;
        }



        progressDialog.setMessage("Registering...");
        progressDialog.show();


        //create new user
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //user is successfully registered
                    //send email for varification to current user
                    firebaseAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //after sending email
                                        //add all user data into MyAppUser class object
                                        final MyAppUser myAppUser = new MyAppUser();
                                        myAppUser.setName(user);
                                        myAppUser.setEmail(email);
                                        myAppUser.setId(firebaseAuth.getCurrentUser().getUid());
                                        myAppUser.setWallet(0.0);


                                        //specifiy the location for data into firebase Database
                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");
                                        //and save all data into firebase Database
                                        mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(myAppUser);
                                        Toast.makeText(Register.this, "Registered Successfully \n Verify Email and Login", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    //hide progress bar adnd navigate to Login activity
                    progressDialog.hide();
                    finish();
                    startActivity(new Intent(Register.this, Login.class));

                } else {

                    Toast.makeText(Register.this, "Please Try Again. ", Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }

                progressDialog.dismiss();
            }
        });
    }


}
