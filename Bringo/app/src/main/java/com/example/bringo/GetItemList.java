package com.example.bringo;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by xuyidi on 4/19/17.
 */

public class GetItemList {
    private DefaultListActivity da;
    private int sID;
    private ArrayList<String> defaultItems = new ArrayList<String>();
    private int firstItemID;

    public void getItemList(DefaultListActivity da, int sID){
        this.da = da;
        this.sID = sID;
        new AsyncGetItemList().execute(String.valueOf(sID));
    }

    private class AsyncGetItemList extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            String responseStr = getDefaultItemList(params[0]);
            firstItemID = parseItemListJson(responseStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            da.itemListReady(defaultItems, firstItemID);
        }

        private String getDefaultItemList(String sID){
            int status;
            StringBuilder responseSB = new StringBuilder("");;
            HttpsURLConnection conn;

            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getItemListByID?ID="+sID);
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

        private int parseItemListJson(String responseStr){
            int firstItemFlag = 1;
            int firstItemID = -1;
            try{
                JSONObject responseJson  = new JSONObject(responseStr);
                Iterator<String> keyIter = responseJson.keys();
                while(keyIter.hasNext()){
                    String itemID = keyIter.next();
                    String itemName = responseJson.getString(itemID);
                    defaultItems.add(itemName);
                    if(firstItemFlag == 1){
                        firstItemID = Integer.valueOf(itemID);
                    }
                    firstItemFlag = 0;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return firstItemID;
        }
    }


}
