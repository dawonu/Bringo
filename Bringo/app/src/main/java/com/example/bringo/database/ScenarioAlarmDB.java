package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by huojing on 4/19/17.
 */

public class ScenarioAlarmDB extends SugarRecord{
    private int[] time = new int[3];
    private boolean[] dayOfWeek = new boolean[7];
    private boolean repeat;

    public ScenarioAlarmDB() {}

    public void setHour(int hour) {
        time[0] = hour;
    }

    public void setMinute(int minute) {
        time[1] = minute;
    }

    public int getHour() {
       return time[0];
    }
    public int getMinute() {
        return time[1];
    }
    public String getTime() {
        return String.format("%02d", time[0]) + ":" + String.format("%02d", time[1]);
    }

    public void checkDayOfWeek(int index) {
        dayOfWeek[index] = true;
    }

    public void uncheckDayOfWeek(int index) {
        dayOfWeek[index] = false;
    }

    public boolean[] getDayOfWeek() {
        return dayOfWeek;
    }

    public void checkRepeat() {repeat = true;}

    public void uncheckRepeat() {repeat = false;}

    public boolean getRepeat() {
        return repeat;
    }

}
