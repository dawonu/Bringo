package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/29/17.
 */

public class TodayListDB extends SugarRecord{
    String date;
    String itemName;
    boolean weatherFlag;

    public TodayListDB(){}

    public TodayListDB(String date, String itemName, boolean weatherFlag){
        this.date = date;
        this.itemName = itemName;
        this.weatherFlag = weatherFlag;
    }

    public boolean isWeatherItem(){
        return weatherFlag;
    }

    public String getDate(){
        return date;
    }

    public String getItemName(){
        return itemName;
    }

}
