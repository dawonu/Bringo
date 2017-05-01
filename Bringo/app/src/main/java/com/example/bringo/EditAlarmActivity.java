package com.example.bringo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.DestinationDB;
import com.example.bringo.database.ScenarioAlarmDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EditAlarmActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = new Intent(EditAlarmActivity.this, SetAlarmActivity.class);
            startActivity(intent);
            return true;
        }
    };
    private ExpandableListView expandableListView;
    private AlertDialog dialog;
    private ScenarioAlarmDB tmpDB;
    private Button tmpTime;
    private EditText tmpTimeText;

    private List<String> scenarioNames = new ArrayList<>();
    private List<DefaultScenarios> group1;
    private List<CustomizedSceDB> group2;
    private List<ScenarioAlarmDB> children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        myToolbar = (Toolbar) findViewById(R.id.travel_toolbar);
        myToolbar.setTitle("Edit Alarms");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        expandableListView = (ExpandableListView) findViewById(R.id.elv);

        group1 = DefaultScenarios.listAll(DefaultScenarios.class);
        group2 = CustomizedSceDB.listAll(CustomizedSceDB.class);
        children = ScenarioAlarmDB.listAll(ScenarioAlarmDB.class);
        for(DefaultScenarios ds: group1) {
            if(ds.getName() != null) {
                scenarioNames.add(ds.getName());
            }
        }
        for(CustomizedSceDB cs: group2) {
            if(cs.getName() != null) {
                scenarioNames.add(cs.getName());
            }
        }
//        System.out.println(defaultScenarioNames);
        AlarmExpandableListAdapter adapter = new AlarmExpandableListAdapter(this);
        expandableListView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_back, menu);
        return true;
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
            return group1.size() + group2.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition < group1.size()) {
                return group1.get(groupPosition);
            } else {
                return group2.get(groupPosition-group1.size());
            }
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

            if (groupPosition < group1.size()) {
                tv_group_name.setText(group1.get(groupPosition).getName());
            } else {
                tv_group_name.setText(group2.get(groupPosition-group1.size()).getName());
            }

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
            tmpTime = (Button) view.findViewById(R.id.time_picker);
            tmpTimeText = (EditText) view.findViewById(R.id.time_picker_text);
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
                            tmpTimeText.setText(tmpDB.getTime());
                            dialog.dismiss();
                        }});
                }
            });
            if (tmpDB.getTime() != null) {
                tmpTimeText.setText(tmpDB.getTime());
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
                    if (tmpDB.isTurnOn()) {
//                        System.out.println("alarm is on: "+scenarioNames.get(groupPosition));
                        tmpDB.turnOn();
                        tmpDB.save();

                        String alarmName = "name";
                        NotificationReceiver.updateNotification("Reminder", "Check this list: \"" + alarmName+ "\" before you leave.");
//                        System.out.println("array: " + tmpDB.getNotificationArray()[0]);
                        NotificationReceiver.setRepeatDate(tmpDB.getNotificationArray());

                        boolean repeat = tmpDB.getRepeat();
                        int hour = tmpDB.getHour();
                        System.out.println("hour"+hour);
                        int minute = tmpDB.getMinute();
                        System.out.println("minute"+minute);
                        setNotificationAlarm(hour, minute, 11, false);
                    }
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

    public void setNotificationAlarm(int hour,int minute,int second, boolean repeat){
        System.out.println("hour"+hour);
        System.out.println("minute"+minute);
        // the following code is just for notification test!!!
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);

        // NotificationReceiver is a BroadcastReceiver class
        Intent intent = new Intent(getApplicationContext(),NotificationReceiver.class);
        // Alarm Service requires a PendingIntent as param, set the intent to the pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),101,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // set an alarm that works even if app is cosed, depends on calendar time,
        // repeats everyday, with pendingIntent
        // So when alarm goes off NotificationReceiver will be triggered
        if(repeat == true){
            System.out.println("millis1 "+calendar.getTimeInMillis());
            Calendar tmp = Calendar.getInstance();
            System.out.println("millis2 "+tmp.getTimeInMillis());
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);
        }else{
            System.out.println("millis1 "+calendar.getTimeInMillis());
            Calendar tmp = Calendar.getInstance();
            System.out.println("millis2 "+tmp.getTimeInMillis());
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }

        // cancel the alarm
        //alarmManager.cancel(pendingIntent);

    }
}