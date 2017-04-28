package com.example.bringo.helperclasses;

/**
 * Created by huojing on 4/28/17.
 */

public class TravelCategory {

    private String id;
    private String name;

    public TravelCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
