package com.example.bringo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bringo.supportingapis.YahooWeather;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.BLUETOOTH;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        YahooWeather weather = new YahooWeather();
        weather.refreshWeather("pittsburgh");
        System.out.println("the weather is: " + weather.getWeather("07 Apr 2017"));
    }
}
