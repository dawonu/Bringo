package com.example.bringo;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/4/17.
 */

public class DefaultScenarios extends SugarRecord {
    int scenarioID;

    public DefaultScenarios(){
    }

    public DefaultScenarios(int scenarioID){
        this.scenarioID = scenarioID;
    }

    public int getScenarioID(){
        return scenarioID;
    }

}

