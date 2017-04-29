package com.example.bringo;
//TODO: before leave page, disconnect all connection!

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bringo.database.UserDB;
import com.example.bringo.database.trackerDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by alisonwang on 4/24/17.
 */

public class TrackActivity extends AppCompatActivity {

    private static final String SELECTED_BOTTOM_BAR_ITEM = "arg_selected_item";

    private int currentPageID;

    private BottomNavigationView bottomNavigationView;

    private GridView gridView;

    private Toolbar toolbar;

    private List<trackerDB> trackerDBList;

    private BluetoothAdapter bluetoothAdapter;
    private static BroadcastReceiver mReceiver;

    private static HashMap<String, BluetoothSocket> bluetoothSocketHashMap = new HashMap<>();

    //latet consideration about what item type should be more approperiate;
    private static List<trackerDB> connectedtrackerDBList = new ArrayList<>();

    private UserDB userDB = UserDB.listAll(UserDB.class).get(0);

    private boolean forgetReminder;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_main);

        final TrackActivity trackActivity = this;

//1. Set up upper toolbar
        toolbar = (Toolbar) findViewById(R.id.track_toolbar);
        toolbar.setTitle("Tracking");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

//2. Set up bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_track_main);
        //listener for nav item
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("an item is selected");
                //function to change activity
                bottomNavigationItemSelected(item, 2);
                return true;
            }
        });
        //set default NavItem
        setDefaultBottomNavigationItem(savedInstanceState );

//3. get Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//4. check bluetooth permission
        if(userDB.getAcBluetooth() == false){
            // jump to SettingsActivity
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Permission for Bluetooth needed");
            alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        } else {

//5. retrieve data from database
            trackerDBList = trackerDB.listAll(trackerDB.class);

//6. turn on Bluetooth
            if(!bluetoothAdapter.isEnabled())
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }

//7. check whether need forget reminder
            for (int i = 0; i < trackerDBList.size(); i++) {
                if (trackerDBList.get(i).getForgetReminder()) {
                    forgetReminder = true;

                }
            }
            System.out.println("++++++++++++++FORGET REMNIDER =" +forgetReminder+"++++++++++++++++");
//8. set up forget reminder


                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                            String deviceName = "";
                            //TODO: delete the device from connectedtrackerDBList and bluetoothSocketHashMap
                            for (int i = 0; i < connectedtrackerDBList.size(); i++){
                                if(connectedtrackerDBList.get(i).getAddress().equals(device.getAddress())) {
                                    deviceName = connectedtrackerDBList.get(i).getName();
                                    bluetoothSocketHashMap.remove(connectedtrackerDBList.get(i).getAddress());
                                    connectedtrackerDBList.remove(i);
                                    break;
                                }
                            }

                            //repopulate gridview
                            gridView = (GridView) findViewById(R.id.track_itemView);
                            gridView.setAdapter(new trackerGridAdapter(getApplicationContext(), connectedtrackerDBList, bluetoothSocketHashMap));
                            System.out.println("size of connected trackerDB:" + connectedtrackerDBList.size());

                            //Device has disconnected
                            System.out.println("***: ACTION_ACL_DISCONNECTED："+ deviceName);
                            //TODO: make an alert;
                            System.out.println("*********************************ALERT********************************");
                            NotificationReceiver.updateNotification("Reminder","Remember to bring "+ deviceName);
                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => "+c.getTime());
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            int h = Integer.parseInt(formattedDate.substring(11,13));
                            int m = Integer.parseInt(formattedDate.substring(14,16));
                            int s = Integer.parseInt(formattedDate.substring(17,19));
                            System.out.println(h + " " + m + " " + s);
                            //check permission to send alarm
                            if (forgetReminder && userDB.getRmBluetooth()) {
                                setNotificationAlarm(h, m, s, false);
                            }

                        }
                    }
                };


                IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                this.registerReceiver(mReceiver, filter3);



//9. establish connections to not connected devices
            if(trackerDBList.size() > connectedtrackerDBList.size()) {
                for (int i = 0; i < trackerDBList.size(); i++) {
                    String addr = trackerDBList.get(i).getAddress();
                    //check if this device is in the connected tracker DB list
                    boolean contains = false;
                    for (int j = 0; j < connectedtrackerDBList.size(); j++) {
                        if (addr.equals(connectedtrackerDBList.get(j).getAddress())) {
                            contains = true;
                        }
                    }
                    //if it is not contained in the connect tracker DB list, then make the connection
                    if (!contains) {
                        BluetoothSocket btSocket = null;
                        new ConnectBT(btSocket, trackerDBList.get(i)).execute();
                    }
                }
            }
            else if (trackerDBList.size() < connectedtrackerDBList.size()) {

                for (int i = 0; i < connectedtrackerDBList.size(); i++) {
                    String addr = connectedtrackerDBList.get(i).getAddress();
                    boolean contains = false;

                    for (int j = 0; j < trackerDBList.size(); j++) {
                        if (addr.equals(trackerDBList.get(j).getAddress())) {
                            contains = true;
                        }
                    }
                    if(!contains) {
                        connectedtrackerDBList.remove(i);
                        try {
                            bluetoothSocketHashMap.get(addr).close();
                        } catch (IOException e) {
                            System.out.println("A-OH");
                        }
                        bluetoothSocketHashMap.remove(addr);
                    }

                }

            }

