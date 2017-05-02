package com.example.bringo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bringo.database.DestinationDB;
import com.example.bringo.database.TravelCheckItemsDB;
import com.example.bringo.database.TravelUserInputDB;

import java.util.List;

public class ViewDestinationActivity extends AppCompatActivity {

    private int dID;
    private TextView chart;
    private TextView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination);

        dID = Integer.valueOf(getIntent().getStringExtra("dID"));
        System.out.println("passed value did " + dID);
        // get scenario name from DefaultScenarios DB
        List<DestinationDB> dss = DestinationDB.find(DestinationDB.class,"did = ?",String.valueOf(dID));
        DestinationDB ds = dss.get(0);
        String dName = ds.getDestination();
        System.out.println("destination " + dName);
        System.out.println("true did " + String.valueOf(ds.getDesID()));

        StringBuilder sb = new StringBuilder();
        List<TravelCheckItemsDB> allItems = TravelCheckItemsDB.listAll(TravelCheckItemsDB.class);
        System.out.println("all items " + allItems);
        List<TravelCheckItemsDB> items = TravelCheckItemsDB.find(TravelCheckItemsDB.class,"destination_id = ?",String.valueOf(ds.getDesID()));
        for (TravelCheckItemsDB item: items) {
            System.out.println("items: "+item.getName());
            int itemID = item.getItemID();
            if (itemID < 1000) {
                String itemName = item.getName();
                sb.append(itemName).append("\n");
            } else {
                List<TravelUserInputDB> inputItems = TravelUserInputDB.find(TravelUserInputDB.class, "item_id = ?", String.valueOf(itemID));
                String itemName = inputItems.get(0).getItemName();
                sb.append(itemName).append("\n");
            }
        }
        System.out.println("LIST: "+sb.toString());

        list = (TextView) findViewById(R.id.content);
        list.setText(sb.toString());

        StringBuilder info = new StringBuilder();
        info.append("destination: ").append(ds.getDestination()).append("\n");
        info.append("departure date: ").append(ds.getDepartureDate()).append("\n");
        info.append("return date: ").append(ds.getReturnDate()).append("\n");

        info.append("departure flight: ").append(ds.getDepatureFlightNum(0)).append("\n");
        info.append("departure airport: ").append(ds.getDepartureAirport(0)).append("\n");
        info.append("departure flight time: ").append(ds.getDepartureFlightTime(0)).append("\n");
        info.append("return flight: ").append(ds.getReturnFlightNum(0)).append("\n");
        info.append("return airport: ").append(ds.getReturnAirport(0)).append("\n");
        info.append("return flight time: ").append(ds.getReturnFlightTime(0)).append("\n");

        info.append("departure train: ").append(ds.getDepartureTrainStation(0)).append("\n");
        info.append("return train: ").append(ds.getReturnTrainStation(0)).append("\n");

        info.append("departure car:").append(ds.getDepartureCarInfo()).append("\n");
        info.append("return car:").append(ds.getReturnCarInfo()).append("\n");

        System.out.println(info.toString());
        chart = (TextView) findViewById(R.id.information);
        chart.setText(info.toString());
    }
}
