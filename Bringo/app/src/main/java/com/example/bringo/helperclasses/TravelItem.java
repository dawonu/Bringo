package com.example.bringo.helperclasses;

/**
 * Created by huojing on 4/28/17.
 */

public class TravelItem {

    private String id;
    private String parentId;
    private String name;

    public TravelItem(String ID, String name) {
        this.id = ID;
        this.name = name;
    }

    public void setParentId(String pid) {
        this.parentId = pid;
    }

    public String getParentID() {
        return parentId;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
