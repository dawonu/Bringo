package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by huojing on 4/30/17.
 */

public class TravelCheckItemsDB extends SugarRecord{
    int itemID;
    int categoryID;
    int destinationID;
    String name;

    public TravelCheckItemsDB() {
    }

    public TravelCheckItemsDB(int itemID, int dID, String name) {
        this.itemID = itemID;
        this.destinationID = dID;
        this.name = name;
    }

    public int getItemID() {
        return this.itemID;
    }

    public int getCategoryID() {
        return this.categoryID;
    }

    public String getName() {
        return name;
    }

    public void setDestinationID(int i) {
        this.destinationID = i;
    }

    public int getDestinationID() {
        return destinationID;
    }
}
