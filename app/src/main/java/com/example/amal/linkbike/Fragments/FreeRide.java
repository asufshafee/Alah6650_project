package com.example.amal.linkbike.Fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amal.linkbike.Objects.UserInfo;
import com.example.amal.linkbike.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FreeRide extends Fragment {


    Button CopyCode, InviteFriends;
    TextView PromotionCode;

    public FreeRide() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_free_ride, container, false);
        CopyCode = (Button) view.findViewById(R.id.CopyCode);
        CopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboardMgr = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardMgr.setText(PromotionCode.getText().toString());
                    showToast("Copied");
                } else {
                    // this api requires SDK version 11 and above, so suppress warning for now
                    android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text", PromotionCode.getText().toString());
                    clipboardMgr.setPrimaryClip(clip);
                    showToast("Copied");

                }


            }
        });
        PromotionCode = (TextView) view.findViewById(R.id.Code);
        UserInfo userInfo = (UserInfo) getActivity().getApplicationContext();
        PromotionCode.setText(" " + userInfo.getId());

        InviteFriends = (Button) view.findViewById(R.id.InviteFriends);
        InviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, "Use This Invitation Code To Get Promotion " + PromotionCode.getText());
                startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });
        return view;
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

}
