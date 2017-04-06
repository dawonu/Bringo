package com.example.bringo;

import android.os.AsyncTask;

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

    public void getScenarioNames(HomeActivity ha){
        this.ha = ha;
        getDefaultScenarioNames();
        getCustomizedScenarioNames();
        System.out.println("names ready "+names.size());
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
            new AsyncGetSceNames().execute(String.valueOf(sID));
        }
    }

    private void getCustomizedScenarioNames(){

    }

    private class AsyncGetSceNames extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            return doGet(params[0]);
        }
        protected void onPostExecute(String responseStr){
            System.out.println("Post: "+responseStr);
            names.add(responseStr);
            ha.namesReady(names);
        }

    }


    public String doGet(String sID){
        System.out.println("enter doGet method");
        int status;
        StringBuilder responseSB = new StringBuilder("");;
        HttpURLConnection conn;
        try{
            // send GET request
            URL url = new URL("https://morning-waters-80123.herokuapp.com/getScenarioNameByID?ID="+sID);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            System.out.println("begin read response");
            // read response
            status = conn.getResponseCode();
            if(status != 200){
                System.out.println("get not success");
                return "wrong";
            }
            System.out.println("state is 200");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String responseLine = "";
            while((responseLine = br.readLine())!=null){
                System.out.println(responseLine);
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
