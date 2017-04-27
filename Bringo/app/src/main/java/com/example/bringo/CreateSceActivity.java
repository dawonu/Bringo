package com.example.bringo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyidi on 4/7/17.
 */

public class CreateSceActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private ArrayList<Integer> checkedScenarios = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsce);

        //set up toolbar
        myToolbar = (Toolbar)findViewById(R.id.defaultList_toolbar);
        myToolbar.setTitle("New List (1/2)");
        setSupportActionBar(myToolbar);

        //set up gridview
        ArrayList<String> sceNames  = loadDefaultSceNames();
        GridView gridView = (GridView) findViewById(R.id.gridview_createsce_step1);
        gridView.setAdapter(new GridCheckBoxAdapter(this,sceNames));

        //set up "Next" button
        Button nextBtn = (Button) findViewById(R.id.btn_createsce_step1);
        nextBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // jump to the page of create new list step 2
                Intent intent = new Intent(CreateSceActivity.this,CreateSceTwoActivity.class);
                // forward the checkedScenarios to next activity
                intent.putIntegerArrayListExtra("checkedScenarios",checkedScenarios);
                startActivity(intent);
            }
        });

    }

    private ArrayList<String> loadDefaultSceNames(){
        ArrayList<String> sceNames = new ArrayList<>();
        List<DefaultScenarios> sceList  = DefaultScenarios.listAll(DefaultScenarios.class);
        for(DefaultScenarios ds:sceList){
            int sID = ds.getScenarioID();
            if(sID<6){
                sceNames.add(ds.getName());
            }
        }
        return sceNames;
    }

    public class GridCheckBoxAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<String> sceNames;

        public GridCheckBoxAdapter(Context context, ArrayList<String> sceNames){
            this.context = context;
            this.sceNames = sceNames;
        }

        @Override
        public int getCount() {
            return sceNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckBox checkBox;
            if(convertView == null){
                checkBox = new CheckBox(context);
                checkBox.setLayoutParams(new GridView.LayoutParams(450,220));
                checkBox.setPadding(8,8,8,8);
            }else{
                checkBox = (CheckBox) convertView;
            }

            checkBox.setText(sceNames.get(position));
            checkBox.setTextSize(20);
            checkBox.setBackgroundColor(Color.LTGRAY);
            checkBox.setId(position);

            // set onclick listneer for each checkbox
            checkBox.setOnClickListener(new ScenarioCheckListener(position));
            return checkBox;
        }
    }

    private class ScenarioCheckListener implements View.OnClickListener{
        int sID;  // indicates which scenario is selected on the page

        public ScenarioCheckListener(int position){
            this.sID = position + 1;
        }

        @Override
        public void onClick(View v) {
            // add or delete selected scenario's sID into the checkedScenarios list
            if(checkedScenarios.contains(Integer.valueOf(sID))){
                // if the scenario is unchecked
                checkedScenarios.remove(Integer.valueOf(sID));
            }else{
                // if the scenario is checked
                checkedScenarios.add(Integer.valueOf(sID));
            }
        }
    }


}
