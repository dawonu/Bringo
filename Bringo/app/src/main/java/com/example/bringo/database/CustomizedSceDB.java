package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/26/17.
 */

public class CustomizedSceDB extends SugarRecord{
    int scenarioID;


    String name;

    public CustomizedSceDB(){
    }

    public CustomizedSceDB(int scenarioID,String name){
        this.scenarioID = scenarioID;
        this.name = name;
    }

    public int getScenarioID(){
        return scenarioID;
    }


    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
