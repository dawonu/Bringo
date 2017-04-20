package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/19/17.
 */

public class UserInputItemsDB extends SugarRecord {
    int itemID;
    String itemName;

    public UserInputItemsDB(){
    }

    public UserInputItemsDB(int itemID,String itemName){
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
