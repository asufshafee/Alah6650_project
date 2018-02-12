package com.example.amal.linkbike;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.amal.linkbike.Objects.Common;
import com.example.amal.linkbike.Objects.UserRide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Ride extends AppCompatActivity {


    long NowTimeInMiliSecond;
    long StartTimeInMiliSecond;
    long Difference;

    Button CancelReservation;
    TextView Time;

    UserRide userRide;
    Runnable run;
    Handler handler;


    int Hours = 0;
    int Mins = 0;
    int Secs = 0;

    Double MintRate=1.0;
    Double HoursRate=10.0;
    Double Basic=10.0;


    DatabaseReference mDatabaseRef;
    SimpleDateFormat DateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        CancelReservation = (Button) findViewById(R.id.cancelReservation);
        Time = (TextView) findViewById(R.id.ReservationTime);

        userRide = (UserRide) getIntent().getSerializableExtra("CurrentRide");
        StartTimeInMiliSecond = userRide.getStartTimeMiliSecond();
        scheduleThread();

        CancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Double Fare=(Hours*HoursRate)+(Mins*MintRate)+Basic;

                // Created a new Dialog
                final Dialog dialog = new Dialog(Ride.this);
                // inflate the layout
                dialog.setContentView(R.layout.dialog_endride_layout);
                // Set the dialog text -- this is better done in the XML
                Button DepositAmount;
                DepositAmount = (Button) dialog.findViewById(R.id.endConform);
                TextView TotalFare=(TextView)dialog.findViewById(R.id.TotalFare);
                TotalFare.setText("Fare: $"+String.valueOf(Fare));
                DepositAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (handler!=null)
                            handler.removeCallbacks(run);
                        Common.UpdateWallet(-Fare,getApplicationContext());

                        Calendar calendar=Calendar.getInstance();
                        userRide.setStatus(true);
                        userRide.setEndDate(DateFormat.format(calendar.getTime()));
                        userRide.setEndTime(TimeFormat.format(calendar.getTime()));
                        userRide.setDuration(getDurationString(Difference));
                        userRide.setFare(Fare);

                        SharedPreferences prefs = getSharedPreferences("UniqueKey", MODE_PRIVATE);
                            String Key = prefs.getString("Key","no value ");

                        //specifiy the location for data into firebase Database
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Trips");
                        //and save all data into firebase Database
                        mDatabaseRef.child(Key).setValue(userRide);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        dialog.hide();

                    }
                });
                Button Cancel;
                Cancel = (Button) dialog.findViewById(R.id.cencel);
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.hide();

                    }
                });


                dialog.show();

            }
        });


    }

    public void scheduleThread() {
        handler = new Handler();
        run = new Runnable() {

            @Override
            public void run() {
                updateTime();
            }
        };
        handler.postDelayed(run, 1000);
    }
    public void updateTime() {

        scheduleThread();
        NowTimeInMiliSecond = Calendar.getInstance().getTimeInMillis();
        Difference = NowTimeInMiliSecond - StartTimeInMiliSecond;
        Time.setText(getDurationString(Difference));
        Log.d("","Time    "+getDurationString(Difference));

    }

    private String getDurationString(long mills) {

         Hours = (int) (mills/(1000 * 60 * 60));
         Mins = (int) (mills/(1000*60)) % 60;
         Secs = (int) (mills / 1000) % 60;

        return twoDigitString(Hours) + ":" + twoDigitString(Mins) + ":" + twoDigitString(Secs);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
