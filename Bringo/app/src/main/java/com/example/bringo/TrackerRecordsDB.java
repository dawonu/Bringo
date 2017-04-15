package com.example.bringo;

import com.orm.SugarRecord;

public class TrackerRecordsDB extends SugarRecord{

    /*
    private int id;
    private String name;
    private String pass;
    private boolean forgetReminder;

    public TrackerRecordsDB(){

    }

    public TrackerRecordsDB(int id, String name, String pwd, boolean forgetReminder){
        this.id = id;
        this.name = name;
        this.pass = pwd;
        this.forgetReminder = forgetReminder;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getPwd(){
        return this.pass;
    }

    public boolean getForgetReminder(){
        return this.forgetReminder;
    }
    */
    int scenarioID;

    public TrackerRecordsDB(){
    }

    public TrackerRecordsDB(int scenarioID){
        this.scenarioID = scenarioID;
    }

    public int getScenarioID(){
        return scenarioID;
    }
}
