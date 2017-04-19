package com.example.bringo;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bringo.database.DestinationDB;

import java.util.List;

public class CreateDestination1Activity extends AppCompatActivity {

    // tool bar and tabs
    private Toolbar myToolbar;
    private ViewPager mVpContainer;
    private TabLayout mTvTabs;

    // UI components
    private List<DestinationDB> list = DestinationDB.listAll(DestinationDB.class);
    private DestinationDB dDB = list.get(list.size() - 1);
    private EditText destination;
    private EditText departureMonth;
    private EditText departureDay;
    private EditText returnMonth;
    private EditText returnDay;
    private Button work;
    private Button leisure;
    private TextView message;
    private Button next;

    private int workOrLeisure;

    // tab part
    PlaneFragment planeFragment = new PlaneFragment();
    private Fragment[] mFragments = new Fragment[] {planeFragment, new TrainFragment(), new CarFragment()};
    private String[] mTitles = new String[]{
            "Plane", "Train", "Car"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_destination1);

        myToolbar = (Toolbar) findViewById(R.id.destination_toolbar);
        myToolbar.setTitle("Add destination 1/2");
        setSupportActionBar(myToolbar);

        if (dDB == null) {
            dDB = new DestinationDB();
        }

        destination = (EditText) findViewById(R.id.destination);
        departureMonth = (EditText) findViewById(R.id.departure_month);
        departureDay = (EditText) findViewById(R.id.departure_day);
        returnMonth = (EditText) findViewById(R.id.return_month);
        returnDay = (EditText) findViewById(R.id.return_day);

        work = (Button) findViewById(R.id.work);
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work.setBackgroundColor(Color.GRAY);
                leisure.setBackgroundColor(Color.LTGRAY);
                dDB.setWorkOrLeisure(true);
                dDB.save();
            }
        });
        leisure = (Button) findViewById(R.id.leisure);
        leisure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leisure.setBackgroundColor(Color.GRAY);
                work.setBackgroundColor(Color.LTGRAY);
                dDB.setWorkOrLeisure(false);
                dDB.save();
            }
        });
        message = (TextView) findViewById(R.id.message);
        next = (Button) findViewById((R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // destination
                String d = String.valueOf(destination.getText());
                if( d == null || d.trim().length() == 0 ) {
                    message.setText("Please enter a destination. ");
                } else {
                    dDB.setDestination(d);
                    System.out.println(dDB.getDestination());
                }
                // date
                String dMonth = String.valueOf(departureMonth.getText());
                String dDay = String.valueOf(departureDay.getText());
                dDB.setDepartureDate(dMonth, dDay);
                System.out.println(dDB.getDepartureDate());

                String rMonth = String.valueOf(returnMonth.getText());
                String rDay = String.valueOf(returnDay.getText());
                dDB.setReturnDate(rMonth, rDay);
                System.out.println(dDB.getReturnDate());

//                System.out.println(planeFragment.getDepartureFlightNum());
//                System.out.println(planeFragment.getDepartureAirport());
//                System.out.println(planeFragment.getDepartureTime());

                // flight info
                dDB.addDepartureFlightInfo(planeFragment.getDepartureFlightNum(), planeFragment.getDepartureAirport(),
                        planeFragment.getDepartureTime());
                dDB.addReturnFlightInfo(planeFragment.getReturnFlightNum(), planeFragment.getReturnAirport(),
                        planeFragment.getReturnTime());

                if (message.getText() != null && message.getText().length() == 0) {
                    System.out.println("HERE");
                    dDB.save();
                    // jump to step2
                }
            }
        });

        initView();
    }

    private void initView() {
        mVpContainer = (ViewPager) findViewById(R.id.viewpager);
        mTvTabs = (TabLayout) findViewById(R.id.sliding_tabs);

        mVpContainer.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        mVpContainer.setOffscreenPageLimit(4);
        mTvTabs.setupWithViewPager(mVpContainer);
    }

}