//10. populate gridview
            gridView = (GridView) findViewById(R.id.track_itemView);
            gridView.setAdapter(new trackerGridAdapter(getApplicationContext(), connectedtrackerDBList, bluetoothSocketHashMap));
            System.out.println("size of connected trackerDB:" + connectedtrackerDBList.size());

        }

    }

    //for toolbar to have menu
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

    //for bottom navigation bar
    private void bottomNavigationItemSelected (MenuItem item, int current) {

        // update selected item
        currentPageID = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
            System.out.println("uncheck others: "+item.getItemId());
        }

        int a = bottomNavigationView.getMenu().getItem(0).getItemId();
        int b = bottomNavigationView.getMenu().getItem(1).getItemId();
        int c = bottomNavigationView.getMenu().getItem(2).getItemId();
        int d = bottomNavigationView.getMenu().getItem(3).getItemId();
        //change activity here
        if(currentPageID == a && current != 0){
            System.out.println("jump to HOME");
            Intent intent0 = new Intent(this, HomeActivity.class);
            startActivity(intent0);
        }
        else if (currentPageID == b && current != 1){
            System.out.println("jump to TODAY'S LIST");
            //TODO: *********change to today's list**************
            Intent intent1 = new Intent(this, HomeActivity.class);
            startActivity(intent1);
        }
        else if (currentPageID == c && current != 2){
            System.out.println("jump to Tracking");
            Intent intent2 = new Intent(this, TrackActivity.class);
            startActivity(intent2);
        }
        else if (currentPageID == d && current != 3){
            System.out.println("jump to SETTINGS");
            Intent intent3 = new Intent(this, SettingsActivity.class);
            startActivity(intent3);
        }
    }

    //set default navigation item
    private void setDefaultBottomNavigationItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            currentPageID = savedInstanceState.getInt(SELECTED_BOTTOM_BAR_ITEM, 2);
            selectedItem = bottomNavigationView.getMenu().findItem(currentPageID);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = bottomNavigationView.getMenu().getItem(2);
            System.out.println("Current is null, so force to be 2");
        }
        // update selected item
        currentPageID = selectedItem.getItemId();

        // uncheck the other items.
        for (int i = 0; i< bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == selectedItem.getItemId());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState()");
        outState.putInt(SELECTED_BOTTOM_BAR_ITEM, currentPageID);
        super.onSaveInstanceState(outState);
    }


    // Gridview adapter
    public class trackerGridAdapter extends BaseAdapter {
        private List<trackerDB> trackerDBListcon;
        private HashMap<String,BluetoothSocket> bluetoothSocketHashMap;
        private Context context;

        public trackerGridAdapter(Context context, List<trackerDB> a, HashMap<String,BluetoothSocket> b){
            this.context = context;
            this.trackerDBListcon = a;
            this.bluetoothSocketHashMap = b;
        }

        @Override
        public int getCount() {
            return trackerDBListcon.size()+1;
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
            if(position < getCount() -1 ){
                Button itemButton;

                //set the title, color and size of the button
                if(convertView == null){
                    //System.out.println("convertView == null, get View for position: "+ position);
                    itemButton = new Button(context);
                    itemButton.setLayoutParams(new GridView.LayoutParams(1200,280));
                    itemButton.setPadding(8,8,8,8);
                    itemButton.setTextColor(Color.BLACK);
                }
                else{
                    //System.out.println("convertView != null, position: "+position);
                    itemButton = (Button) convertView;
                }
                itemButton.setText(trackerDBList.get(position).getName());
                System.out.println("                                                                "+trackerDBList.get(position).getName());
                System.out.println();
                itemButton.setTextSize(20);
                itemButton.setBackgroundColor(Color.LTGRAY);
                itemButton.setId(position);

                //set onclick listener
                itemButton.setOnClickListener(new trackerOnClickListener(context, bluetoothSocketHashMap.get(trackerDBListcon.get(position).getAddress())));
                return itemButton;

            }
            // button for adding
            else {
                Button itemButton;

                //set the titile, color and size of the button
                if(convertView == null){
                    //System.out.println("convertView == null, get View for position: add");
                    itemButton = new Button(context);
                    itemButton.setLayoutParams(new GridView.LayoutParams(1200,280));
                    itemButton.setPadding(8,8,8,8);
                    itemButton.setTextColor(Color.BLACK);
                }
                else{
                    //System.out.println("convertView != null, position: add");
                    itemButton = (Button) convertView;
                }
                itemButton.setText("＋");
                itemButton.setTextSize(40);
                itemButton.setBackgroundColor(Color.LTGRAY);
                itemButton.setId(position);

                //set onclick listener
                itemButton.setOnClickListener(new addTrackerOnClickListener(context));
                return itemButton;
            }
        }
    }

    //class for onclick listener of item grid button
    private class trackerOnClickListener implements View.OnClickListener{
        boolean on = false;
        Context context;
        BluetoothSocket btSocket;
        public trackerOnClickListener(Context context,BluetoothSocket btSocket){
            this.context = context;
            this.btSocket = btSocket;
        }
        @Override
        public void onClick(View v) {
            if(!on){
                v.setBackgroundColor(Color.BLUE);
                System.out.println("clicked, from off to on");
                try
                {
                    btSocket.getOutputStream().write("TO".getBytes());
                    on = true;
                    System.out.println("TO");
                }
                catch (IOException e)
                {
                    Toast.makeText(getApplicationContext(), "Not connected",Toast.LENGTH_LONG).show();
                    on = true;
                }

            }
            else {
                v.setBackgroundColor(Color.LTGRAY);
                System.out.println("clicked, from on to off");
                try
                {
                    btSocket.getOutputStream().write("TF".getBytes());
                    on = false;
                    System.out.println("TF");
                }
                catch (IOException e)
                {
                    Toast.makeText(getApplicationContext(), "Not connected",Toast.LENGTH_LONG).show();
                    on = false;
                }

            }
        }
    }

    //class for onclick listener of add grid button
    private class addTrackerOnClickListener implements View.OnClickListener{
        Context context;

        public addTrackerOnClickListener(Context context){
            this.context = context;
        }
        @Override
        public void onClick(View v) {

            System.out.println("add clicked");
            //jump to add activity
            Intent intent1 = new Intent(context, TrackAddActivity.class);
            startActivity(intent1);
        }

    }

    //class to make bluetooth connection via bluetooth socket
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        private BluetoothSocket bluetoothSocket;

        String address;

        trackerDB device;

        public  ConnectBT(BluetoothSocket bluetoothSocket,trackerDB device){
            this.bluetoothSocket = bluetoothSocket;
            this.address = device.getAddress();
            this.device = device;
        }

        @Override
        protected void onPreExecute()
        {
            System.out.println("Connecting to "+ address);
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            //make the connection in background
            try
            {
                //if the bluetooth socket is null and it is not connected!
                if (bluetoothSocket == null )
                {
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    //changed it to SSP connection
                    bluetoothSocket = dispositivo.createRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    //once is connected, then cancelDiscovery
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    //start connection
                    bluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (ConnectSuccess)
            {
                connectedtrackerDBList.add(device);
                bluetoothSocketHashMap.put(address,bluetoothSocket);


                System.out.println("connected to "+ address);
                System.out.println("number of socket:" + bluetoothSocketHashMap.size());
            }
            else {
                System.out.println("failed to connect to "+ address);
                System.out.println("number of socket:" + bluetoothSocketHashMap.size());
            }

            //11. populate gridview
            gridView = (GridView) findViewById(R.id.track_itemView);
            gridView.setAdapter(new trackerGridAdapter(getApplicationContext(), connectedtrackerDBList, bluetoothSocketHashMap));
            System.out.println("size of connected trackerDB:" + connectedtrackerDBList.size());
        }
    }


    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d("TAG", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    protected void onPause(){

        super.onPause();

        System.out.println("-----------------------ON PAUSE---------------------");
         /*
        //disconnect all socket
        onLeave = true;
        for(int i = 0; i < pairedDBcrossList.size(); i++){
            try {
                pairedDBcrossList.get(i).getSocket().close();
                System.out.println("Disconnect "+ pairedDBcrossList.get(i).getName());
            } catch (Exception e) {
                System.out.println("Fail to Disconnect "+ pairedDBcrossList.get(i).getName());
            }
        }
        onCreate = false;
        */
        System.out.println("-----------------------COMPLETE ON PAUSE---------------------");


    }

    public void setNotificationAlarm(int hour,int minute,int second, boolean repeat){
        if (forgetReminder && userDB.getRmBluetooth()) {
            // the following code is just for notification test!!!
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, second);

            // NotificationReceiver is a BroadcastReceiver class
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            // Alarm Service requires a PendingIntent as param, set the intent to the pendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 101,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            // set an alarm that works even if app is cosed, depends on calendar time,
            // repeats everyday, with pendingIntent
            // So when alarm goes off NotificationReceiver will be triggered
            if (repeat == true) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            // cancel the alarm
            //alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("------------------RESUME-----------------------");
        /*
        if(!onCreate) {
            System.out.println("-----------------not from onCreate--------------------");
            connectedtrackerDBList = new ArrayList<>();
            bluetoothSocketList = new ArrayList<>();
            for (int i = 0; i < pairedDBcrossList.size(); i++) {

                    BluetoothSocket btSocket = null;
                    new ConnectBT(btSocket, pairedDBcrossList.get(i)).execute();
                    //bluetoothSocketList, connectedDeviceAddress, connectedtrackerDBList are populated in the async class

            }
            gridView = (GridView) findViewById(R.id.track_itemView);
            gridView.setAdapter(new trackerGridAdapter(getApplicationContext(), connectedtrackerDBList, bluetoothSocketList));
            System.out.println("size of connected trackerDB:" + connectedtrackerDBList.size());
        }
        */
        System.out.println("------------------FINISH RESUME-----------------------");

    }


}
