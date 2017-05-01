package com.example.bringo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.bringo.database.DestinationDB;
import com.example.bringo.database.InputItemID;
import com.example.bringo.database.InputSceID;
import com.example.bringo.database.TravelCategoryDB;
import com.example.bringo.database.TravelCheckItemsDB;
import com.example.bringo.database.TravelUserInputDB;
import com.example.bringo.dataretriever.TravelCategoryGetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CreateDestination2Activity extends AppCompatActivity {

    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            // the following code is just fot testing
            int count = 0;
            Set<String> checkedItemNmes = stepTwoItems.keySet();
            for(String itemName:checkedItemNmes){
                CreateDestination2HashClass hc = stepTwoItems.get(itemName);
                if(hc.getCheckedStatus()==true){
                    count++;
                    System.out.println("count "+count+":"+itemName+" "+hc.getItemID()+" "+hc.getCheckedStatus());
                }
            }
            System.out.println("Now hashTable has:"+count);

            Set<String> checkedItemNames = stepTwoItems.keySet();
            List list = DestinationDB.listAll(DestinationDB.class);
            DestinationDB ddb = (DestinationDB) list.get(list.size() - 1);
            int destinationid = getDestinationID();
            ddb.setDesID(destinationid);
            ddb.save();
            for(String itemName:checkedItemNames){
                CreateDestination2HashClass hc = stepTwoItems.get(itemName);
                if(hc.getCheckedStatus()==true){
                    TravelCheckItemsDB checkedItem = new TravelCheckItemsDB(hc.getItemID(),destinationid, itemName);
                    checkedItem.save();
                }
            }
            System.out.println(stepTwoItems.size()+" items have been saved to DB");
            Intent intent = new Intent(getBaseContext(),TravelActivity.class);
            startActivity(intent);
            return true;
        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_save, menu);
        return true;
    }

    // record the status
    private Hashtable<String, CreateDestination2HashClass> stepTwoItems = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_destination2);

        myToolbar = (Toolbar) findViewById(R.id.destination_toolbar);
        myToolbar.setTitle("Add destination 2/2");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        Button addBtn = (Button) findViewById(R.id.add_item_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user input item name
                String inputItem = ((EditText)findViewById(R.id.input_item)).getText().toString();
                EditText editText = (EditText)findViewById(R.id.input_item);
                editText.setText("");
                ListView lv = (ListView)findViewById(R.id.other_List);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>)lv.getAdapter();
                adapter.add(inputItem);

                int inputItemPos = adapter.getPosition(inputItem);
                lv.setItemChecked(inputItemPos,true);
                setListViewHeightBasedOnChildren(lv);
                // save user input item into UserInputDB
                int inputItemID = getInputItemAddID();
                TravelUserInputDB inputItemsDB = new TravelUserInputDB(inputItemID,inputItem);
                inputItemsDB.save();

                // update hash table
                CreateDestination2HashClass hval = new CreateDestination2HashClass(inputItemID, 17, true);
                if(!stepTwoItems.containsKey(inputItem)){
                    stepTwoItems.put(inputItem,hval);
                }
            }
        });

        new TravelCategoryGetter(this);
    }

    public void afterGetCategory() {
        // load categories
        List<TravelCategoryDB> categoryList = TravelCategoryDB.listAll(TravelCategoryDB.class);
        System.out.println("categories: " + categoryList);
        // for every category
        for (int i = 0; i < categoryList.size(); i++) {
            // find this category's view
            String categoryName = categoryList.get(i).getName();
            String idStr = "c" + i + "_name";
            System.out.println("id string: " + idStr);
            int textViewID = getResources().getIdentifier(idStr, "id", getPackageName());
            System.out.println("textview id: " + textViewID);
            TextView textView = (TextView) findViewById(textViewID);
            // set text and visiblity
            textView.setText(categoryName);
            textView.setVisibility(View.VISIBLE);
            // load this category's contents (argument, the category id)
            new AsyncLoadCategoryContents().execute(categoryList.get(i).getCategoryID());
        }
    }

    private class AsyncLoadCategoryContents extends AsyncTask<String, Void, ArrayList<String>> {

//        int lastCategoryFlag = -1;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
//            lastCategoryFlag = Integer.valueOf(params[0]);
            ArrayList<String> oneCategoryItems = loadItems(params[0]);
            return oneCategoryItems;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            selectedReady(result);
        }

        private ArrayList<String> loadItems(String cID) {
            ArrayList<String> oneCategoryItems = new ArrayList<>();
            oneCategoryItems.add(cID);
            // get all the items in this category
            String responseStr = doGet(Integer.parseInt(cID));
            try {
                JSONObject responseJson  = new JSONObject(responseStr);
                Iterator<String> keyIter = responseJson.keys();
                while(keyIter.hasNext()){
                    String itemID = keyIter.next();
                    String itemName = responseJson.getString(itemID);
                    CreateDestination2HashClass hashVal = new CreateDestination2HashClass(Integer.parseInt(itemID), Integer.parseInt(cID), false);
                    stepTwoItems.put(itemName, hashVal);
                    oneCategoryItems.add(itemName);
                    }
                } catch(JSONException e) {
                e.printStackTrace();
            }
            List<TravelCheckItemsDB> items = CheckedItemsDB.find(TravelCheckItemsDB.class,"category_id = ?",String.valueOf(cID));
            for (TravelCheckItemsDB item: items) {
                int itemID = item.getItemID();
                if (itemID < 1000) {
                        String itemName = item.getName();
                        CreateDestination2HashClass hashVal = new CreateDestination2HashClass(itemID,Integer.parseInt(cID),true);
                        stepTwoItems.put(itemName,hashVal);
                        oneCategoryItems.add(itemName);
                } else {
                    List<TravelUserInputDB> inputItems = TravelUserInputDB.find(TravelUserInputDB.class, "item_id = ?", String.valueOf(itemID));
                    String itemName = inputItems.get(0).getItemName();
                    CreateDestination2HashClass hashVal = new CreateDestination2HashClass(itemID,Integer.parseInt(cID), true);
                    stepTwoItems.put(itemName,hashVal);
                    oneCategoryItems.add(itemName);
                }
            }
            return oneCategoryItems;
        }

        private String doGet(int cID) {
            int status;
            StringBuilder responseSB = new StringBuilder("");
            HttpsURLConnection conn;

            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getTravelItemListByID?ID=" + cID);
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

            return responseSB.toString();
        }
    }

    private void selectedReady(ArrayList<String> oneSceItems){
        // get the ID of the loaded ready scenario
        int cID = Integer.parseInt(oneSceItems.get(0));
        oneSceItems.remove(0);

        // get the scenario's list view
        String listIDStr = "c"+cID+"_List";
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
                CreateDestination2HashClass hashVal = stepTwoItems.get(clickedItem);
                if(hashVal.getCheckedStatus()==true){
                    // when the item on the list is previously checked
                    hashVal.setCheckedStatus(false);
                }else{
                    // when the item on the list is previously unchecked
                    hashVal.setCheckedStatus(true);
                }
            }
        });

        ListView listView2 = (ListView) findViewById(R.id.other_List);
        listView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // set up the adapter of the list view
        final ArrayAdapter<String> listAdapter2 = new ArrayAdapter<String>(this,R.layout.check_box,R.id.check_text);
        listView2.setAdapter(listAdapter2);
        setListViewHeightBasedOnChildren(listView2);

        // set up OnItemClickListener
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = listAdapter.getItem(position);
                CreateDestination2HashClass hashVal = stepTwoItems.get(clickedItem);
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

    /*
 * reutrns an ItemID for newly entered user input items
 */
    private int getInputItemAddID(){
        int itemID;
        if(InputItemID.count(InputItemID.class)==0){
            itemID = 1501;
            InputItemID id = new InputItemID(1501);
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

    private int getDestinationID(){
        int sceID;
        if(InputSceID.count(InputSceID.class)==0){
            sceID = 100;
            InputSceID id = new InputSceID(100);
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

}
