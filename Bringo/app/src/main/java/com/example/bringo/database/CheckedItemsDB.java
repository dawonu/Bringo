package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/19/17.
 */

public class CheckedItemsDB extends SugarRecord {
    int itemID;
    int scenarioID;

    public CheckedItemsDB(){
    }

    public CheckedItemsDB(int itemID, int scenarioID){
        this.itemID = itemID;
        this.scenarioID = scenarioID;
    }

    public int getItemID(){
        return this.itemID;
    }

    public int getScenarioID(){
        return this.scenarioID;
    }
}
