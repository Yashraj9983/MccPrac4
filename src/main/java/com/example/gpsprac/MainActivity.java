package com.example.gpsprac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    TextView latitude, longitude, altitude, accuracy, speed, address;
    Switch turnOnLocation, turnOnGPS;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean updateOn = false;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        altitude = findViewById(R.id.altitude);
        accuracy = findViewById(R.id.accuracy);
        speed = findViewById(R.id.speed);
        address = findViewById(R.id.address);
        turnOnLocation = findViewById(R.id.location);
        turnOnGPS = findViewById(R.id.gps);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 15);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocationValues(locationResult.getLastLocation());
            }
        };

        turnOnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (turnOnGPS.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        });
        turnOnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (turnOnLocation.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
        updateGps();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGps();
    }

    private void stopLocationUpdates(){
        latitude.setText("Not Tracking...");
        longitude.setText("Not Tracking...");
        altitude.setText("Not Tracking...");
        accuracy.setText("Not Tracking...");
        speed.setText("Not Tracking...");
        address.setText("Not Tracking...");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateGps();
                }else {
                    Toast.makeText(this,"requires permission",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGps(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateLocationValues(location);
                }
            });
        }else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }

    }

    private void updateLocationValues(Location location){
        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));
        accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            altitude.setText(String.valueOf(location.getAltitude()));
        }else {
            altitude.setText("Not Available");
        }
        if(location.hasSpeed()){
            speed.setText(String.valueOf(location.getSpeed()));
        }else {
            altitude.setText("Not Available");
        }

    }
}