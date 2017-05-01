package com.example.bringo.database;

import com.example.bringo.helperclasses.TravelCategory;
import com.orm.SugarRecord;

/**
 * Created by huojing on 4/30/17.
 */

public class TravelCategoryDB extends SugarRecord{
    private String cID;
    private String name;

    public TravelCategoryDB() {}

    public TravelCategoryDB(String id, String name) {
        this.cID = id;
        this.name = name;
    }

    public String getCategoryID() {
        return cID;
    }

    public String getName() {
        return name;
    }
}
