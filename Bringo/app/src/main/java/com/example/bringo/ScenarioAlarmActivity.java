package com.example.bringo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class ScenarioAlarmActivity extends AppCompatActivity {

    private Toolbar myToolbar;

    private List<String> scenarioNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_alarm);

        List<DefaultScenarios> defaultScenariosList = DefaultScenarios.listAll(DefaultScenarios.class);
        for(DefaultScenarios ds: defaultScenariosList) {
            if(ds.getName() != null) {
                scenarioNames.add(ds.getName());
            }
        }
        System.out.println(scenarioNames);

        myToolbar = (Toolbar) findViewById(R.id.travel_toolbar);
        myToolbar.setTitle("Set Alarms");
        setSupportActionBar(myToolbar);
    }
}
