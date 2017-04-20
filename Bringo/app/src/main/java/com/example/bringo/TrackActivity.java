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

    private List<trackerDB> recordsDBList;

    private List<String> itemNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_main);

        final TrackActivity ta = this;

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
                navItemSelected(item, 2);
                return true;
            }
        });

        //set default NavItem
        setDefaultNavItem(savedInstanceState );
        

/*
        //initialize database if not initialized
        SharedPreferences pref = AppSharedPreferences.getSharedPreferences(this);
        String restoredText = pref.getString("trackerDB", null);
        if (restoredText == null) {


            //initialize database
            System.out.println("CREATE NEW TRACKER RECORDS DATABASE!!!!!!");
            trackerDB trackerRecords = new trackerDB(1,"key","1234", false);
            trackerRecords.save();


            //set shared preference
            SharedPreferences.Editor editor = AppSharedPreferences.editor(this);
            editor.putString("trackerDB", "created");
            editor.commit();
        }
        else {
            System.out.println("database already exists, no need to create entry");
        }

*/

        //delete testing records from database
        trackerDB.deleteAll(trackerDB.class);


        //retrieve data from database

        recordsDBList = trackerDB.listAll(trackerDB.class);
        System.out.println("database size: "+recordsDBList.size());
        itemNames = new ArrayList<>();

        for(trackerDB entry : recordsDBList){
            itemNames.add(entry.getName());
        }

        //populate gridview using data from database
        itemView = (GridView)findViewById(R.id.track_itemView);
        itemView.setAdapter(new trackerGridAdapter(this, itemNames));


    }

    public class trackerGridAdapter extends BaseAdapter {
        private List<String> names;
        private Context context;

        public trackerGridAdapter(Context context, List<String> names){
            this.context = context;
            this.names = names;
        }

        @Override
        public int getCount() {
            return names.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // add an add button image to position 0
            if(position < getCount() -1 ){
                Button itemButton;
                if(convertView == null){
                    System.out.println("convertView == null, get View for position: "+ position);
                    itemButton = new Button(context);
                    itemButton.setLayoutParams(new GridView.LayoutParams(900,280));
                    itemButton.setPadding(8,8,8,8);
                }
                else{
                    System.out.println("convertView != null, position: "+position);
                    itemButton = (Button) convertView;
                }
                itemButton.setText(names.get(position));
                itemButton.setTextSize(20);
                itemButton.setBackgroundColor(Color.LTGRAY);
                itemButton.setId(position);

                int trackerID = recordsDBList.get(position).getItemID();
                //add onClickListener
                itemButton.setOnClickListener(new trackerOnClickListener(trackerID));
                return itemButton;

            }
            // add scenario names to other positions
            else {

                Button itemButton;
                if(convertView == null){
                    System.out.println("convertView == null, get View for position: add");
                    itemButton = new Button(context);
                    itemButton.setLayoutParams(new GridView.LayoutParams(900,280));
                    itemButton.setPadding(8,8,8,8);
                }
                else{
                    System.out.println("convertView != null, position: add");
                    itemButton = (Button) convertView;
                }
                itemButton.setText("＋");
                itemButton.setTextSize(40);
                itemButton.setBackgroundColor(Color.LTGRAY);
                itemButton.setId(position);
                //add onClickListener
                itemButton.setOnClickListener(new addTrackerOnClickListener());
                return itemButton;
            }
        }
    }


    //listener for tracker
    private class trackerOnClickListener implements View.OnClickListener{
        int ID;
        public trackerOnClickListener(int sID){
            this.ID = sID;
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            v.setBackgroundColor(Color.BLUE);
            System.out.println(ID+ "clicked");
            //TODO: check BT permission and send signal to Arduino 
        }
    }

    private class addTrackerOnClickListener implements View.OnClickListener{

        public addTrackerOnClickListener(){

        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            v.setBackgroundColor(Color.BLUE);
            System.out.println("add clicked");
            // Just for testing
            trackerDB trackerRecord = new trackerDB(1994,"key","1234", false);
            trackerRecord.save();
            System.out.println("entry 1 saved");
            recordsDBList.add(trackerRecord);

            itemNames.add(trackerRecord.getName());

            itemView.setAdapter(new trackerGridAdapter(getApplicationContext(), itemNames));
            //TODO: change to add activity

        }

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
            System.out.println("Edit clicked, about to change activity");
            Intent intent = new Intent(getApplicationContext(), TrackEdit1Activity.class);
            startActivity(intent);
            return true;
        }
    };



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
            Intent intent1 = new Intent(this, HomeActivity.class);
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
}
