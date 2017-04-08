package com.example.bringo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.bringo.supportingapis.GoogleGeocoding;
import com.example.bringo.supportingapis.YahooWeather;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SettingsActivity extends AppCompatActivity implements LocationListener {

    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 1;

    private Button test;
    private Switch locationSwitch;
    private Location currentLocation;

    private YahooWeather weather;
    private GoogleGeocoding geocoding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        test = (Button) findViewById(R.id.testingButton);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather();
            }
        });

        locationSwitch = (Switch) findViewById(R.id.switch1);
        locationSwitch.setOnCheckedChangeListener(new AcLocationListener());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, GET_WEATHER_FROM_CURRENT_LOCATION);
            }
        }
        getCurrentLocation();
    }

    private Location getCurrentLocation() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        System.out.println("GPS"+isGPSEnabled);
        if(!isGPSEnabled) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }

        locationManager.requestSingleUpdate(locationCriteria, this, null);
        locationManager.requestSingleUpdate(locationCriteria, this, null);

        String provider = locationManager.getBestProvider(locationCriteria, true);
//        System.out.println("Best provider is " + provider);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        currentLocation = locationManager.getLastKnownLocation(provider);
//        System.out.println("location is " + currentLocation);
        return currentLocation;
    }

    //may be called from outside??
    private void getWeather() {

        String address = geocoding.getAddress();
        System.out.println("address: " + address);

        weather = new YahooWeather();
        weather.refreshWeather(address);
    }

    @Override
    public void onLocationChanged(Location location) {
        getCurrentLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class AcLocationListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                System.out.println("begin");
                if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    geocoding = new GoogleGeocoding();
                    getCurrentLocation();
                    System.out.println("current location: " + currentLocation);
                    if (currentLocation != null) {
//                        System.out.println("lat: " + currentLocation.getLatitude());
//                        System.out.println("lng: " + currentLocation.getLongitude());
                        geocoding.refreshLocation(currentLocation);
                    }
                    return;
                } else {
                    AlertDialog.Builder window =new AlertDialog.Builder(SettingsActivity.this);
                    window.setTitle("Access to location")
                            .setMessage("Please change your settings to authorize Bringo to access your location. ")
                            .setPositiveButton("OK", null)
                            .show();
                    locationSwitch.setChecked(false);
                }
            }
        }
    }

}
