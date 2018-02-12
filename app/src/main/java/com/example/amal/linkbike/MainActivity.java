package com.example.amal.linkbike;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amal.linkbike.Fragments.*;
import com.example.amal.linkbike.Fragments.Help;
import com.example.amal.linkbike.Objects.Common;
import com.example.amal.linkbike.Objects.Feedback;
import com.example.amal.linkbike.Objects.MyAppUser;
import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.Objects.UserRide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ArrayList<MyAppUser> values = new ArrayList<>();
    MyAppUser CurrentUser;

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    TextView UserEmail;
    TextView Username;

    Fragment fragment = null;
    Class fragmentClass = null;
    UserInfo globaUserInfo;

    TextView ContacUS, Help;


    ImageView Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentClass = Home.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting instance of firebase auth for current user
        firebaseAuth = FirebaseAuth.getInstance();
        //to check if the user is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            //finish the current activity
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }


        //getting current user info in FirebaseUser class Object
        user = firebaseAuth.getCurrentUser();


        //code for adding drawer layout into our current activity
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer

                if (globaUserInfo!=null)
                if (globaUserInfo.getProfileUrl()!=null && globaUserInfo.getProfileLoadCheck().equals(false))
                {
                    Picasso.with(MainActivity.this)
                            .load(globaUserInfo.getProfileUrl())
                            .into(Profile);
                    globaUserInfo.setProfileLoadCheck(true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

        //code for navigation when user click to open navigation drawer this will show view for that
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //creating references to xml file id's
        //initialise view objects through id
        //these id's will acccess from navigation view
        View headerView = navigationView.getHeaderView(0);
        UserEmail = (TextView) headerView.findViewById(R.id.userEmail);
        Username = (TextView) headerView.findViewById(R.id.Username);
        Profile=(ImageView)headerView.findViewById(R.id.profile);


        ContacUS = (TextView) findViewById(R.id.contactUS);
        Help = (TextView) findViewById(R.id.Help);
        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentClass = Help.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        ContacUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentClass = Contact_US.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            }
        });


        //getting data from firebase Database
        //first we will get the reference for that
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //sand we will ask for data location and this mDatabaseRef get all data from that
        //we have userData root object in witch we have User info
        mDatabaseRef.child("userData").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //DataSnapshop will receive
                //check if data is exist
                if (dataSnapshot.exists()) {

                    //convert all data into our class object
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyAppUser myAppUser = snapshot.getValue(MyAppUser.class);
                        values.add(myAppUser);

                    }
                    //get data for current user object by his Uid
                    for (MyAppUser myAppUser : values)
                        if (myAppUser.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                            CurrentUser = myAppUser;
                            //Display user Data into UserEmail and Username Fields in Navigation Drawer
                            UserEmail.setText(myAppUser.getEmail());
                            Username.setText(myAppUser.getName());

                            //set userinfo to Gloable Class so we can access info in our app
                            globaUserInfo = (UserInfo) getApplicationContext();
                            globaUserInfo.setId(myAppUser.getId());
                            globaUserInfo.setEmail(myAppUser.getEmail());
                            globaUserInfo.setName(myAppUser.getName());
                            globaUserInfo.setWallet(myAppUser.getWallet());
                            globaUserInfo.setPayment(myAppUser.getPayment());
                            globaUserInfo.setProfile(myAppUser.getProfile());
                            DownlaodProfile();

                        }
                    //sand we will ask for data location and this mDatabaseRef get all data from that
                    mDatabaseRef.child("Trips").addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ArrayList<UserRide> Trips = new ArrayList<>();

                            //DataSnapshop will receive
                            //check if data is exist
                            if (dataSnapshot.exists()) {

                                //convert all data into our class object
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    UserRide myTrip = snapshot.getValue(UserRide.class);
                                    Trips.add(myTrip);

                                }
                                //get data for current user object by his Uid
                                for (UserRide myTrip : Trips)
                                    if (myTrip.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                        if (myTrip.getStatus().equals(false)) {
                                            finish();
                                            Intent intent = new Intent(getApplicationContext(), Ride.class);
                                            intent.putExtra("CurrentRide", myTrip);
                                            startActivity(intent);
                                        }
                                    }
                            }
                            progressDialog.dismiss();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //if there is an any error
                            progressDialog.dismiss();
                            showToast(databaseError.getMessage());
                        }
                    });


                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //if there is an any error
                progressDialog.dismiss();
                showToast(databaseError.getMessage());
            }
        });


    }

    //to hide navigation drawer on backpress

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.lagout) {

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));

        } else if (id == R.id.mywallet) {

            fragmentClass = MyWallet.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        } else if (id == R.id.home) {
            fragmentClass = Home.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } else if (id == R.id.mytrip) {
            fragmentClass = MyTrip.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } else if (id == R.id.freerides) {
            fragmentClass = FreeRide.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        } else if (id == R.id.facebook) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AmalAlahamdi")));
        }
        if (id == R.id.profile) {
            fragmentClass = Profile.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } else if (id == R.id.feedback) {



            // Created a new Dialog
            final Dialog dialog = new Dialog(this);
            // inflate the layout
            dialog.setContentView(R.layout.dialog_feedback_layout);
            // Set the dialog text -- this is better done in the XML
             Button DepositAmount;
            DepositAmount = (Button) dialog.findViewById(R.id.Submit);
            DepositAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar;
                    ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);

                    Feedback feedback=new Feedback();
                    feedback.setUsername(globaUserInfo.getName());
                    feedback.setUserEmail(globaUserInfo.getEmail());
                    feedback.setRatting(ratingBar.getRating());

                    //specifiy the location for data into firebase Database
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("Feedback");
                    //and save all data into firebase Database
                    mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(feedback);
                    showToast("Thanks For Ratting!!");

                    dialog.hide();

                }
            });
            Button Cancel;
            Cancel = (Button) dialog.findViewById(R.id.cancel);
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.hide();

                }
            });


            dialog.show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

    public void DownlaodProfile()
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Images/"+globaUserInfo.getProfile());

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Picasso.with(MainActivity.this)
                        .load(uri.toString())
                        .into(Profile);
                globaUserInfo.setProfileUrl(uri.toString());
                globaUserInfo.setProfileLoadCheck(true);
                //Handle whatever you're going to do with the URL here
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
