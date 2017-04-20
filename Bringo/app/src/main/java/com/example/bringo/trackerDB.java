package com.example.bringo;

import com.orm.SugarRecord;

public class trackerDB extends SugarRecord{

    public int itemID;
    public String tagName;
    public String pass;
    private boolean forgetReminder;

    public trackerDB(){

    }

    public trackerDB(int id, String name, String pwd, boolean forgetReminder){
        this.itemID = id;
        this.tagName = name;
        this.pass = pwd;
        this.forgetReminder = forgetReminder;
    }

    public int getItemID(){
        return this.itemID;
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

    public void changeID(int ID){
        this.itemID = ID;
    }
    public void changeName(String name){
        this.tagName = name;
    }
    public void changePwd(String pwd) {
        this.pass = pwd;
    }
    public void changeForgetReminder(boolean reminder){
        this.forgetReminder = reminder;
    }
}
