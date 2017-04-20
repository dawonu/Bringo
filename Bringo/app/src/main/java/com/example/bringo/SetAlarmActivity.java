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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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
    private List<String> defaultScenarioNames = new ArrayList<>();
    private List<DefaultScenarios> defaultScenarios;
    private List<ScenarioAlarmDB> defaultAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        myToolbar = (Toolbar) findViewById(R.id.set_alarm_toolbar);
        myToolbar.setTitle("Set Alarm");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        defaultScenarios = DefaultScenarios.listAll(DefaultScenarios.class);
        defaultAlarms = ScenarioAlarmDB.listAll(ScenarioAlarmDB.class);
        System.out.println("1" + defaultScenarios);
        for(DefaultScenarios ds: defaultScenarios) {
            if(ds.getName() != null) {
                defaultScenarioNames.add(ds.getName());
            }
//            System.out.println("1" + ds.scenarioID);
        }

//        for(ScenarioAlarmDB s: defaultAlarms) {
//            System.out.println("2" + s.getID());
//        }

        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(new AlarmListAdapter(this));
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
            return defaultScenarioNames.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.lv_alarm,null);
            TextView title = (TextView) view.findViewById(R.id.name);
            title.setText(defaultScenarioNames.get(position));
            TextView description = (TextView) view.findViewById(R.id.description);
            description.setText(defaultAlarms.get(position).getTime());

            Switch s = (Switch) view.findViewById(R.id.aswitch);
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                    } else {

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

}
