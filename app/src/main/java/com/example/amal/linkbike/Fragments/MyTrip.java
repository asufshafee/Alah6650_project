package com.example.amal.linkbike.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amal.linkbike.Adapters.TripsAdapter;
import com.example.amal.linkbike.Objects.MyAppUser;
import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.Objects.UserRide;
import com.example.amal.linkbike.R;
import com.example.amal.linkbike.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTrip extends Fragment {

    private FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;


    private List<UserRide> TripsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TripsAdapter mAdapter;

    public MyTrip() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_trip, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new TripsAdapter(TripsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareTripsData();
        return view;


    }

    private void prepareTripsData() {
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
                        try {


                            if (myTrip.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                TripsList.add(myTrip);
                            }
                        }catch (Exception a)
                        {

                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
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
    public void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

}
