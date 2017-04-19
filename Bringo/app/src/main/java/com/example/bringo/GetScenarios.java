package com.example.bringo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyidi on 4/4/17.
 */

public class GetScenarios {
    HomeActivity ha = null;
    List<String> names = new ArrayList<String>();

    public void getScenarioNames(HomeActivity ha) {
        this.ha = ha;
        new AsyncGetSceNames().execute();
    }

    private class AsyncGetSceNames extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            getDefaultScenarioNames();
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            getCustomizedScenarioNames();
            ha.namesReady(names);
        }

        /*
         * getDefaultScenarioNames() sends request to app server and parse JSON message in response
         * to get scenario names for each scenario ID stored in DefaultScenarios DB
         */
        private void getDefaultScenarioNames(){
            List<DefaultScenarios> scenarioIdList = DefaultScenarios.listAll(DefaultScenarios.class);
            for(DefaultScenarios ds:scenarioIdList){
                int sID = ds.getScenarioID();

                // parse the String of response to JSON object to retrieve scenario names
                String responseStr = doGet(String.valueOf(sID));
                try{
                    JSONObject responseJson = new JSONObject(responseStr);
                    String sceName = responseJson.getString(String.valueOf(sID));
                    names.add(sceName);
                    ds.setName(sceName);
                    ds.save();
                    Log.d("debugging names list", sceName);
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void getCustomizedScenarioNames(){

        }

        /*
         * getGet() method sends GET HTTP request to app's server and returns the JSON response:
         * {"scenario ID": scenarioName}
         */
        private String doGet(String sID){
            int status;
            StringBuilder responseSB = new StringBuilder("");;
            HttpURLConnection conn;
            try{
                // send GET request
                URL url = new URL("https://morning-waters-80123.herokuapp.com/getScenarioNameByID?ID="+sID);
                conn = (HttpURLConnection) url.openConnection();
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



}
