package com.example.bringo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alisonwang on 4/19/17.
 */

public class TrackEdit3Activity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private int NavSelectedItem;

    private BottomNavigationView BottomNav;

    private GridView itemView;

    private Toolbar myToolbar;

    private List<trackerDB> recordsDBList;

    private List<String> itemNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit_1);

        final TrackEdit3Activity te1a = this;

        //set up toolbar
        myToolbar = (Toolbar) findViewById(R.id.track_toolbar);
        myToolbar.setTitle("Edit:Delete Tracker");
        setSupportActionBar(myToolbar);
        //set up menu listener
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        //set bottom bar selection
        BottomNav = (BottomNavigationView) findViewById(R.id.nav_track_main);

        //listener for nav item
        BottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
            return names.size();
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

                Button itemButton;
                if(convertView == null){
                    System.out.println("convertView == null, get View for position: "+ position);
                    itemButton = new Button(context);
                    itemButton.setLayoutParams(new GridView.LayoutParams(1200,150));
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

                String add = recordsDBList.get(position).getAddress();
                System.out.println("tracker add = "+add);
                //add onClickListener
                itemButton.setOnClickListener(new trackerOnClickListener(add, context, itemButton));
                return itemButton;

        }
    }

    //listener for tracker
    private class trackerOnClickListener implements View.OnClickListener{
        String add;
        Context context;
        Button button;
        public trackerOnClickListener(String add, Context context, Button button){
            this.add = add;
            this.context = context;
            this.button = button;
        }
        @Override
        public void onClick(View v) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


            alertDialogBuilder.setTitle("Delete Tracker");
            alertDialogBuilder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    List<trackerDB> clickedItemList = trackerDB.find(trackerDB.class, "address=?", add);
                    System.out.println("there are "+ clickedItemList.size() + " items in the database with add == "+ add);
                    trackerDB clickedItem = clickedItemList.get(0);
                    String itemName = button.getText().toString();

                    clickedItem.delete();
                    System.out.println("now the new name of the clicked item is" + clickedItem.getName());
                    itemNames.remove(itemName);
                    itemView.setAdapter(new trackerGridAdapter(context, itemNames));

                }
            });
            alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        }
    }


    //listener for "save and back"
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            System.out.println("Back clicked, about to change activity");
            Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
            startActivity(intent);
            return true;
        }
    };


    private void navItemSelected (MenuItem item, int current) {

        // update selected item
        NavSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< BottomNav.getMenu().size(); i++) {
            MenuItem menuItem = BottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
            System.out.println("uncheck others: "+item.getItemId());
        }

        int a = BottomNav.getMenu().getItem(0).getItemId();
        int b = BottomNav.getMenu().getItem(1).getItemId();
        int c = BottomNav.getMenu().getItem(2).getItemId();
        //change activity here
        if(NavSelectedItem == a && current != 0){
            System.out.println("jump to Reminder");
            //TODO: ***********change to reminder activity
            Intent intent = new Intent(this, TrackEdit1Activity.class);
            startActivity(intent);
        }
        else if (NavSelectedItem == b && current != 1){
            System.out.println("jump to Rename");
            //TODO: *********change to rename activity**************
            Intent intent = new Intent(this, TrackEdit2Activity.class);
            startActivity(intent);
        }
        else if (NavSelectedItem == c && current != 2){
            System.out.println("jump to Delete");
            //TODO: *********change to delete activity**************
            Intent intent = new Intent(this, TrackEdit3Activity.class);
            startActivity(intent);
        }
    }

    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            NavSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 2);
            selectedItem = BottomNav.getMenu().findItem(NavSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = BottomNav.getMenu().getItem(2);
            System.out.println("Current is null, so force to be 2");
        }
        // update selected item
        NavSelectedItem = selectedItem.getItemId();

        // uncheck the other items.
        for (int i = 0; i< BottomNav.getMenu().size(); i++) {
            MenuItem menuItem = BottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == selectedItem.getItemId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_back, menu);
        return true;
    }

}
