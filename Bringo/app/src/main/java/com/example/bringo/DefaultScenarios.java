package com.example.bringo;

import com.example.bringo.database.ScenarioAlarmDB;
import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/4/17.
 */

public class DefaultScenarios extends SugarRecord {
    int scenarioID;

    private String name;

    public DefaultScenarios(){
    }

    public DefaultScenarios(int scenarioID){
        this.scenarioID = scenarioID;
    }

    public int getScenarioID(){
        return scenarioID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

