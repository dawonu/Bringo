package com.example.bringo;

import com.orm.SugarRecord;

/**
 * Created by huojing on 4/16/17.
 */

public class UserDB extends SugarRecord{

    private String username;

    private boolean acLocation;
    private boolean acCalendar;
    private boolean acBluetooth;

    private boolean rmCalendar;
    private boolean rmTravel;

    public UserDB() {}

    public UserDB(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public void setAcLocation(boolean access) {
        acLocation = access;
    }

    public boolean getAcLocation() {
        return acLocation;
    }

    public void setAcCalendar(boolean access) {
        acCalendar = access;
    }

    public boolean getAcCalendar() {
        return acCalendar;
    }

    public void setAcBluetooth(boolean access) {
        acBluetooth = access;
    }

    public boolean getAcBluetooth() {
        return acBluetooth;
    }

    public void setRmCalendar(boolean remindStatus) {
        rmCalendar = remindStatus;
    }

    public boolean getRmCalendar() {
        return rmCalendar;
    }

    public void setRmTravel(boolean remindStatus) {
        rmTravel = remindStatus;
    }

    public boolean getRmTravel() {
        return rmTravel;
    }
}
