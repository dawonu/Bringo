package com.example.bringo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;


import com.example.bringo.database.ScenarioAlarmDB;


import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final static int DEFAULT_SCENARIOS_COUNT = 6;
    private GridView gridView;
    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = new Intent(HomeActivity.this, SetAlarmActivity.class);
            startActivity(intent);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set up toolbar
        myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        myToolbar.setTitle("Home");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);


        final HomeActivity ha = this;
        // initialize the default scenarios db
        initialDefaultScenariosDB();
        GetScenarios gs = new GetScenarios();
        gs.getScenarioNames(ha);
    }



    /*
     * initialDefaultScenarioDB() adds default scenario IDs into the DefaultScenario Database
     */
    private void initialDefaultScenariosDB(){
        DefaultScenarios.deleteAll(DefaultScenarios.class);
        ScenarioAlarmDB.deleteAll(ScenarioAlarmDB.class);
        for(int i=1;i<=DEFAULT_SCENARIOS_COUNT;i++){
            DefaultScenarios defaultSce = new DefaultScenarios(i);
            defaultSce.save();
            ScenarioAlarmDB defaultAlarm = new ScenarioAlarmDB(i);
            defaultAlarm.save();
        }
    }

    /*
     * namesReady() adds scenario names to the grid view of the layouts
     */
    public void namesReady(List<String> names){
        // set the layout's gridview by GridButtonAdapter
        gridView = (GridView)findViewById(R.id.scenarioView);
        gridView.setAdapter(new GridButtonAdapter(this, names));
    }

    /*
     * GridButtonAdapter set the content of the gridview with dynamic scenario names and numbers
     */
    public class GridButtonAdapter extends BaseAdapter{
        // names is a Arraylist containing Strings of scenario names
        private List<String> names;
        private Context context;

        public GridButtonAdapter(Context context, List<String> names){
            this.context = context;
            this.names = names;
        }


        @Override
        public int getCount() {
            return names.size()+1;
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
            // add an add sign "+" to position 0
            if(position==0){
                Button addButton;
                if (convertView == null) {
                    addButton = new Button(context);
                    addButton.setLayoutParams(new GridView.LayoutParams(550, 280));
                    addButton.setPadding(8, 8, 8, 8);
                } else {
                    addButton = (Button) convertView;
                }
                addButton.setText("+");
                addButton.setTextSize(40);
                addButton.setBackgroundColor(Color.LTGRAY);
                addButton.setId(position);
                // set OnClickLinstener for each item on the grid view
                addButton.setOnClickListener(new AddScenarioOnClickListener(position));
                return addButton;

             /*
                ImageView addImage;
                if(convertView == null) {
                    addImage = new ImageView(context);
                    addImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    addImage.setLayoutParams(new LinearLayoutCompat.LayoutParams(450,280));
                    addImage.setPadding(8, 8, 8, 8);
                }else{
                    addImage = (ImageView) convertView;
                }
                addImage.setImageResource(R.drawable.add);
                addImage.setBackgroundColor(Color.LTGRAY);
                // set OnClickListener for the add button
                addImage.setOnClickListener(new AddScenarioOnClickListener(position));
                return addImage;*/
            }
            // add scenario names to other positions
            else {
                Button sceButton;
                if (convertView == null) {
                    sceButton = new Button(context);
                    sceButton.setLayoutParams(new GridView.LayoutParams(550, 280));
                    sceButton.setPadding(8, 8, 8, 8);
                } else {
                    sceButton = (Button) convertView;
                }
                sceButton.setText(names.get(position-1));
                sceButton.setTextSize(20);
                sceButton.setBackgroundColor(Color.LTGRAY);
                sceButton.setId(position);
                // set OnClickLinstener for each item on the grid view
                sceButton.setOnClickListener(new ScenarioOnClickListener(position));
                return sceButton;
            }
        }
    }

    /*
     * ScenarioOnclickListener directs user to the item list page of the scenario they select
     */
    private class ScenarioOnClickListener implements View.OnClickListener{
        int sID;
        public ScenarioOnClickListener(int sID){
            this.sID = sID;
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            System.out.println(sID);

            // jump to the selected default scenario
            Intent intent = new Intent(HomeActivity.this,DefaultListActivity.class);
            intent.putExtra("sID",String.valueOf(sID));
            startActivity(intent);
        }
    }

    /*
     * AddScenarioOnclickListener directs user to the process of creating their customised scenario
     */
    private class AddScenarioOnClickListener implements View.OnClickListener{
        int sID;
        public AddScenarioOnClickListener(int sID){
            this.sID = sID;
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test
            v.setBackgroundColor(Color.BLUE);
            System.out.println(sID);
            // the following code is just for notification set test
            NotificationReceiver.updateNotification("Title","Notification Content");
            setNotificationAlarm(16, 6, 50, true);
        }
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_alarm, menu);
        return true;
    }


}