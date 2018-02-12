package com.example.amal.linkbike.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amal.linkbike.Adapters.AutoCompleteAdapter;
import com.example.amal.linkbike.LocationClasses.LocationAddress;
import com.example.amal.linkbike.Objects.Common;
import com.example.amal.linkbike.Objects.Station;
import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.Objects.UserRide;
import com.example.amal.linkbike.R;
import com.example.amal.linkbike.Ride;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment implements OnMapReadyCallback {

    //implements OnMapReadyCallback to get location Services on our map Fragment


    private GoogleMap mMap;

    DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    Boolean mLocationPermissionGranted = true;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    int DEFAULT_ZOOM = 15;

    ArrayList<Station> Stations = new ArrayList<>();

    Button unlock;
    ImageView btnlocation;
    Animation downtoup;

    UserInfo userInfo;

    SimpleDateFormat DateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm");


    AutoCompleteTextView search;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //getting instance of firebase auth for current user
        firebaseAuth = FirebaseAuth.getInstance();

        userInfo = (UserInfo) getActivity().getApplicationContext();

        search = (AutoCompleteTextView) view.findViewById(R.id.search);

        downtoup = AnimationUtils.loadAnimation(getActivity(), R.anim.downtoup);
        unlock = (Button) view.findViewById(R.id.btnunlock);
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if user payment is not verified
                //check if user payment is not verified
                if (userInfo.getPayment().equals(false)) {
                    Common.UpdatePayment(true, getActivity().getApplicationContext());
                }
                if (userInfo.getWallet()<=0)
                {
                    showToast("No Balance to take Ride!!");
                    return;
                }

                // Created a new Dialog
                final Dialog dialog = new Dialog(getActivity());
                // inflate the layout
                dialog.setContentView(R.layout.dialog_unlockbike_layout);
                // Set the dialog text -- this is better done in the XML
                final TextView StationCode = (TextView) dialog.findViewById(R.id.StationCode);
                Button DepositAmount;
                DepositAmount = (Button) dialog.findViewById(R.id.promotionDone);
                DepositAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        for (Station station : Stations) {
                            if (station.getCode().equals(StationCode.getText().toString())) {


                                UserRide userRide = new UserRide();
                                userRide.setId(firebaseAuth.getCurrentUser().getUid());
                                userRide.setStatus(false);
                                userRide.setStationAddress(station.getAddress());
                                userRide.setStationDescripton(station.getDescription());
                                userRide.setFare(0.0);

                                Calendar calendar = Calendar.getInstance();

                                userRide.setStartDate(DateFormat.format(calendar.getTime()));
                                userRide.setStartTime(TimeFormat.format(calendar.getTime()));
                                userRide.setStartTimeMiliSecond(calendar.getTimeInMillis());

                                //specifiy the location for data into firebase Database
                                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Trips").push();
                                String key = mDatabaseRef.getKey();
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("UniqueKey", Context.MODE_PRIVATE).edit();
                                editor.putString("Key", key);
                                editor.apply();
                                //and save all data into firebase Database
                                mDatabaseRef.setValue(userRide);
                                getActivity().finish();
                                Intent intent = new Intent(getActivity().getApplicationContext(), Ride.class);
                                intent.putExtra("CurrentRide", userRide);
                                startActivity(intent);

                            }
                        }

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

        btnlocation = (ImageView) view.findViewById(R.id.btnlocation);
        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();

            }
        });

        //auto complet esearch
        LocationAutocompleate(search);


        //check for Location Permission and do all map procedure after this
        getLocationPermission();


        return view;
    }

    public void MapFuncation() {

        //getLocation Services to from LocationServices class in LicationProvoderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //get map into our map fragment we declare in our xml code
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //show progress bar while getting station info from firebase database
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        //getting data from firebase Database
        //first we will get the reference for that
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //sand we will ask for data location and this mDatabaseRef get all data from that
        //we have locations root object in witch we have locations of all stations
        mDatabaseRef.child("locations").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //DataSnapshop will receive
                //check if data is exist
                if (dataSnapshot.exists()) {

                    //convert all data into our class object
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Station station = snapshot.getValue(Station.class);

                        if (mMap!=null)
                        {

                            //get latlong from firebasedatabase and show then into map
                            LatLng latLng = new LatLng(station.getLat(), station.getLon());
                            Marker mkr = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(station.getDescription())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.station)));
                            station.setId(mkr.getId());
                            //save all station info to a list name StationsLatLng
                            Stations.add(station);
                        }

                    }

                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //if there is an any error
                showToast(databaseError.getMessage());
                progressDialog.dismiss();

            }
        });
    }

        //get location
        private void getMyLocation() {
            //check if location is not open user mobile so open location settings
            if (mLastKnownLocation != null) {
                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                mMap.animateCamera(cameraUpdate);
            } else {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }

        }

    //Override method from OnMapReadyCallback witch we are implementing this will return google map object
    // and we will assign ths object to our mMap object
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        //marker click if user click on stations that we are showing they are actully markers
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //show unlock buttons
                unlock.setVisibility(View.VISIBLE);
                unlock.startAnimation(downtoup);


                return false;
            }
        });

        //map click of user click on map we will hide the unlock button
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //check if unlock button is showing
                if (unlock.getVisibility() == View.VISIBLE) {
                    unlock.setVisibility(View.GONE);
                }

            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.station_layout, null);


                TextView StationAddress = (TextView) v.findViewById(R.id.StationAddress);
                TextView StationName = (TextView) v.findViewById(R.id.StationName);
                TextView StationNumber=(TextView) v.findViewById(R.id.StationNumber);
                //get user clicked marker info from our stations list
                for (Station station : Stations) {
                    if (station.getId().equals(arg0.getId())) {
                        StationName.setText(station.getDescription());
                        StationAddress.setText(station.getAddress());
                        StationNumber.setText("Station "+station.getId().replace("m",""));
                    }
                }

                return v;

            }
        });


        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    public void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            MapFuncation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    11);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    MapFuncation();
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                //if we have permission we will
                //enable this to show current location
                mMap.setMyLocationEnabled(true);
                //enable location button
                btnlocation.setVisibility(View.VISIBLE);
                //hide toolbar that will show default
                mMap.getUiSettings().setMapToolbarEnabled(false);
                //disable builtin location button false
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //disable builtin location button false
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //if don't  have permission we will
                //hide location button
                //current location make false otherwise we will get error
                //get permisssons
                mMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                btnlocation.setVisibility(View.GONE);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            showToast(e.getMessage());
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {

                //make a location task to get location from ProviderClient
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            //get location in location object
                            mLastKnownLocation = (Location) task.getResult();
                            // if location is null means we location is off in user mobile
                            if (mLastKnownLocation != null)
                            {
                                getMyLocation();
                            }


                            else {
                                //open locaiton setting to open location
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    //auto complete search class


    public void LocationAutocompleate(AutoCompleteTextView locationEditext) {
        locationEditext.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        locationEditext.setAdapter(new AutoCompleteAdapter(getActivity(), R.layout.location_list_item));
        locationEditext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });
        locationEditext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show Alert

                Log.d("AutocompleteContacts", "Position:" + position + " Month:" + parent.getItemAtPosition(position));

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);


                if (search.getText().toString().length() > 0) {

                    if (Common.isNetworkAvailable(getActivity())) {

                        LocationAddress.getAddressFromLocation(search.getText().toString(), getActivity().getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(getActivity(), "No Network", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    private class GeocoderHandlerLatitude extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;

            }
            if (locationAddress != null) {
                if (locationAddress.equals("Unable connect to Geocoder")) {
                    Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_LONG).show();
                } else {
                    String[] LocationSplit = locationAddress.split("\\,");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.parseDouble(LocationSplit[0]), Double.parseDouble(LocationSplit[1])))      // Sets the center of the map to location user
                            .zoom(DEFAULT_ZOOM)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            }
        }
    }

}
