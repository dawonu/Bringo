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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bringo.supportingapis.GoogleGeocoding;
import com.example.bringo.supportingapis.WeatherAPI;
import com.example.bringo.supportingapis.YahooWeather;

import org.w3c.dom.Text;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.SET_ALARM;

public class SettingsActivity extends AppCompatActivity {

    public static int REQUEST_CODE = 1;

    private TextView name;
    private Switch locationSwitch;
    private Switch calendarSwitch;
    private Switch bluetoothSwitch;
    private Switch calendarReminder;
    private Switch travelReminder;
    private BottomNavigationView mBottomNav;

    private UserDB userDB = UserDB.listAll(UserDB.class).get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set bottom bar selection
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_settings);

        //listener for nav item
//        mBottomNav.setOnNavigationItemSelectedListener();

        // display the email address
        name = (TextView) findViewById(R.id.emailDisplay);
        if (userDB != null) {
            name.setText("user: " + userDB.getUserName());
        }

        // 5 setting switches
        locationSwitch = (Switch) findViewById(R.id.switch1);
        locationSwitch.setOnCheckedChangeListener(new AcLocationListener());

        calendarSwitch = (Switch) findViewById(R.id.switch2);
        calendarSwitch.setOnCheckedChangeListener(new AcCalendarListener());

        bluetoothSwitch = (Switch) findViewById(R.id.switch3);
        bluetoothSwitch.setOnCheckedChangeListener(new AcBluetoothListener());

        calendarReminder = (Switch) findViewById(R.id.switch4);
        calendarReminder.setOnCheckedChangeListener(new CalendarReminderListener());

        travelReminder = (Switch) findViewById(R.id.switch5);
        travelReminder.setOnCheckedChangeListener(new TravelReminderListener());

        // asking for access
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String[] permissions = new String[] {SET_ALARM, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, BLUETOOTH, BLUETOOTH_ADMIN};
            boolean needGrantedPermissions = false;
            for (int i = 0, size = permissions.length; i < size; i++) {
                if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    needGrantedPermissions = true;
                } else {
                    permissions[i] = "";
                }
            }

            if (needGrantedPermissions) {
                requestPermissions(permissions, REQUEST_CODE);
            }
        }
    }


    private class AcLocationListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                System.out.println("begin");
                if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (userDB != null) {
                        System.out.print(userDB.getUserName());
                        userDB.setAcLocation(true);
                        userDB.save();
                        System.out.println("name: " + userDB.getUserName());
                        System.out.println("status: " + userDB.getAcLocation());
                    }
                } else {
                    AlertDialog.Builder window =new AlertDialog.Builder(SettingsActivity.this);
                    window.setTitle("Access to location")
                            .setMessage("Please change your settings to authorize Bringo to access your location." +
                                    "(in Settings - APP) ")
                            .setPositiveButton("OK", null)
                            .show();
                    locationSwitch.setChecked(false);
                }
            } else {
                if (userDB != null) {
                    userDB.setAcLocation(false);
                    userDB.save();
                }
            }
        }
    }

    private class AcCalendarListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                } else {

                }
            }
        }

    private class AcBluetoothListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                System.out.println("begin");
                if (checkSelfPermission(BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    if (userDB != null) {
                        System.out.print(userDB.getUserName());
                        userDB.setAcBluetooth(true);
                        userDB.save();
                        System.out.println("name: " + userDB.getUserName());
                        System.out.println("status: " + userDB.getAcBluetooth());
                    }
                } else {
                    AlertDialog.Builder window =new AlertDialog.Builder(SettingsActivity.this);
                    window.setTitle("Access to Bluetooth")
                            .setMessage("Please change your settings to authorize Bringo to access your bluetooth." +
                                    "(in Settings - APP) ")
                            .setPositiveButton("OK", null)
                            .show();
                    locationSwitch.setChecked(false);
                }
            } else {
                if (userDB != null) {
                    userDB.setAcBluetooth(false);
                    userDB.save();
                }
            }
        }
    }

    private class CalendarReminderListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                System.out.println("begin");
                if (checkSelfPermission(SET_ALARM) == PackageManager.PERMISSION_GRANTED) {
                    if (userDB != null) {
                        System.out.print(userDB.getUserName());
                        userDB.setRmCalendar(true);
                        userDB.save();
                        System.out.println("name: " + userDB.getUserName());
                        System.out.println("status: " + userDB.getRmCalendar());
                    }
                } else {
                    AlertDialog.Builder window =new AlertDialog.Builder(SettingsActivity.this);
                    window.setTitle("Access to Bluetooth")
                            .setMessage("Please change your settings to authorize Bringo to send notifications." +
                                    "(in Settings - APP) ")
                            .setPositiveButton("OK", null)
                            .show();
                    locationSwitch.setChecked(false);
                }
            } else {
                if (userDB != null) {
                    userDB.setRmCalendar(false);
                    userDB.save();
                }
            }
        }
    }

    private class TravelReminderListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                System.out.println("begin");
                if (checkSelfPermission(SET_ALARM) == PackageManager.PERMISSION_GRANTED) {
                    if (userDB != null) {
                        System.out.print(userDB.getUserName());
                        userDB.setRmTravel(true);
                        userDB.save();
                        System.out.println("name: " + userDB.getUserName());
                        System.out.println("status: " + userDB.getRmTravel());
                    }
                } else {
                    AlertDialog.Builder window =new AlertDialog.Builder(SettingsActivity.this);
                    window.setTitle("Access to Bluetooth")
                            .setMessage("Please change your settings to authorize Bringo to send notifications." +
                                    "(in Settings - APP) ")
                            .setPositiveButton("OK", null)
                            .show();
                    locationSwitch.setChecked(false);
                }
            } else {
                if (userDB != null) {
                    userDB.setRmTravel(false);
                    userDB.save();
                }
            }
        }
    }


}
