package com.example.bringo;

import android.bluetooth.BluetoothSocket;

import com.orm.SugarRecord;

public class trackerDB extends SugarRecord{

    public String address;
    public String deviceName;
    public String tagName;
    private boolean forgetReminder;
    private BluetoothSocket socket;

    public trackerDB(){

    }

    public trackerDB(String id, String name, String deviceName, boolean forgetReminder, BluetoothSocket socket){
        this.address = id;
        this.tagName = name;
        this.deviceName = deviceName;
        this.forgetReminder = forgetReminder;
        this.socket = socket;
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
    public BluetoothSocket getSocket() { return this.socket; }


    public void changeName(String name){
        this.tagName = name;
    }
    public void changeForgetReminder(boolean reminder){
        this.forgetReminder = reminder;
    }
    public void changeSocket(BluetoothSocket socket){
        this.socket = socket;
    }
}
