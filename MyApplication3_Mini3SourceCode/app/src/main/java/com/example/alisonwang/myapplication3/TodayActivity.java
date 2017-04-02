package com.example.alisonwang.myapplication3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bringo for Today");
        toolbar.setSubtitle("02/20/2017");
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.content);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        data.add("keys");
        data.add("wallet");
        data.add("laptop");
        data.add("student id");
        data.add("driver's license");
        data.add("water bottlle");
        data.add("lipstick");
        data.add("tower");
        data.add("coupon");
        data.add("pencils");
        return data;
    }

}
