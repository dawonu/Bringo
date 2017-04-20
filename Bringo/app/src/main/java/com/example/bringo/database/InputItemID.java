package com.example.bringo.database;

import android.content.Intent;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/19/17.
 */

public class InputItemID extends SugarRecord{
    int itemID;

    public InputItemID(){
    }

    public InputItemID(int itemID){
        this.itemID = itemID;
    }

    public void setItemID(int itemID){
        this.itemID = itemID;
    }

    public int getItemID(){
        return this.itemID;
    }

}
