package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by xuyidi on 4/26/17.
 */

public class InputSceID extends SugarRecord{
    int sceID;

    public InputSceID(){
    }

    public InputSceID(int sceID){
        this.sceID = sceID;
    }

    public void setSceID(int sceID){
        this.sceID = sceID;
    }

    public int getSceID(){
        return this.sceID;
    }
}
