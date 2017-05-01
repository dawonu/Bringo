package com.example.bringo.dataretriever;

import android.os.AsyncTask;

import com.example.bringo.CreateDestination2Activity;
import com.example.bringo.database.TravelCategoryDB;
import com.example.bringo.helperclasses.TravelCategory;
import com.example.bringo.helperclasses.TravelItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by huojing on 4/30/17.
 */

public class TravelCategoryGetter {
    private CreateDestination2Activity act;

    public TravelCategoryGetter(CreateDestination2Activity activity) {
        this.act = activity;
        new TravelCategoryGetter.AsyncGetTravelList().execute();
    }

    private class AsyncGetTravelList extends AsyncTask<String,Void,Void> {
//        private List<TravelCategory> category = new LinkedList<>();

        @Override
        protected Void doInBackground(String... params) {
            TravelCategoryDB.deleteAll(TravelCategoryDB.class);
            getTravelCategoryList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            act.afterGetCategory();
        }

        private String getTravelCategoryList(){
            int status;
            StringBuilder responseSB = new StringBuilder("");
            HttpsURLConnection conn;

            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getTravelCategoryNameByID?ID=all");
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
            System.out.println("in async, category" + responseSB.toString());
            parseJson(responseSB.toString());
            return null;
        }

        private void parseJson(String responseStr){
            try{
                JSONObject responseJson  = new JSONObject(responseStr);
                Iterator<String> keyIter = responseJson.keys();
                while(keyIter.hasNext()){
                    String itemID = keyIter.next();
                    String itemName = responseJson.getString(itemID);
                    TravelCategoryDB temp = new TravelCategoryDB(itemID, itemName);
                    temp.save();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
