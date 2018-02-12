package com.example.amal.linkbike.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contact_US extends Fragment {

    EditText UserMessage;
    Button SendEmail;

    public Contact_US() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view=inflater.inflate(R.layout.fragment_contact__u, container, false);

        SendEmail=(Button)view.findViewById(R.id.SendEmail);
        UserMessage=(EditText)view.findViewById(R.id.UserMessage);
        final UserInfo userInfo=(UserInfo)getActivity().getApplicationContext();

        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserMessage.getText().toString()!="")
                    composeEmail( new String [] {"cp670.linkbike@gmail.com"},userInfo.getName()+"\n"+UserMessage.getText().toString());
                else {

                    UserMessage.setError("Enter Message");
                }

            }
        });

        return view;
    }
    public void composeEmail(String[] addresses, String Message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_TEXT,Message);
        intent.putExtra(Intent.EXTRA_SUBJECT,"LinkBike" );
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
