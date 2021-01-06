package com.amozeng.a4_knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final List<Office> officeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficeAdapter mAdapter;
    private String location;
    private String zip;

    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;

    private double latitude, longitude;

    private static int OPEN_OFFICIAL_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        mAdapter = new OfficeAdapter(officeList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // location part
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        // gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE_ID);
        }else{
            //todo what should I put here?
            getLatLong();
        }


        InfoDownloader infoDownloader = new InfoDownloader(this, zip);
        new Thread(infoDownloader).start();
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(this,"click", Toast.LENGTH_SHORT).show();
        int pos = recyclerView.getChildLayoutPosition(v);
        Office office = officeList.get(pos);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OPEN_OFFICIAL", office);
        intent.putExtra(Intent.EXTRA_TEXT, location);
        startActivityForResult(intent, OPEN_OFFICIAL_REQUEST);
    }

    @Override
    public boolean onLongClick(View v) {
        //Toast.makeText(this,"long click", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_about:
                // open About Page
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                setLocaitonDialog();
                //setLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_GRANTED) {
                //setLocation();
                getLatLong();
                return;
            }
        }
        ((TextView) findViewById(R.id.location)).setText("NO Data For Location");
    }

    private void setLocaitonDialog(){

        // Check network
        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without a network connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        // Dialog START
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setLocation(input.getText().toString());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to cancel the dialog
            }
        });

        builder.setMessage("Enter a City, State or a Zip Code:");

        AlertDialog dialog = builder.create();
        dialog.show();
        // Dialog END


    }

    private void setLocation(String input){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addresses;

            String loc = ((TextView) findViewById(R.id.location)).getText().toString();
            addresses = geocoder.getFromLocationName(input, 5);
            displayAddress(addresses);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        InfoDownloader infoDownloader = new InfoDownloader(this, input);
        officeList.clear();
        new Thread(infoDownloader).start();
    }


    @SuppressLint("MissingPermission")
    private void getLatLong(){

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without a network connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            ((TextView) findViewById(R.id.location)).setText("No Data For Location");
            return;
        }

        // Location Start
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        if(bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if(currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            ((TextView) findViewById(R.id.location)).setText(String.format(Locale.getDefault(), currentLocation.toString()));
        }
        doLatDon();
    }

    private void doLatDon() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
            displayAddress(addresses);
        } catch (IOException e){
            // todo: Toast or not?
            e.printStackTrace();
        }
    }

    private void displayAddress(List<Address> addresses) {


        if(addresses.size() == 0){
            ((TextView) findViewById(R.id.location)).setText("No Data For Location");
            return;
        }

        Address one = addresses.get(0);

        // check if it's null or not
        String city = one.getLocality() == null ? "" : one.getLocality();
        String postalCode = one.getPostalCode() == null ? "" : one.getPostalCode();
        String state = one.getAdminArea() == null ? "" : one.getAdminArea();
        location = city + ", " + state + " " + postalCode;
        zip = postalCode;
        ((TextView) findViewById(R.id.location)).setText(city + ", " + state + " " + postalCode);
    }

    public void addOffice(Office newOffice) {
        if(newOffice == null)
        {
            Log.d(TAG, "addOffice: null new office to add");
        }

        officeList.add(newOffice);
        mAdapter.notifyDataSetChanged();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}