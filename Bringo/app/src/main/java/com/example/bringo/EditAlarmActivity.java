package com.example.bringo;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bringo.database.ScenarioAlarmDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EditAlarmActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ExpandableListView expandableListView;
    private AlertDialog dialog;
    private ScenarioAlarmDB tmpDB;
    private EditText tmpTime;

    private List<String> defaultScenarioNames = new ArrayList<>();
    private List<DefaultScenarios> group;
    private List<ScenarioAlarmDB> children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        myToolbar = (Toolbar) findViewById(R.id.travel_toolbar);
        myToolbar.setTitle("Edit Alarms");
        setSupportActionBar(myToolbar);

        expandableListView = (ExpandableListView) findViewById(R.id.elv);

        group = DefaultScenarios.listAll(DefaultScenarios.class);
        children = new LinkedList<>();
        for(DefaultScenarios ds: group) {
            children.add(ds.getAlarm());
            if(ds.getName() != null) {
                defaultScenarioNames.add(ds.getName());
            }
        }
//        System.out.println(defaultScenarioNames);
        AlarmExpandableListAdapter adapter = new AlarmExpandableListAdapter(this);
        expandableListView.setAdapter(adapter);
    }

    private class AlarmExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private LayoutInflater inflater;

        public AlarmExpandableListAdapter(Context context) {
                this.context = context;
                inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return group.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.elv_title,null);
            ImageView iv_group_icon = (ImageView) view.findViewById(R.id.iv_group_icon);
            TextView tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);

            tv_group_name.setText(group.get(groupPosition).getName());

            if(isExpanded){
                iv_group_icon.setImageResource(R.drawable.arrow_down);
            }else {
                iv_group_icon.setImageResource(R.drawable.arrow_right);
            }
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.elv_content,null);

            tmpDB = (ScenarioAlarmDB) getChild(groupPosition, childPosition);
            tmpTime = (EditText) view.findViewById(R.id.time_picker);
            tmpTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new AlertDialog.Builder(EditAlarmActivity.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(R.layout.dialog_timepicker);

                    ((TimePicker) dialog.getWindow().findViewById(R.id.time_picker)).setIs24HourView(true);
                    ((TimePicker) dialog.getWindow().findViewById(R.id.time_picker)).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
                    {
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
                        {
                            tmpDB.setHour(hourOfDay);
                            tmpDB.setMinute(minute);
                        }
                    });

                    dialog.getWindow().findViewById(R.id.time_sure).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            tmpDB.save();
                            tmpTime.setText(tmpDB.getTime());
                            dialog.dismiss();
                        }});
                }
            });
            if (tmpDB.getTime() != null) {
                tmpTime.setText(tmpDB.getTime());
            }

            boolean[] choice = tmpDB.getDayOfWeek();
            CheckBox S = (CheckBox) view.findViewById(R.id.checkboxS);
            S.setOnCheckedChangeListener(new DayOfWeekCheckListener(0));
            if(choice[0] == true) {
                S.setChecked(true);
            }
            CheckBox M = (CheckBox) view.findViewById(R.id.checkboxM);
            M.setOnCheckedChangeListener(new DayOfWeekCheckListener(1));
            if(choice[1] == true) {
                M.setChecked(true);
            }
            CheckBox T = (CheckBox) view.findViewById(R.id.checkboxT);
            T.setOnCheckedChangeListener(new DayOfWeekCheckListener(2));
            if(choice[2] == true) {
                T.setChecked(true);
            }
            CheckBox W = (CheckBox) view.findViewById(R.id.checkboxW);
            W.setOnCheckedChangeListener(new DayOfWeekCheckListener(3));
            if(choice[3] == true) {
                W.setChecked(true);
            }
            CheckBox Th = (CheckBox) view.findViewById(R.id.checkboxTh);
            Th.setOnCheckedChangeListener(new DayOfWeekCheckListener(4));
            if(choice[4] == true) {
                Th.setChecked(true);
            }
            CheckBox F = (CheckBox) view.findViewById(R.id.checkboxF);
            F.setOnCheckedChangeListener(new DayOfWeekCheckListener(5));
            if(choice[5] == true) {
                F.setChecked(true);
            }
            CheckBox Sa = (CheckBox) view.findViewById(R.id.checkboxSa);
            Sa.setOnCheckedChangeListener(new DayOfWeekCheckListener(6));
            if(choice[6] == true) {
                Sa.setChecked(true);
            }

            CheckBox repeat = (CheckBox) view.findViewById(R.id.repeat);
            repeat.setOnCheckedChangeListener(new RepeatCheckListener());
            if (tmpDB.getRepeat() == true) {
                repeat.setChecked(true);
            }

            Button save = (Button) view.findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tmpDB.save();
                }
            });

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private class DayOfWeekCheckListener implements CompoundButton.OnCheckedChangeListener{

        int index;

        DayOfWeekCheckListener(int i) {
            index = i;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                tmpDB.checkDayOfWeek(index);
            } else {
                tmpDB.uncheckDayOfWeek(index);
            }
            tmpDB.save();
        }
    }

    private class RepeatCheckListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                tmpDB.checkRepeat();
            } else {
                tmpDB.uncheckRepeat();
            }
            tmpDB.save();
        }
    }
}
