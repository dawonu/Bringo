package com.example.bringo;

import com.orm.SugarRecord;

public class trackerDB extends SugarRecord{

    public String address;
    public String deviceName;
    public String tagName;
    private boolean forgetReminder;

    public trackerDB(){

    }

    public trackerDB(String id, String name, String deviceName, boolean forgetReminder){
        this.address = id;
        this.tagName = name;
        this.deviceName = deviceName;
        this.forgetReminder = forgetReminder;
    }

    public String getAddress(){
        return this.address;
    }

    public String getName(){
        return this.tagName;
    }

    public String getDeviceName(){
        return this.deviceName;
    }

    public boolean getForgetReminder(){
        return this.forgetReminder;
    }


    public void changeName(String name){
        this.tagName = name;
    }
    public void changeForgetReminder(boolean reminder){
        this.forgetReminder = reminder;
    }
}
