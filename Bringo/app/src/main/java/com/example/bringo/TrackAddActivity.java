package com.example.bringo;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by alisonwang on 4/25/17.
 */

public class TrackAddActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private Toolbar toolbar;

    private Set<BluetoothDevice> pairedDevices;

    private ArrayList<BluetoothDevice> unpairedDBcrossList = new ArrayList<>();

    private List<trackerDB> trackerDBList;

    private GridView gridView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_add);

//1. set up bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//2. set up upper toolbar
        //1. Set up upper toolbar
        toolbar = (Toolbar) findViewById(R.id.track_toolbar);
        toolbar.setTitle("Add tracker");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

//3. discover unpaired bluetooth
        Discover();

//4. set gridview
        gridView = (GridView) findViewById(R.id.track_itemView);
        gridView.setAdapter(new trackerGridAdapter(this, unpairedDBcrossList));

    }

    public void Discover(){
        System.out.println("Looking for unlisted devices.");
        pairedDevices = bluetoothAdapter.getBondedDevices();
        trackerDBList = trackerDB.listAll(trackerDB.class);

        //check divices that are not in data base and add to unpairedDBcrossList
        for (BluetoothDevice bt: pairedDevices) {
            String addr = bt.getAddress();
            boolean contain = false;
            for (int i = 0; i < trackerDBList.size(); i++) {
                if(addr.equals(trackerDBList.get(i).getAddress())){
                    contain = true;
                }
            }
            if(!contain) {
                unpairedDBcrossList.add(bt);
            }
        }

    }



    public class trackerGridAdapter extends BaseAdapter {
        private List<BluetoothDevice> deviceList;
        private Context context;

        public trackerGridAdapter(Context context, ArrayList<BluetoothDevice> devices){
            this.context = context;
            this.deviceList = devices;
        }

        @Override
        public int getCount() {
            return deviceList.size();
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
            // button for devices
            Button itemButton;

            //set the title, color and size of the button
            if (convertView == null) {
                System.out.println("convertView == null, get View for position: " + position);
                itemButton = new Button(context);
                itemButton.setLayoutParams(new GridView.LayoutParams(1200, 280));
                itemButton.setPadding(8, 8, 8, 8);
                itemButton.setTextColor(Color.BLACK);
            } else {
                System.out.println("convertView != null, position: " + position);
                itemButton = (Button) convertView;
            }
            itemButton.setText(deviceList.get(position).getName() + "\n" + deviceList.get(position).getAddress());
            itemButton.setTextSize(20);
            itemButton.setBackgroundColor(Color.LTGRAY);
            itemButton.setId(position);

            //set onclick listener
            itemButton.setOnClickListener(new deviceOnClickListener(context, deviceList.get(position)));
            return itemButton;
        }
    }

    private class deviceOnClickListener implements View.OnClickListener{
        boolean on = false;
        Context context;
        BluetoothDevice device;
        public deviceOnClickListener(Context context,BluetoothDevice device){
            this.context = context;
            this.device = device;
        }
        @Override
        public void onClick(View v) {
            //pop up window to input new name
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.track_rename_popup, null);


            alertDialogBuilder.setView(view);
            alertDialogBuilder.setTitle("Input Item Name");
            alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    //TODO:
                    TextView inputBox = (EditText)view.findViewById(R.id.newname);
                    String Name = inputBox.getText().toString();
                    trackerDB trackerRecords = new trackerDB(device.getAddress(),Name, device.getName(),false);
                    trackerRecords.save();
                    //JUMP to main activity
                    Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
                    startActivity(intent);
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

    //for toolbar to have menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_back, menu);
        return true;
    }

    //listener for "edit"
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            System.out.println("Edit clicked, about to change activity");
            Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
            startActivity(intent);
            return true;
        }
    };



}
