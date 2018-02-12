package com.example.amal.linkbike.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amal.linkbike.Objects.Common;
import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.Objects.UserRide;
import com.example.amal.linkbike.R;
import com.example.amal.linkbike.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    TextView Username,Username1,Email,TotalTrips,Wbalance;
    UserInfo userInfo;

    private FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    private List<UserRide> TripsList = new ArrayList<>();

    ImageView Profile;
    private static final int SELECT_PICTURE = 0;

    // uplaoding image to firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;



    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Inflate the layout for this fragment
        userInfo = (UserInfo) getActivity().getApplicationContext();
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        Profile=(ImageView)view.findViewById(R.id.Profile);

        if (userInfo.getProfileUrl()!=null)
        Picasso.with(getActivity())
                .load(userInfo.getProfileUrl())
                .into(Profile);

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getstoragePermission();

            }
        });

        Username=(TextView)view.findViewById(R.id.userName);
        Username1=(TextView)view.findViewById(R.id.Username1);
        Email=(TextView)view.findViewById(R.id.userEmail);
        TotalTrips=(TextView)view.findViewById(R.id.TotalTrips);
        Wbalance=(TextView)view.findViewById(R.id.WBalance);

        Username.setText(userInfo.getName());
        Username1.setText(userInfo.getName());
        Email.setText(userInfo.getEmail());
        Wbalance.setText("$"+String.valueOf(userInfo.getWallet()));
        GetTrips();

        return view;
    }
    private void GetTrips() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //sand we will ask for data location and this mDatabaseRef get all data from that
        mDatabaseRef.child("Trips").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //DataSnapshop will receive
                //check if data is exist
                if (dataSnapshot.exists()) {

                    //convert all data into our class object
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserRide myTrip = snapshot.getValue(UserRide.class);
                        if (myTrip.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                            TripsList.add(myTrip);
                        }
                    }
                    TotalTrips.setText(String.valueOf(TripsList.size()));


                }

                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //if there is an any error
                progressDialog.dismiss();
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Profile.setImageBitmap(bitmap);
            uploadImage();
        }
    }

    private void selectImage() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void uploadImage() {

        progressDialog.show();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (filePath != null) {

            String uri = filePath.toString();


            FirebaseApp.initializeApp(getActivity());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReferenceFromUrl("gs://linkbike-c0cd0.appspot.com/");
            StorageReference imagesRef = reference.child("Images/" + userInfo.getEmail().trim()+"."+getMimeType(getActivity().getApplicationContext(),filePath).trim());

            Common.UpdateProfile(getActivity().getApplicationContext(),userInfo.getEmail()+"."+getMimeType(getActivity().getApplicationContext(),filePath).trim());
            userInfo.setProfileLoadCheck(false);

            UploadTask uploadTask = imagesRef.putFile(filePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DownlaodProfile();

                }
            });
        }
    }


    private void getstoragePermission() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            selectImage();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    11);
        }
    }
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void DownlaodProfile()
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Images/"+userInfo.getProfile());

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                userInfo.setProfileUrl(uri.toString());
                progressDialog.dismiss();
                //Handle whatever you're going to do with the URL here
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
