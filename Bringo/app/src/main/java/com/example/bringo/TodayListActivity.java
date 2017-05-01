package com.example.bringo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bringo.database.CheckedItemsDB;
import com.example.bringo.database.TodayListDB;
import com.example.bringo.supportingapis.WeatherAPI;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TodayListActivity extends AppCompatActivity implements LocationListener{
    private Toolbar myToolbar;
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";
    private ArrayList<String> todayList = new ArrayList<>();
    private ArrayList<String> weatherItemsList = new ArrayList<>();
    private Location currentLocation;
    private WeatherAPI weatherGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_list);

        //set up toolbar
        myToolbar = (Toolbar)findViewById(R.id.todayList_toolbar);
        myToolbar.setTitle("Bringo for Today");
        setSupportActionBar(myToolbar);

        // set up bottom bar
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_todayList);
        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //function to change activity
                navItemSelected(item, 1);
                return true;
            }
        });
        //set default NavItem
        setDefaultNavItem(savedInstanceState );

        //set up text of today's date
        // get today's date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String todayDate = formatter.format(calendar.getTime());
        TextView textView = (TextView) findViewById(R.id.todayList_date);
        textView.setText(todayDate);

        // load items from TodayListDB
        loadTodayListDB(todayDate);

        // load weathr items
        loadWeatherItems();

    }

   public void parseWeatherStr(String weather){
       System.out.println("parse weatehr str: "+weather);

       // set weather info in text
       TextView textView = (TextView) findViewById(R.id.todayList_date);
       String date = textView.getText().toString();
       textView.setText(date+" "+weather);
       itemsReady();

       // add relevant items to weatherItemsList
       String weatherParse = weather.toLowerCase();
       if(weatherParse.contains("thunderstorm") || weatherParse.contains("rain") || weatherParse.contains("shower")){
           weatherItemsList.add("umbrella");
           weatherItemsList.add("rain coats");
           weatherItemsList.add("rain boots");
       }
       if(weatherParse.contains("snow") || weatherParse.contains("freezing") || weatherParse.contains("cold")){
           weatherItemsList.add("snow boots");
           weatherItemsList.add("scarf");
           weatherItemsList.add("hat");
           weatherItemsList.add("gloves");
       }
       if(weatherParse.contains("foggy") || weatherParse.contains("dust") || weatherParse.contains("smoky")){
           weatherItemsList.add("gauze mask");
       }
       if(weatherParse.contains("blustery") || weatherParse.contains("windy") || weatherParse.contains("cloudy")){
           weatherItemsList.add("scarf");
           weatherItemsList.add("coats");
       }
       if(weatherParse.contains("sun") || weatherParse.contains("hot")){
           weatherItemsList.add("sunglasses");
           weatherItemsList.add("sun cream");
       }

       Set<String> weatherItemsSet = new HashSet<>();
       weatherItemsSet.addAll(weatherItemsList);
       weatherItemsList.clear();;
       weatherItemsList.addAll(weatherItemsSet);
   }


    private void itemsReady(){
        // set up weather items list
        ListView weatherListView = (ListView) findViewById(R.id.today_weather_List);
        weatherListView.setBackgroundColor(Color.YELLOW);
        Collections.sort(weatherItemsList);
        final ArrayAdapter<String> weatherListAdapter = new ArrayAdapter<String>(this,R.layout.mylist,weatherItemsList);
        weatherListView.setAdapter(weatherListAdapter);

        // set up list view of today's list
        ListView listView = (ListView) findViewById(R.id.todayList_List);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        Collections.sort(todayList);
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text,todayList);
        listView.setAdapter(listAdapter);
    }



    private void loadWeatherItems(){
        weatherGetter = new WeatherAPI(TodayListActivity.this);
        getCurrentLocation();
        if (currentLocation != null) {
//                        System.out.println("lat: " + currentLocation.getLatitude());
//                        System.out.println("lng: " + currentLocation.getLongitude());
            weatherGetter.refreshWeather(currentLocation);
        }
    }

    private void loadTodayListDB(String todayDate){
        System.out.println(TodayListDB.count(TodayListDB.class));
        List<TodayListDB> todayListDBs = TodayListDB.listAll(TodayListDB.class);
        if(todayListDBs.size()!=0){
            // when today's list is not empty
            System.out.println("size is "+todayListDBs.size());
            System.out.println(todayListDBs.get(0).getDate());
            if(todayDate.equals(todayListDBs.get(0).getDate())){
                // load items to todayList when the date in DB = today's date
                for(TodayListDB todayListDB : todayListDBs){
                    todayList.add(todayListDB.getItemName());
                }
            }
        }
    }




    private void navItemSelected (MenuItem item, int current) {

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
            System.out.println("uncheck others: "+item.getItemId());
        }

        int a = mBottomNav.getMenu().getItem(0).getItemId();
        int b = mBottomNav.getMenu().getItem(1).getItemId();
        int c = mBottomNav.getMenu().getItem(2).getItemId();
        int d = mBottomNav.getMenu().getItem(3).getItemId();
        //change activity here
        if(mSelectedItem == a && current != 0){
            System.out.println("jump to HOME");
            Intent intent0 = new Intent(this, HomeActivity.class);
            startActivity(intent0);
        }
        else if (mSelectedItem == b && current != 1){
            System.out.println("jump to TODAY'S LIST");
            //TODO: *********change to today's list**************
            Intent intent1 = new Intent(this, TodayListActivity.class);
            startActivity(intent1);
        }
        else if (mSelectedItem == c && current != 2){
            System.out.println("jump to Tracking");
            Intent intent2 = new Intent(this, TrackActivity.class);
            startActivity(intent2);
        }
        else if (mSelectedItem == d && current != 3){
            System.out.println("jump to SETTINGS");
            Intent intent3 = new Intent(this, SettingsActivity.class);
            startActivity(intent3);
        }
    }

    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 1);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = mBottomNav.getMenu().getItem(1);
            System.out.println("Current is null, so force to be 0");
        }
        // update selected item
        mSelectedItem = selectedItem.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == selectedItem.getItemId());
        }
    }

    private Location getCurrentLocation() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        System.out.println("GPS"+isGPSEnabled);
        /*if(!isGPSEnabled) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
        */
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

    @Override
    public void onLocationChanged(Location location) {

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
}
