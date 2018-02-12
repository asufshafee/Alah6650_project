package com.example.amal.linkbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password extends AppCompatActivity {

    private EditText Email;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);

        Email = (EditText) findViewById(R.id.userEmail);

        //progress bar for loading
        progressDialog = new ProgressDialog(this);

    }

    public void Forget(View view) {


        String email = Email.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email field is empty
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Sending Email...");
        progressDialog.show();
        //getting instance of firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;

        //sending email
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        //if user enter email is correct
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(Forget_Password.this, Login.class));
                        }else {
                            Toast.makeText(getApplicationContext(), "Something Wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
