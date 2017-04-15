package com.example.bringo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.*;

/**
 * Created by alisonwang on 4/14/17.
 */

public class TrackActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private int mSelectedItem;

    private BottomNavigationView mBottomNav;

    private GridView itemView;

    private Toolbar myToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_main);

        //set up toolbar
        myToolbar = (Toolbar) findViewById(R.id.track_toolbar);
        myToolbar.setTitle("Tracking");
        setSupportActionBar(myToolbar);
        //set up menu listener
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        //set bottom bar selection
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_track_main);

        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("an item is selected");
                //function to change activity
                itemSelected(item, 2);
                return true;
            }
        });

        //set default NavItem
        setDefaultNavItem(savedInstanceState );


        //initialize database if not initialized
        SharedPreferences pref = AppSharedPreferences.getSharedPreferences(this);
        String restoredText = pref.getString("trackerDB", null);
        if (restoredText == null) {
            //initialize database
            System.out.println("CREATE NEW TRACKER RECORDS DATABASE!!!!!!");
            TrackerRecordsDB trackerRecords = new TrackerRecordsDB(2);
            long temp = trackerRecords.save();


            //set shared preference
            SharedPreferences.Editor editor = AppSharedPreferences.editor(this);
            editor.putString("trackerDB", "created");
            editor.commit();
        }


        //retrieve data from database
        List<TrackerRecordsDB> recordsDBList = TrackerRecordsDB.listAll(TrackerRecordsDB.class);
        System.out.println(recordsDBList.size());

    }

    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 2);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = mBottomNav.getMenu().getItem(2);
            System.out.println("Current is null, so force to be 2");
        }
        // update selected item
        mSelectedItem = selectedItem.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == selectedItem.getItemId());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState()");
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_edit, menu);
        return true;
    }

    //listener for "edit"
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            ///////////////////////////////////////////change to jump to other activity
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_track_edit:
                    msg += "Click edit";
                    break;

            }

            if(!msg.equals("")) {
                Toast.makeText(TrackActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            ////////////////////////////////////////////
            return true;
        }
    };



    private void itemSelected (MenuItem item, int current) {

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        int a = mBottomNav.getMenu().getItem(0).getItemId();
        int b = mBottomNav.getMenu().getItem(1).getItemId();
        int c = mBottomNav.getMenu().getItem(2).getItemId();
        int d = mBottomNav.getMenu().getItem(3).getItemId();
        //change activity here
        if(mSelectedItem == a && current != 0){
            System.out.println("jump to HOME");
        }
        else if (mSelectedItem == b && current != 1){
            System.out.println("jump to TODAY'S LIST");
        }
        else if (mSelectedItem == c && current != 2){
            System.out.println("jump to Tracking");
        }
        else if (mSelectedItem == d && current != 3){
            System.out.println("jump to SETTINGS");
        }
    }
}
