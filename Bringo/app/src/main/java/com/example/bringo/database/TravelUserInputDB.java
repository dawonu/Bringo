package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by huojing on 4/30/17.
 */

public class TravelUserInputDB extends SugarRecord {
    int itemID;
    String itemName;

    public TravelUserInputDB(){
    }

    public TravelUserInputDB(int itemID,String itemName){
        this.itemID = itemID;
        this.itemName = itemName;
    }

    public int getItemID(){
        return this.itemID;
    }

    public String getItemName(){
        return this.itemName;
    }
}
