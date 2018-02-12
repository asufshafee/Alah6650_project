package com.example.amal.linkbike.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amal.linkbike.Objects.Common;
import com.example.amal.linkbike.Objects.MyAppUser;
import com.example.amal.linkbike.Objects.Promotion;
import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWallet extends Fragment {

    Button Deposit;
    TextView payment, cashback, promotions;
    TextView balance;


    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    UserInfo userInfo;

    TextView cashbackAmount;

    public MyWallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wallet, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        cashbackAmount=(TextView) view.findViewById(R.id.cashbackAmount);

        balance = (TextView) view.findViewById(R.id.balance);
        payment = (TextView) view.findViewById(R.id.payment);
        cashback = (TextView) view.findViewById(R.id.cashback);
        promotions = (TextView) view.findViewById(R.id.promotions);
        //get Globale class instance and get value of wallet amount
        userInfo = (UserInfo) getActivity().getApplicationContext();
        balance.setText("$" + userInfo.getWallet());

        if (userInfo.getPayment()==true)
        {
            cashbackAmount.setText("$30");
        }else {
            cashbackAmount.setText("$0");

        }


        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Created a new Dialog
//                final Dialog dialog = new Dialog(getActivity());
//                // inflate the layout
//                dialog.setContentView(R.layout.dialog_payment_layout);
//                // Set the dialog text -- this is better done in the XML
//                Button DepositAmount;
//                DepositAmount = (Button) dialog.findViewById(R.id.cashabckConform);
//                DepositAmount.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (userInfo.getWallet() < 50.0) {
//                            showToast("Please Deposit Your Account");
//                        } else {
//                            if (userInfo.getPayment().equals(false)) {
//                                Common.UpdatePayment(true, getActivity().getApplicationContext());
//                                balance.setText("$" + userInfo.getWallet());
//                            } else {
//                                showToast("Already payed");
//
//                            }
//
//                        }
//                        dialog.hide();
//
//                    }
//                });
//                Button Cancel;
//                Cancel = (Button) dialog.findViewById(R.id.cencel);
//                Cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        dialog.hide();
//
//                    }
//                });
//
//
//                dialog.show();


                ///

                    // Created a new Dialog
                    final Dialog dialog = new Dialog(getActivity());
                    // inflate the layout
                    dialog.setContentView(R.layout.dialog_deposit_layout);
                    // Set the dialog text -- this is better done in the XML
                    final TextView amount=(TextView)dialog.findViewById(R.id.amount);
                    Button DepositAmount;
                    DepositAmount = (Button) dialog.findViewById(R.id.deposit);
                    DepositAmount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.UpdateWallet(Double.parseDouble(amount.getText().toString()),getActivity().getApplicationContext());
                            balance.setText("$" + userInfo.getWallet());

                            if (userInfo.getWallet() < 30.0) {
                                showToast("Please Deposit Your Account");
                            } else {
                                if (userInfo.getPayment().equals(false)) {
                                    Common.UpdatePayment(true, getActivity().getApplicationContext());
                                    balance.setText("$" + userInfo.getWallet());
                                    if (userInfo.getPayment()==true)
                                    {
                                        cashbackAmount.setText("$30");
                                    }else {
                                        cashbackAmount.setText("$0");

                                    }

                                } else {
                                    showToast("Already payed");

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
        cashback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Created a new Dialog
                final Dialog dialog = new Dialog(getActivity());
                // inflate the layout
                dialog.setContentView(R.layout.dialog_cashback_layout);
                // Set the dialog text -- this is better done in the XML
                Button DepositAmount;
                DepositAmount = (Button) dialog.findViewById(R.id.cashabckConform);
                DepositAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            if (userInfo.getPayment().equals(true)) {
                                Common.UpdatePayment(false, getActivity().getApplicationContext());
                                balance.setText("$" + userInfo.getWallet());

                                if (userInfo.getPayment()==true)
                                {
                                    cashbackAmount.setText("$30");
                                }else {
                                    cashbackAmount.setText("$0");

                                }                            } else {
                                showToast("You Have No Cashback");
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
        promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Created a new Dialog
                final Dialog dialog = new Dialog(getActivity());
                // inflate the layout
                dialog.setContentView(R.layout.dialog_promotion_layout);
                // Set the dialog text -- this is better done in the XML
                final EditText PromotionCode = (EditText) dialog.findViewById(R.id.PromotionCode);
                final Button Promotion;
                Promotion = (Button) dialog.findViewById(R.id.promotionDone);
                Promotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Promotion(PromotionCode.getText().toString());
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

        Deposit = (Button) view.findViewById(R.id.deposit);
        Deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Created a new Dialog
                final Dialog dialog = new Dialog(getActivity());
                // inflate the layout
                dialog.setContentView(R.layout.dialog_deposit_layout);
                // Set the dialog text -- this is better done in the XML
                final TextView amount=(TextView)dialog.findViewById(R.id.amount);
                Button DepositAmount;
                DepositAmount = (Button) dialog.findViewById(R.id.deposit);
                DepositAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.UpdateWallet(Double.parseDouble(amount.getText().toString()),getActivity().getApplicationContext());
                        balance.setText("$" + userInfo.getWallet());
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
        return view;
    }


    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    Boolean PromotionCheck = false;

    public void Promotion(final String PromotionCode) {
        final ArrayList<MyAppUser> values = new ArrayList<>();

        if (PromotionCode.equals(userInfo.getId())) {
            showToast("Invalid Code");
            return;
        }


        if (!CheckAlreadyGetPromotion(PromotionCode)) {
            showToast("Invalid Code!! Already Used");
            return;
        } else {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            //getting data from firebase Database
            //first we will get the reference for that
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            //sand we will ask for data location and this mDatabaseRef get all data from that
            //we have userData root object in witch we have User info
            mDatabaseRef.child("userData").addListenerForSingleValueEvent(new ValueEventListener() {


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
                        for (MyAppUser myAppUser : values)
                            if (myAppUser.getId().equals(PromotionCode)) {

                                Common.UpdateWallet(30.0, getActivity().getApplicationContext());
                                balance.setText(userInfo.getWallet() + "$");
                                PromotionCheck = true;
                                Promotion promotion = new Promotion();
                                promotion.setPromotioniD(PromotionCode);
                                promotion.setUserID(myAppUser.getId());
                                //specifiy the location for data into firebase Database
                                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Promotions");
                                //and save all data into firebase Database
                                mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(promotion);
                            }
                    }
                    if (PromotionCheck) {
                        showToast("Success");

                    } else {
                        showToast("Invalid Code");
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

    }

    Boolean AlreadyPromotionCheck = true;

    public Boolean CheckAlreadyGetPromotion(final String PromotionCode) {
        final ArrayList<Promotion> values = new ArrayList<>();


        //getting data from firebase Database
        //first we will get the reference for that
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //sand we will ask for data location and this mDatabaseRef get all data from that
        //we have userData root object in witch we have User info
        mDatabaseRef.child("Promotions").addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //DataSnapshop will receive
                //check if data is exist
                if (dataSnapshot.exists()) {

                    //convert all data into our class object
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Promotion promotion = snapshot.getValue(Promotion.class);
                        values.add(promotion);
                    }
                    for (Promotion myAppUser : values)
                        if (myAppUser.getPromotioniD().equals(PromotionCode)) {

                            AlreadyPromotionCheck = false;

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
        return AlreadyPromotionCheck;
    }

}
