package com.example.bringo;

import com.orm.SugarRecord;

public class trackerDB extends SugarRecord{

    private int id;
    private String tagName;
    private String pass;
    private boolean forgetReminder;

    public trackerDB(){

    }

    public trackerDB(int id, String name, String pwd, boolean forgetReminder){
        this.id = id;
        this.tagName = name;
        this.pass = pwd;
        this.forgetReminder = forgetReminder;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.tagName;
    }

    public String getPwd(){
        return this.pass;
    }

    public boolean getForgetReminder(){
        return this.forgetReminder;
    }

}
