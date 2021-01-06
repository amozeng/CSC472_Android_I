package com.amozeng.a4_knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";

    private ConstraintLayout constraintLayout;

    private TextView location;
    private TextView office;
    private TextView name;

    private ImageView portrait;
    private ImageView partyIcon;

    private String party;
    private String imageURL;

    private Office officeHolder;

    private static final String URL_DEM = "https://democrats.org";
    private static final String URL_REP = "https://www.gop.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        constraintLayout = findViewById(R.id.photo_constrainLayout);

        location = findViewById(R.id.photo_location);
        office = findViewById(R.id.photo_office);
        name = findViewById(R.id.photo_name);

        portrait = findViewById(R.id.photo_image);
        partyIcon = findViewById(R.id.photo_party);


        Intent intent = getIntent();

        if(intent.hasExtra("OFFICE")) {
            officeHolder = (Office) intent.getSerializableExtra("OFFICE");
        }
        party = officeHolder.getParty();
        imageURL = officeHolder.getPhotoUrls();

        office.setText(officeHolder.getOffice());
        name.setText(officeHolder.getName());


        if(intent.hasExtra("LOCATION")){
            String loc = intent.getStringExtra("LOCATION");
            location.setText(loc);
        }

        setPartyIcon();
        loadRemoteImage();
    }

    private void setPartyIcon() {
        if (party.contains("Democratic")){
            partyIcon.setImageResource(R.drawable.dem_logo);
            constraintLayout.setBackgroundColor(Color.BLUE);
        }else if(party.contains("Republican")){
            partyIcon.setImageResource(R.drawable.rep_logo);
            constraintLayout.setBackgroundColor(Color.RED);
        }else{
            partyIcon.setVisibility(ImageView.GONE);
            constraintLayout.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadRemoteImage(){

        if (!checkNetworkConnection()) {
            portrait.setImageResource(R.drawable.brokenimage);
            return;
        }

        Picasso.get().load(imageURL).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(portrait,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) portrait.getDrawable()).getBitmap().getByteCount());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public void clickPartyIcon(View v){
        Intent intent = null;
        if(officeHolder.getParty().contains("Democratic")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_DEM));
        }else if(officeHolder.getParty().contains("Republican")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_REP));
        }else{
            Log.d(TAG, "clickPartyIcon: something wrong here");
        }
        startActivity(intent);
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}