package com.amozeng.a4_knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ImageView officialImageView;
    private ImageView partyIcon;

    private static final String TAG = "OfficialActivity";
    private TextView location;
    private TextView office;
    private TextView name;
    private TextView party;
    private TextView address, phone, website, email;

    private Office tmpOffice;
    private String imageURL = "";
    private String facebookID;
    private String twitterID;
    private String youtubeID;

    private static int OPEN_IMAGE_REQUEST = 1;

    private static final String URL_DEM = "https://democrats.org";
    private static final String URL_REP = "https://www.gop.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        location = findViewById(R.id.location_offical);
        officialImageView = findViewById(R.id.OfficialImageView);
        partyIcon = findViewById(R.id.icon_party);

        office = findViewById(R.id.official_office);
        name = findViewById(R.id.official_name);
        party = findViewById(R.id.official_party);
        address = findViewById(R.id.address_detail);
        phone = findViewById(R.id.phone_detail);
        website = findViewById(R.id.website_detail);
        email = findViewById(R.id.email_detail);
        constraintLayout = findViewById(R.id.photo_constrainLayout);

        Intent intent = getIntent();
        if(intent.hasExtra("OPEN_OFFICIAL")){
            tmpOffice = (Office) intent.getSerializableExtra("OPEN_OFFICIAL");
            if(tmpOffice != null){
                if(tmpOffice.getPhotoUrls() != null){
                    imageURL = tmpOffice.getPhotoUrls();
                }else{
                    imageURL = "https://images-assets.nasa.gov/image/6900952/does_not_exist.jpg";
                }
            }
        }
        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            String loc = intent.getStringExtra(Intent.EXTRA_TEXT);
            location.setText(loc);
        }
        loadOfficeInfo();
        loadRemoteImage();

        // Linkify
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(website, Linkify.ALL);
        Linkify.addLinks(phone, Linkify.ALL);
        Linkify.addLinks(email, Linkify.ALL);
    }

    private void loadOfficeInfo(){

        office.setText(tmpOffice.getOffice());
        name.setText(tmpOffice.getName());

        String partyString = tmpOffice.getParty();
        if(partyString != null){
            party.setText("(" + partyString + ")");

            if(partyString.contains("Democratic")) {
                constraintLayout.setBackgroundColor(Color.BLUE);
                partyIcon.setImageResource(R.drawable.dem_logo);
            } else if (partyString.contains("Republican")){
                constraintLayout.setBackgroundColor(Color.RED);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }else{
                constraintLayout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(ImageView.GONE);
            }
        }

        // ADDRESS
        if(tmpOffice.getAddress() != null) {

            address.setText(tmpOffice.getAddress());
        }else{
            TextView addressTitle = findViewById(R.id.addressTitle);
            addressTitle.setVisibility(TextView.GONE);
            address.setVisibility(TextView.GONE);
        }

        // PHONE
        if(tmpOffice.getPhone() != null) {
            phone.setText(tmpOffice.getPhone());
        }else{
            TextView phoneTitle = findViewById(R.id.phone_title);
            phoneTitle.setVisibility(TextView.GONE);
            phone.setVisibility(TextView.GONE);
        }

        // WEBSITE
        if(tmpOffice.getWebsite() != null) {
            website.setText(tmpOffice.getWebsite());
        }else{
            TextView webTitle = findViewById(R.id.website_title);
            webTitle.setVisibility(TextView.GONE);
            website.setVisibility(TextView.GONE);
        }

        // EMAIL
        String test = tmpOffice.getEmail();
        if(tmpOffice.getEmail() != null) {
            email.setText(tmpOffice.getEmail());
        }else{
            TextView emailTitle = findViewById(R.id.email_title);
            emailTitle.setVisibility(TextView.GONE);
            email.setVisibility(TextView.GONE);
        }

        // FACEBOOK
        if(tmpOffice.getFacebookID() != null){
            facebookID = tmpOffice.getFacebookID();
        }else{
            ImageView facebookICON = findViewById(R.id.facebook);
            facebookICON.setVisibility(ImageView.GONE);
        }

        // TWITTER
        if(tmpOffice.getTwitterID() != null){
            twitterID = tmpOffice.getTwitterID();
        }else{
            ImageView twitterICON = findViewById(R.id.twitter);
            twitterICON.setVisibility(ImageView.GONE);
        }

        // YOUTUBE
        if(tmpOffice.getYoutubeID() != null){
            youtubeID = tmpOffice.getYoutubeID();
        }else{
            ImageView youtubeICON = findViewById(R.id.youtube);
            youtubeICON.setVisibility(ImageView.GONE);
        }
    }

    private void loadRemoteImage(){

        if (!checkNetworkConnection()) {
            officialImageView.setImageResource(R.drawable.brokenimage);
            return;
        }

        Picasso.get().load(imageURL).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(officialImageView,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) officialImageView.getDrawable()).getBitmap().getByteCount());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public void clickFacebook(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookID;
        Intent intent;
        String urlToUse;
        try{
            getPackageManager().getPackageInfo("com.facebook.katana",0);
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if(versionCode >= 3002850) { // newer version of Facebook app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }else { // older
                urlToUse = "fb://page/" + facebookID;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        }catch (Exception e){ // if no app, open through web browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }
        startActivity(intent);
    }

    public void clickTwitter(View v) {
        String twitterAppUrl = "twitter://user?screen_name=" + twitterID;
        String twitterWebUrl = "https://twitter.com/" + twitterID;

        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + youtubeID));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + youtubeID)));
        }
    }

    public void clickImage(View v){
        if(tmpOffice.getPhotoUrls() != null){
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            String loc = location.getText().toString();
            String p = party.getText().toString();

            intent.putExtra("OFFICE", tmpOffice);
            intent.putExtra("LOCATION", loc);

            startActivityForResult(intent, OPEN_IMAGE_REQUEST);
        }
    }


    public void clickPartyIcon(View v){
        Intent intent = null;
        if(tmpOffice.getParty().contains("Democratic")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_DEM));
        }else if(tmpOffice.getParty().contains("Republican")){
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