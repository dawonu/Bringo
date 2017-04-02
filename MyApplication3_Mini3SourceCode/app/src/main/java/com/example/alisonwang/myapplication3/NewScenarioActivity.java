package com.example.alisonwang.myapplication3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NewScenarioActivity extends AppCompatActivity {

    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scenario);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Scenario");
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.content);
        adapter = new CategoryAdapter(this);
        processList();
        listView.setAdapter(adapter);
    }

    private void processList() {
        List<String> category = new ArrayList<>();
        category.add("essentials");
        category.add("school");
        category.add("self-defined");
        List<List<String>> content = new ArrayList();
        List<String> data1 = new ArrayList<String>();
        data1.add("keys");
        data1.add("wallet");
        List<String> data2 = new ArrayList<String>();
        data2.add("laptop");
        data2.add("student id");
        data2.add("pencil");
        List<String> data3 = new ArrayList<String>();
        data3.add("driver's license");
        content.add(data1);
        content.add(data2);
        content.add(data3);

        for (int i = 0; i < category.size(); i++) {
            adapter.addSeparatorItem(category.get(i));
            for (int j = 0; j < content.get(i).size(); j++) {
                    adapter.addItem(content.get(i).get(j));
            }
        }
    }

}
