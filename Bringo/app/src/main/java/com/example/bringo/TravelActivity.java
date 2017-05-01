package com.example.bringo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bringo.database.CheckedItemsDB;
import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.DestinationDB;
import com.example.bringo.database.InputSceID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huojing on 4/16/17.
 */
public class TravelActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private int mSelectedItem;

    private BottomNavigationView mBottomNav;

    private GridView itemView;

    private Toolbar myToolbar;

    private List<DestinationDB> destinationDBList;

    private List<String> itemNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        final TravelActivity ta = this;

        //set up toolbar
        myToolbar = (Toolbar) findViewById(R.id.travel_toolbar);
        myToolbar.setTitle("Travel");
        setSupportActionBar(myToolbar);

        //set bottom bar selection
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_travel);
        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("an item is selected");
                navItemSelected(item, 0);
                return true;
            }
        });
        setDefaultNavItem(savedInstanceState);

        //delete testing records from database
//        DestinationDB.deleteAll(DestinationDB.class);

        //retrieve data from database
//        DestinationDB.deleteAll(DestinationDB.class);
        destinationDBList = DestinationDB.listAll(DestinationDB.class);
        System.out.println("database size: "+destinationDBList.size());
        itemNames = new ArrayList<>();

        for(DestinationDB entry : destinationDBList){
            itemNames.add(entry.getDestination()+"\n"+entry.getDepartureDate()+" - "+entry.getReturnDate());
        }

        //populate gridview using data from database
        itemView = (GridView)findViewById(R.id.travel_itemView);
        itemView.setAdapter(new travelGridAdapter(this, itemNames));
    }

    public class travelGridAdapter extends BaseAdapter {
        private List<String> names;
        private Context context;

        public travelGridAdapter(Context context, List<String> names){
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
            if(position < getCount() - 1 ){
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

                int destinationID = destinationDBList.get(position).getDesID();
                //add onClickListener
                itemButton.setOnClickListener(new DestinationOnClickListener(destinationID));
//                itemButton.setOnLongClickListener(new DestinationLongClickListener(position));
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
                itemButton.setOnClickListener(new AddDestinationOnClickListener());
                return itemButton;
            }
        }
    }

private class DestinationLongClickListener implements View.OnLongClickListener{
    int dID;
    public DestinationLongClickListener(int position){
        String desName = itemNames.get(position);
        System.out.println("Destination name: "+desName);
        // find its sceID in CustomizedSceDB
        List<DestinationDB> dDBs = DestinationDB.find(DestinationDB.class,"destination=?",desName);
        if (dDBs != null || dDBs.size() != 0) {
            DestinationDB dDB = dDBs.get(0);
            dID = dDB.getDesID();
        }
    }

    @Override
    public boolean onLongClick(View v) {
            /*
            System.out.println("long click scenario");
            Intent intent = new Intent(HomeActivity.this, CreateSceActivity.class);
            startActivity(intent);*/
        System.out.println("long click scenario");
        View view = LayoutInflater.from(TravelActivity.this).inflate(R.layout.activity_delete_sce,null);


        AlertDialog.Builder builder = new AlertDialog.Builder(TravelActivity.this);
        builder.setMessage("Are you sure?").setView(view)
                .setNegativeButton("Cancel",null)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("choose to delete destination");
                        // delete the scenario in CustomizedSceDB
                        List<CustomizedSceDB> sceDBs = CustomizedSceDB.find(CustomizedSceDB.class,"scenario_id = ?",String.valueOf(dID));
                        CustomizedSceDB sceDB = sceDBs.get(0);
                        sceDB.delete();
                        // delete items in CheckedItemsDB with scenarioID = sID
                        CheckedItemsDB.deleteAll(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(dID));
                        // delete the scenario in Home page's grid view
                        Intent intent = new Intent(TravelActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }
}

    /*
     * ScenarioOnclickListener directs user to the item list page of the scenario they select
     */
    private class DestinationOnClickListener implements View.OnClickListener{
        int dID;
        public DestinationOnClickListener(int position){
            dID = position;
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            System.out.println(dID);
            Intent intent = new Intent(TravelActivity.this, ViewDestinationActivity.class);
            TravelActivity.this.startActivity(intent);
        }
    }


    private class AddDestinationOnClickListener implements View.OnClickListener{

        public AddDestinationOnClickListener(){

        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            v.setBackgroundColor(Color.BLUE);
            System.out.println("add clicked");

            DestinationDB destinationRecord = new DestinationDB();
            destinationRecord.save();
            System.out.println("entry 1 saved");

            destinationDBList.add(destinationRecord);
            Intent intent = new Intent(TravelActivity.this, CreateDestination1Activity.class);
            startActivity(intent);

//            itemNames.add(destinationRecord.getDestination());
//            itemView.setAdapter(new travelGridAdapter(getApplicationContext(), itemNames));
        }

    }


    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
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
}
