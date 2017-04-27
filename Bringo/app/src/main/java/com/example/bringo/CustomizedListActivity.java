package com.example.bringo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bringo.database.CheckedItemsDB;
import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.InputItemID;
import com.example.bringo.database.UserInputItemsDB;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CustomizedListActivity extends AppCompatActivity {
    private int sID;
    CustomizedSceDB sceDB;
    private Hashtable<String,CreateSceTwoHashClass> itemsTable = new Hashtable<>();

    private Toolbar myToolbar;
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customized_list);

        // get sID from the previous home activity
        sID = Integer.valueOf(getIntent().getStringExtra("sID"));
        List<CustomizedSceDB> sceDBs = CustomizedSceDB.find(CustomizedSceDB.class,"scenario_id = ?",String.valueOf(sID));
        sceDB = sceDBs.get(0);
        String sceName = sceDB.getName();
        TextView textView = (TextView) findViewById(R.id.cus_scenario_name);
        textView.setText(sceName);

        // set up tool bar
        myToolbar = (Toolbar)findViewById(R.id.customisedList_toolbar);
        myToolbar.setTitle(sceName);
        setSupportActionBar(myToolbar);
        //set up menu listener
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        // set up bottom bar
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_customisedList);
        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //function to change activity
                navItemSelected(item, 6);
                return true;
            }
        });
        //set default NavItem
        setDefaultNavItem(savedInstanceState );

        // get items saved in CheckedItemsDB for this customised scenario
        new AsyncGetItems().execute();

        // set up "+" button's onClickListener
        Button addBtn = (Button) findViewById(R.id.cus_add_item_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user input item name
                String inputItem = ((EditText)findViewById(R.id.cus_input_item)).getText().toString();
                EditText editText = (EditText)findViewById(R.id.cus_input_item);
                editText.setText("");
                ListView lv = (ListView)findViewById(R.id.customisedList_List);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>)lv.getAdapter();
                adapter.add(inputItem);

                int inputItemPos = adapter.getPosition(inputItem);
                lv.setItemChecked(inputItemPos,true);

                // save user input item into UserInputDB
                int inputItemID = getInputItemAddID();
                UserInputItemsDB inputItemsDB = new UserInputItemsDB(inputItemID,inputItem);
                inputItemsDB.save();

                // update hash table
                CreateSceTwoHashClass hval = new CreateSceTwoHashClass(inputItemID,true);
                if(!itemsTable.containsKey(inputItem)){
                    itemsTable.put(inputItem,hval);
                }
            }
        });


    }

    /*
     * reutrns an ItemID for newly entered user input items
     */
    private int getInputItemAddID(){
        int itemID;
        if(InputItemID.count(InputItemID.class)==0){
            itemID = 1001;
            InputItemID id = new InputItemID(1001);
            id.save();
        }else{
            List<InputItemID> inputItemIDs = InputItemID.listAll(InputItemID.class);
            InputItemID id = inputItemIDs.get(0);
            itemID = id.getItemID() + 1;
            id.setItemID(itemID);
            id.save();
        }
        return itemID;
    }

    private class AsyncGetItems extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> scenItems = loadSceItems();
            return scenItems;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            sceItemsReady(result);
        }

        private ArrayList<String> loadSceItems(){
            ArrayList<String> sceItems = new ArrayList<>();
            // load items in CheckedItemsDB with scenarioID = sID
            List<CheckedItemsDB> itemsDBs = CheckedItemsDB.find(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(sID));
            for(CheckedItemsDB itemsDB:itemsDBs){
                int itemID = itemsDB.getItemID();
                if(itemID<=1000){
                    // when item is a default item
                    String responseStr = doGet(itemID);
                    try{
                        JSONObject jsonObject = new JSONObject(responseStr);
                        String itemName = jsonObject.getString(String.valueOf(itemID));
                        CreateSceTwoHashClass hval = new CreateSceTwoHashClass(itemID,true);
                        itemsTable.put(itemName,hval);
                        sceItems.add(itemName);
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    // get item name from UserInputItemsDB
                    List<UserInputItemsDB> inputitems = UserInputItemsDB.find(UserInputItemsDB.class, "item_id = ?", String.valueOf(itemID));
                    String itemName = inputitems.get(0).getItemName();
                    CreateSceTwoHashClass hval = new CreateSceTwoHashClass(itemID,true);
                    itemsTable.put(itemName,hval);
                    sceItems.add(itemName);
                }
            }
            return sceItems;
        }

        private String doGet(int itemID){
            int status;
            StringBuilder responseSB = new StringBuilder("");;
            HttpsURLConnection conn;

            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getItemNameByID?ID="+itemID);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // read response
                status = conn.getResponseCode();
                if(status != 200){
                    return "wrong";
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseLine = "";
                while((responseLine = br.readLine())!=null){
                    responseSB.append(responseLine);
                }

                // close GET request
                conn.disconnect();
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            // return the response as a String
            return responseSB.toString();
        }
    }

    private void sceItemsReady(ArrayList<String> sceItems){
        // get list view of the page
        System.out.println("sceitems size is "+sceItems.size());
        ListView listView = (ListView) findViewById(R.id.customisedList_List);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // set up listview's adpter
        Collections.sort(sceItems);
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text,sceItems);
        listView.setAdapter(listAdapter);
        for(int pos = 0;pos<listAdapter.getCount();pos++){
            // check all loaded items on the list view
            listView.setItemChecked(pos,true);

        }

        // set up OnItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = listAdapter.getItem(position);
                CreateSceTwoHashClass hashVal = itemsTable.get(clickedItem);
                if(hashVal.getCheckedStatus()==true){
                    // when the item on the list is previously checked
                    hashVal.setCheckedStatus(false);
                }else{
                    // when the item on the list is previously unchecked
                    hashVal.setCheckedStatus(true);
                }
            }
        });

    }

    //listener for "save"
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            System.out.println("save button clicked and now hashtable has "+itemsTable.size()+" checked items");

            // save checked items in itemsTable into CheckedItemsDB
            CheckedItemsDB.deleteAll(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(sID));
            Set<String> itemNames = itemsTable.keySet();
            for(String itemName:itemNames){
                CreateSceTwoHashClass hv = itemsTable.get(itemName);
                if(hv.getCheckedStatus()==true){
                    // save checked items into checkedItemsDB
                    CheckedItemsDB checkedItemsDB = new CheckedItemsDB(hv.getItemID(),sID);
                    checkedItemsDB.save();
                }
            }

            // after click save button, jump to Home page
            Intent intent = new Intent(CustomizedListActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_save, menu);
        return true;
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

    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
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
}
