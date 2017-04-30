package com.example.bringo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bringo.database.CheckedItemsDB;
import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.InputItemID;
import com.example.bringo.database.InputSceID;
import com.example.bringo.database.TodayListDB;
import com.example.bringo.database.UserInputItemsDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CreateSceTwoActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private ArrayList<Integer> checkedScenarios = new ArrayList<>();
    private Hashtable<String,CreateSceTwoHashClass> stepTwoItems = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sce_two);

        //set up toolbar
        myToolbar = (Toolbar)findViewById(R.id.createlist2_toolbar);
        myToolbar.setTitle("New List (2/2)");
        setSupportActionBar(myToolbar);

        // get checkedScenarios list from the previous activity
        checkedScenarios = getIntent().getIntegerArrayListExtra("checkedScenarios");

        if(checkedScenarios.size()==0){
            getOtherItems();
        }
        else {
            for (int i : checkedScenarios) {
                System.out.println("enter for loop");
                // set checked scenario name
                List<DefaultScenarios> dss = DefaultScenarios.find(DefaultScenarios.class, "scenario_id = ?", String.valueOf(i));
                DefaultScenarios ds = dss.get(0);
                String sceName = ds.getName();
                String idStr = "s" + i + "_name";
                int textViewID = getResources().getIdentifier(idStr, "id", getPackageName());
                TextView textView = (TextView) findViewById(textViewID);
                textView.setText(sceName);
                textView.setVisibility(View.VISIBLE);

                new AsyncLoadDefaultItems().execute(String.valueOf(i));
            }

        }

        // set up "SAVE NEW SCENARIO" button
        Button saveNewSceBtn = (Button) findViewById(R.id.save_new_sce_btn);
        saveNewSceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // the following code is just fot testing
                int count = 0;
                Set<String> checkedItemNmes = stepTwoItems.keySet();
                for(String itemName:checkedItemNmes){
                    CreateSceTwoHashClass hc = stepTwoItems.get(itemName);
                    if(hc.getCheckedStatus()==true){
                        count++;
                        System.out.println("count "+count+":"+itemName+" "+hc.getItemID()+" "+hc.getCheckedStatus());
                    }
                }
                System.out.println("Now hashTable has:"+count);

                // set up AlertDialog for "SAVE NEW SCENARIO" button
                View view = LayoutInflater.from(CreateSceTwoActivity.this).inflate(R.layout.activity_save_new_sce_alert,null);
                final EditText inputSceName = (EditText) view.findViewById(R.id.save_alert_edittext);


                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSceTwoActivity.this);
                builder.setMessage("Edit List Name:").setView(view)
                        .setNegativeButton("Cancel",null)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // set action when user clicks "OK" on the alert dialog
                                System.out.println("OK clicked");

                                // get user input scenario name and save it to CustomizedSceDB
                                String sceName = inputSceName.getText().toString();
                                int sceID = getInputSceAddID();
                                CustomizedSceDB customizedSce = new CustomizedSceDB(sceID,sceName);
                                customizedSce.save();
                                // load checked items in hash table into CheckedItemsDB
                                loadStepTwoItems(sceID);
                                // jump to Home Activity
                                Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();



            }
        });

        // set up "JUST FOR TODAY" button's onClickListener
        Button todayBtn = (Button) findViewById(R.id.just_for_today_btn);
        todayBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // save all checked items into TodayListDB
                saveTodayList();

                // jump to today's list activity
                Intent intent = new Intent(CreateSceTwoActivity.this,TodayListActivity.class);
                startActivity(intent);
            }
        });

        // set up "+" button's onClickListener
        Button addBtn = (Button) findViewById(R.id.c2_add_item_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user input item name
                String inputItem = ((EditText)findViewById(R.id.c2_input_item)).getText().toString();
                EditText editText = (EditText)findViewById(R.id.c2_input_item);
                editText.setText("");
                ListView lv = (ListView)findViewById(R.id.other_List);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>)lv.getAdapter();
                adapter.add(inputItem);

                int inputItemPos = adapter.getPosition(inputItem);
                lv.setItemChecked(inputItemPos,true);
                setListViewHeightBasedOnChildren(lv);
                // save user input item into UserInputDB
                int inputItemID = getInputItemAddID();
                UserInputItemsDB inputItemsDB = new UserInputItemsDB(inputItemID,inputItem);
                inputItemsDB.save();

                // update hash table
                CreateSceTwoHashClass hval = new CreateSceTwoHashClass(inputItemID,true);
                if(!stepTwoItems.containsKey(inputItem)){
                    stepTwoItems.put(inputItem,hval);
                }
            }
        });




        // the following code is just for testing
        /*
        String[] test = {"a","b","c"};
        System.out.println("here");
        ListView ls = (ListView) findViewById(R.id.s1_List);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateSceTwoActivity.this,R.layout.mylist,test);
        ls.setAdapter(adapter);

        ListView ls3 = (ListView) findViewById(R.id.s3_List);
        ls3.setAdapter(adapter);*/
    }

    private class AsyncLoadDefaultItems extends AsyncTask<String,Void,ArrayList<String>> {
        int lastSceFlaf = -1;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            // load selected scenarios checked items from CheckedItemsDB to the hashtable stepTwoItems
            lastSceFlaf = Integer.valueOf(params[0]);
            ArrayList<String> oneScenarioitems = loadDefaultItems(params[0]);
            return oneScenarioitems;
        }
        @Override
        protected void onPostExecute(ArrayList<String> result){
            selectedSceReady(result);
            //System.out.println("finish load hashtable size is:"+stepTwoItems.size());
            //System.out.println(result.toString());

            if(lastSceFlaf == checkedScenarios.get(checkedScenarios.size()-1)) {
                // after loading dselected default scenarios, show other items from ID=All HTTP request
                getOtherItems();
            }
        }

        private ArrayList<String> loadDefaultItems(String sID){
            ArrayList<String> oneScenarioitems = new ArrayList<>();
            oneScenarioitems.add(String.valueOf(sID));
            List<CheckedItemsDB> items = CheckedItemsDB.find(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(sID));
            for(CheckedItemsDB item:items){
                int itemID = item.getItemID();
                if(itemID<=1000){
                    // get item name from the server
                    String responseStr = doGet(itemID);
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        String itemName = jsonObject.getString(String.valueOf(itemID));
                        CreateSceTwoHashClass hashVal = new CreateSceTwoHashClass(itemID,true);
                        stepTwoItems.put(itemName,hashVal);
                        oneScenarioitems.add(itemName);
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    // get item name from UserInputItemsDB
                    List<UserInputItemsDB> inputitems = UserInputItemsDB.find(UserInputItemsDB.class,"item_id = ?",String.valueOf(itemID));
                    String itemName = inputitems.get(0).getItemName();
                    CreateSceTwoHashClass hashVal = new CreateSceTwoHashClass(itemID,true);
                    stepTwoItems.put(itemName,hashVal);
                    oneScenarioitems.add(itemName);
                }
            }
            return oneScenarioitems;
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

    private void selectedSceReady(ArrayList<String> oneSceItems){
        // get the ID of the loaded ready scenario
        int sID = Integer.parseInt(oneSceItems.get(0));
        oneSceItems.remove(0);

        // get the scenario's list view
        String listIDStr = "s"+sID+"_List";
        int listID = getResources().getIdentifier(listIDStr,"id",getPackageName());
        ListView listView = (ListView) findViewById(listID);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        // set up listview's adapter
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text,oneSceItems);
        listView.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(listView);
        for(int pos = 0;pos<listAdapter.getCount();pos++){
            listView.setItemChecked(pos,true);

        }

        // set up OnItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = listAdapter.getItem(position);
                CreateSceTwoHashClass hashVal = stepTwoItems.get(clickedItem);
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

    /**
     * method sets the list view within the scroll view not scroll
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void getOtherItems(){
        new AsyncGetAllItems().execute();
    }

    private class AsyncGetAllItems extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String responseStr = doGetAll();
            // other items requires by ID=all request, duplicates has been removed
            ArrayList<String> otherItems = parseAllItemsJson(responseStr);
            return otherItems;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            otherItemsReady(result);
        }


        private String doGetAll(){
            int status;
            StringBuilder responseSB = new StringBuilder("");;
            HttpsURLConnection conn;
            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getItemListByID?ID=all");
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

        private ArrayList<String> parseAllItemsJson(String responseStr){
            ArrayList<String> otherItems = new ArrayList<>();
            try{
                JSONObject responseJson  = new JSONObject(responseStr);
                Iterator<String> keyIter = responseJson.keys();
                while(keyIter.hasNext()){
                    String itemID = keyIter.next();
                    int itemIDint = Integer.valueOf(itemID);
                    String itemName = responseJson.getString(itemID);
                    if(!stepTwoItems.containsKey(itemName)){
                        otherItems.add(itemName);
                        CreateSceTwoHashClass hashVal = new CreateSceTwoHashClass(itemIDint,false);
                        stepTwoItems.put(itemName,hashVal);
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return otherItems;
        }

    }

    private void otherItemsReady(ArrayList<String> otherItems){
        // set up list view for other items list
        ListView listView = (ListView) findViewById(R.id.other_List);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // set up the adapter of the list view
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text,otherItems);
        listView.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(listView);

        // set up OnItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = listAdapter.getItem(position);
                CreateSceTwoHashClass hashVal = stepTwoItems.get(clickedItem);
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

    /*
 * reutrns an sceID for newly entered user input items
 */
    private int getInputSceAddID(){
        int sceID;
        if(InputSceID.count(InputSceID.class)==0){
            sceID = 7;
            InputSceID id = new InputSceID(7);
            id.save();
        }else{
            List<InputSceID> inputSceIDs = InputSceID.listAll(InputSceID.class);
            InputSceID id = inputSceIDs.get(0);
            sceID = id.getSceID() + 1;
            id.setSceID(sceID);
            id.save();
        }
        return sceID;
    }

    private void loadStepTwoItems(int sceID){
        Set<String> checkedItemNmes = stepTwoItems.keySet();
        for(String itemName:checkedItemNmes){
            CreateSceTwoHashClass hc = stepTwoItems.get(itemName);
            if(hc.getCheckedStatus()==true){
                CheckedItemsDB checkedItem = new CheckedItemsDB(hc.getItemID(),sceID);
                checkedItem.save();
            }
        }
        System.out.println(stepTwoItems.size()+" items have been saved to DB");
    }

    /*
     * save all checked items in stepTwoItems hash table
     */
    private void saveTodayList(){
        TodayListDB.deleteAll(TodayListDB.class);
        String date;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        date = formatter.format(calendar.getTime());
        Set<String> checkedItemNames = stepTwoItems.keySet();
        for(String itemName:checkedItemNames){
            CreateSceTwoHashClass hc = stepTwoItems.get(itemName);
            if(hc.getCheckedStatus()==true){
                TodayListDB todayItem = new TodayListDB(date,itemName,false);
                todayItem.save();
            }
        }
    }


}
