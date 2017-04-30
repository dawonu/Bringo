package com.example.bringo.dataretriever;

import android.os.AsyncTask;

import com.example.bringo.CreateDestination2Activity;
import com.example.bringo.database.TravelListDB;
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
 * Created by huojing on 4/27/17.
 */

public class TravelListGetter {

    public TravelListGetter() {
        new AsyncGetTravelList().execute();
    }

    private class AsyncGetTravelList extends AsyncTask<String,Void,Void> {
        private List<TravelCategory> category = new LinkedList<>();
        private List<List<TravelItem>> travelList = new LinkedList<>();
//        private List<String> cIDs = new LinkedList<>();

        @Override
        protected Void doInBackground(String... params) {
            getTravelCategoryList();
            for (TravelCategory t: category) {
                getTravelCategoryContentsList(t.getID());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            new TravelListDB(category, travelList);
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
            parseJson(category, responseSB.toString(), true);
            return null;
        }

        private String getTravelCategoryContentsList(String cID){
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
            List<TravelItem> temp = new ArrayList<>();
            parseJson(temp, responseSB.toString(), false);
            for (TravelItem t: temp) {
                t.setParentId(cID);
            }
            travelList.add(temp);
            System.out.println("in async, content" +responseSB.toString());

            return null;
        }

        private void parseJson(List list, String responseStr, boolean isCategory){
            try{
                JSONObject responseJson  = new JSONObject(responseStr);
                Iterator<String> keyIter = responseJson.keys();
                while(keyIter.hasNext()){
                    String itemID = keyIter.next();
                    String itemName = responseJson.getString(itemID);
                    if (isCategory) {
                        category.add(new TravelCategory(itemID, itemName));
                    } else {
                        list.add(new TravelItem(itemID, itemName));
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
