package com.example.bringo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.bringo.database.InputItemID;
import com.example.bringo.database.UserInputItemsDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DefaultListActivity extends AppCompatActivity {
    private int sID;                    // sID of the current default scenario
    private int defaultItemsSize;       // size of the default items list (not include user inputs)
    private int firstItemID;
    // all items to be shown on the list view (defaultItems + userInputItems)
    private ArrayList<String> itemsList = new ArrayList<String>();
    // items previously checked and stored in CheckeditemsDB
    // Integer represents checked items' position on the list view
    private ArrayList<Integer> checkedItems  = new ArrayList<Integer>();
    private ArrayList<String> essentials = new ArrayList<String>();
    private static final String SELECTED_ITEM = "arg_selected_item";
    private Toolbar myToolbar;
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_list);

        // get sID passed from HomeActivity
        sID = Integer.valueOf(getIntent().getStringExtra("sID"));
        // get scenario name from DefaultScenarios DB
        List<DefaultScenarios> dss = DefaultScenarios.find(DefaultScenarios.class,"scenario_id = ?",String.valueOf(sID));
        DefaultScenarios ds = dss.get(0);
        String sceName = ds.getName();

        //set up toolbar
        myToolbar = (Toolbar)findViewById(R.id.defaultList_toolbar);
        myToolbar.setTitle(sceName);
        setSupportActionBar(myToolbar);
        //set up menu listener
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);


        // set up bottom bar
        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_defaultList);
        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //function to change activity
                navItemSelected(item, 5);
                return true;
            }
        });

        //set default NavItem
        setDefaultNavItem(savedInstanceState );

        //set text view and list view of essentials
        if(sID!=1){
            // set up text view
            TextView essentialView = (TextView)findViewById(R.id.essential_name);
            essentialView.setText("Essentials");
            // set up list view
            //ListView essentialsList = (ListView) findViewById(R.id.essentialList_List);
            //List<CheckedItemsDB> listDB = CheckedItemsDB.find(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(1));
            new AsyncGetEssentials().execute();

        }


        // set text view of scenario name
        TextView textView = (TextView)findViewById(R.id.scenario_name);
        textView.setText(sceName);

        // set "+" button's onClickListener
        Button addBtn = (Button) findViewById(R.id.add_item_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user input item name
                String inputItem = ((EditText)findViewById(R.id.input_item)).getText().toString();
                EditText editText = (EditText)findViewById(R.id.input_item);
                editText.setText("");
                ListView lv = (ListView)findViewById(R.id.defaultList_List);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>)lv.getAdapter();
                // itemsList is updated
                adapter.add(inputItem);
                System.out.println("After add: " + itemsList.size());
                int inputItemPos = itemsList.indexOf(inputItem);
                lv.setItemChecked(inputItemPos,true);
                checkedItems.add(Integer.valueOf(itemsList.indexOf(inputItem)));
            }
        });

        // following code for this activity
        final DefaultListActivity da = this;
        GetItemList gl = new GetItemList();
        // call getItemList() method in GetItemList class to get all default items for the current scenario
        gl.getItemList(da,sID);
    }


    private class AsyncGetEssentials extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            List<CheckedItemsDB> listDB = CheckedItemsDB.find(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(1));
            for(CheckedItemsDB ci:listDB){
                int ciID = ci.getItemID();
                if(ciID<=1000) {
                    String responseStr = doGet(ciID);
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        essentials.add(jsonObject.getString(String.valueOf(ciID)));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    List<UserInputItemsDB> inputList = UserInputItemsDB.find(UserInputItemsDB.class,"item_id = ?",String.valueOf(ciID));
                    for(UserInputItemsDB ui:inputList){
                        essentials.add(ui.getItemName());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            ListView essentialsList = (ListView) findViewById(R.id.essentialList_List);
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(DefaultListActivity.this,R.layout.mylist,essentials);
            essentialsList.setAdapter(adapter);
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
    /*
     * itemListReady() is called when gets default items from the server
     * @param defaultItems List of Strings of default items
     * @param firstItemID itemID of the first Item in the defaultItems list
     */
    public void itemListReady(ArrayList<String> defaultItems, final int firstItemID){


        // add all items in defaultItems list to itemslist
        this.itemsList = defaultItems;
        // get the total number of default items of this default scenario
        this.defaultItemsSize = defaultItems.size();
        this.firstItemID = firstItemID;

        // set up the item list view
        ListView itemListView = (ListView)findViewById(R.id.defaultList_List);
        itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // load checked items from CheckedItems DB to checkedItems list
        loadCheckedItems(firstItemID,defaultItems);

        // Now itemsList is complete and checkedItems list is ready, set up listview's adapter
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text,this.itemsList);
        itemListView.setAdapter(listAdapter);
        System.out.println("before add: "+itemsList.size());
        // check items based on checkedItems list
        for(int position:checkedItems){
            itemListView.setItemChecked(position,true);
        }   // by now, the initialization of the list view has finished!!!


        // set up ItemOnClickListener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // update checkedItems list
                if(checkedItems.contains(Integer.valueOf(position))){
                    checkedItems.remove(Integer.valueOf(position));
                }else{
                    checkedItems.add(Integer.valueOf(position));
                }
            }
        });


    }

    /*
     * loadCheckedItems() method is called before setting the list adapter
     * It adds userInputItems to itemsList
     * It reads checked items data from CheckedItemsDB and updates checkedItems list
     */
    private void loadCheckedItems(int firstItemID, ArrayList<String> defaultItems){
        int defaultitemsSize = defaultItems.size();

        // get a list of all previously checked items
        List<CheckedItemsDB> checkedItemDBList  = CheckedItemsDB.listAll(CheckedItemsDB.class);
        if(checkedItemDBList.size()>0) {
            for (CheckedItemsDB checkedItem : checkedItemDBList) {
                int itemID = checkedItem.getItemID();
                if (checkedItem.getScenarioID() == sID) {
                    // when the checked item is of the current default scenario
                    if (itemID <= 1000) {
                        // if the checked item is a default item, add its position on the list view to checkedItems
                        checkedItems.add(itemID - firstItemID);
                    } else {
                        // find the instance of UserInputItemsDB which has an itemID = itemID
                        List<UserInputItemsDB> inputItems = UserInputItemsDB.find(UserInputItemsDB.class, "item_id = ?", String.valueOf(itemID));
                        UserInputItemsDB inputItem = inputItems.get(0);
                        // add the userInputItem's Name to itemsList
                        itemsList.add(inputItem.getItemName());
                        // add its position on the list view to checkedItems
                        checkedItems.add(defaultitemsSize);
                        defaultitemsSize++;
                    }

                }
            }
        }
    }

    //listener for "save"
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            ///////////////////////////////////////////change to jump to other activity
            CheckedItemsDB.deleteAll(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(sID));
            for(int position:checkedItems){
                if(position<defaultItemsSize){
                    // when the checked item is a defualt item
                    CheckedItemsDB checkedItem = new CheckedItemsDB(firstItemID+position,sID);
                    checkedItem.save();
                }else{
                    // when the checked item is a user input item
                    String itemName = itemsList.get(position);
                    List<UserInputItemsDB> inputItems = UserInputItemsDB.find(UserInputItemsDB.class,"item_name = ?",itemName);
                    if(inputItems.size()>0){
                        // when the input Item has already been stored in UserInputItemsDB
                        UserInputItemsDB inputItem = inputItems.get(0);
                        CheckedItemsDB checkedItem = new CheckedItemsDB(inputItem.getItemID(),sID);
                        checkedItem.save();
                    }else{
                        // when the input item is first time entered
                        int itemID = getInputItemAddID();
                        UserInputItemsDB inputItem = new UserInputItemsDB(itemID,itemName);
                        inputItem.save();
                        CheckedItemsDB checkedItem = new CheckedItemsDB(inputItem.getItemID(),sID);
                        checkedItem.save();
                    }

                }
            }

            // after click save button, jump to Home page
            Intent intent = new Intent(DefaultListActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
    };

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
