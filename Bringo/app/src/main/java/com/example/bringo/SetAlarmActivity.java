package com.example.bringo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.ScenarioAlarmDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = new Intent(SetAlarmActivity.this, EditAlarmActivity.class);
            startActivity(intent);
            return true;
        }
    };
    private ListView listView;
    private Button homeButton;
    private List<String> scenarioNames = new ArrayList<>();
    private List<DefaultScenarios> defaultScenarios = DefaultScenarios.listAll(DefaultScenarios.class);
    private List<CustomizedSceDB> customizedScenarios = CustomizedSceDB.listAll(CustomizedSceDB.class);
    private List<ScenarioAlarmDB> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        myToolbar = (Toolbar) findViewById(R.id.set_alarm_toolbar);
        myToolbar.setTitle("Set Alarm");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

//        defaultScenarios = DefaultScenarios.listAll(DefaultScenarios.class);
//        customizedScenarios = CustomizedSceDB.listAll(CustomizedSceDB.class);
        alarms = ScenarioAlarmDB.listAll(ScenarioAlarmDB.class);
//        System.out.println("1" + defaultScenarios);
        for(DefaultScenarios ds: defaultScenarios) {
            if(ds.getName() != null) {
                scenarioNames.add(ds.getName());
            }
        }
        for (CustomizedSceDB cs: customizedScenarios) {
            if (cs.getName()!= null) {
                scenarioNames.add(cs.getName());
            }
        }

        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(new AlarmListAdapter(this));

        homeButton = (Button) findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetAlarmActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_edit, menu);
        return true;
    }

    private class AlarmListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        AlarmListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return scenarioNames.size();
        }

        @Override
        public Object getItem(int position) {
            return defaultScenarios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.lv_alarm,null);
            TextView title = (TextView) view.findViewById(R.id.name);
            title.setText(scenarioNames.get(position));
            TextView description = (TextView) view.findViewById(R.id.description);
//            System.out.println(alarms.get(position).getTime());
            description.setText(alarms.get(position).getTime());

            Switch s = (Switch) view.findViewById(R.id.aswitch);
            final ScenarioAlarmDB alarm = alarms.get(position);
            if(alarm.isTurnOn()) {
                s.setChecked(true);
            }
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        System.out.println("alarm is on: "+scenarioNames.get(position));
                        alarm.turnOn();
                        alarm.save();

                        String alarmName = scenarioNames.get(position);
                        NotificationReceiver.updateNotification("Reminder", "Check this list: \"" + alarmName+ "\" before you leave.");
//                        System.out.println("array: " + alarm.getNotificationArray()[0]);
                        NotificationReceiver.setRepeatDate(alarm.getNotificationArray());

                        boolean repeat = alarm.getRepeat();
                        int hour = alarm.getHour();
                        System.out.println("hour"+hour);
                        int minute = alarm.getMinute();
                        System.out.println("minute"+minute);
                        setNotificationAlarm(hour, minute, 0, false);
                    } else {
                        alarm.turnOff();
                        alarm.save();
                        // how to cancel?
                    }
                }
            });
            return view;
        }
    }

    public void setNotificationAlarm(int hour,int minute, boolean repeat){
        // the following code is just for notification test!!!
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);

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
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }

        // cancel the alarm
        //alarmManager.cancel(pendingIntent);

    }

    public void setNotificationAlarm(int hour,int minute,int second, boolean repeat){
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
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }

        // cancel the alarm
        //alarmManager.cancel(pendingIntent);

    }


}
