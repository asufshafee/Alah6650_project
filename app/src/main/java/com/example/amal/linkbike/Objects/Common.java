package com.example.amal.linkbike.Objects;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Common {

    private static DatabaseReference mDatabaseRef;
    private static FirebaseAuth firebaseAuth;

    public static boolean isNetworkAvailable(Activity act) {

        ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return true;
        } else {
            // display error
            return false;
        }

    }
    public static void UpdateWallet(Double wallet, Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        //specifiy the location for data into firebase Database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");
        //and save all data into firebase Database
        final UserInfo globaUserInfo = (UserInfo)context;
        globaUserInfo.setWallet(globaUserInfo.getWallet()+(wallet));
        MyAppUser myAppUser=globaUserInfo.GetUser();
        mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(myAppUser);

    }
    public static void UpdatePayment(Boolean pay ,Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        //specifiy the location for data into firebase Database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");
        //and save all data into firebase Database
        final UserInfo globaUserInfo = (UserInfo)context;
        globaUserInfo.setPayment(pay);
        if (pay==true)
        {
            globaUserInfo.setWallet(globaUserInfo.getWallet()+(-30.0));
        }else {
            globaUserInfo.setWallet(globaUserInfo.getWallet()+(30.0));
        }

        MyAppUser myAppUser=globaUserInfo.GetUser();
        mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(myAppUser);
    }
    public static void UpdateProfile(Context context,String Path) {
        firebaseAuth = FirebaseAuth.getInstance();
        //specifiy the location for data into firebase Database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userData");
        //and save all data into firebase Database
        final UserInfo globaUserInfo = (UserInfo)context;
        globaUserInfo.setProfile(Path);


        MyAppUser myAppUser=globaUserInfo.GetUser();
        mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(myAppUser);
    }


}
