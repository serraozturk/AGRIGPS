package edu.htwd.gpstracktutorial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class GPSView extends AppCompatActivity {

    private Button move;

    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_wayPointCounts;
    Button btn_newWayPoint, btn_showWayPointList;
    Switch sw_locationupdates, sw_gps;

    //  variable to remember if we are tracking or not
    boolean update0n = false;

    //current location
    Location currentLocation;

    //list of saved locations
    List<Location> savedLocations;

    //Location request is a config file for all settings realeted to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    //Google location API. The majority of the App features depends on this.
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gpsview);

        move = findViewById(R.id.changeactivity);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GPSView.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // give each UI variable a value

        tv_lat = findViewById(R.id.tv_lat);

        tv_lon = findViewById(R.id.tv_lon);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = findViewById(R.id.btn_showWayPointList);
        tv_wayPointCounts = findViewById(R.id.tv_countOfCrumbs);


        //set all properties of LocationRequest
        locationRequest = new LocationRequest();

        //how often does the location request occur
        locationRequest.setInterval(1000 * DefaultUpdate());

        //how ofted does the location request occur when set to the most frequent update
        locationRequest.setFastestInterval(1000 * FastUpdateDefault());

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //event triggered  whenever the update interval is meet.
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location

                updateUIValues(locationResult.getLastLocation());
            }
        };

        btn_newWayPoint.setOnClickListener();Clicklistener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                //get the GPS location

                //add the new location to the global list
                MyApplication myApplication = (MyApplication)getApplicationContext();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    //turn on location tracking
                    startLocationUpdate();
                } else {
                    //turn off tracking
                    stopLocationUpdate();
                }
            }
        });


        updateGPS();

    }//end of CreateMethod

    private void stopLocationUpdate() {
        tv_updates.setText("Location is NOT being Track");
        tv_lat.setText("Location is not Being track");
        tv_lon.setText("Location is not Being track");
        tv_speed.setText("Location is not Being track");
        tv_address.setText("Location is not Being track");
        tv_accuracy.setText("Location is not Being track");
        tv_altitude.setText("Location is not Being track");
        tv_sensor.setText("Location is not Being track");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }
    //end of stop method



    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        tv_updates.setText("Location is being Track");

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper());
        updateGPS();

    }
    //end of start method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app required permission to be granted in order to work propertly", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    public void updateGPS(){
        //get permission from the user to track GPS
        //get the current location from the fused client
        //update the UI - i.e set all the properties in their associted text view items.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GPSView.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provide permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got permission. Put values of Location XX into UI Components
                    updateUIValues(location);
                    currentLocation = location;
                }
            });
        }
        else {
            //permission not granted yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }

        }
    }

    private void updateUIValues(Location location) {
        //update all the text views
        tv_lat.setText(String.valueOf(location.getLatitude()));

        tv_lon.setText(String.valueOf(location.getLongitude()));

        if (location.hasAccuracy()){
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        }



        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not available");
        }

        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not available");
        }

        //translation to Adresse
        Geocoder geocoder = new Geocoder(GPSView.this);

        try {
            //Look for the Adress correct
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getCountryName());
        }
        catch (Exception e){
            //Exception in case adress is not found
            tv_address.setText("Unable to get Country name");
        }
    }


    private static int FastUpdateDefault() {
        return 5;
    }

    private static int DefaultUpdate() {
        return 30;
    }

    // show the number of waypoints saved
    tv_wayPointCounts.setText(Integer.toString(savedlocations.size()));

}