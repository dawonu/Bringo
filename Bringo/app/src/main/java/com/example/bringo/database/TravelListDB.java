package com.example.bringo.database;

import com.example.bringo.helperclasses.TravelCategory;
import com.example.bringo.helperclasses.TravelItem;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by huojing on 4/28/17.
 */

public class TravelListDB extends SugarRecord {
    private List<TravelCategory> category;
    private List<List<TravelItem>> travelList;

    public TravelListDB() {}

    public TravelListDB(List<TravelCategory> category, List<List<TravelItem>> travelList) {
        this.category = category;
        this.travelList = travelList;
    }

    public List<TravelCategory> getCategory() {
        return category;
    }

    public List<List<TravelItem>> getTravelList() {
        return travelList;
    }
}
