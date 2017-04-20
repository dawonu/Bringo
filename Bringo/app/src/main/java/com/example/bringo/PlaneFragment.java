package com.example.bringo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bringo.database.DestinationDB;

import java.util.List;

/**
 * Created by huojing on 4/18/17.
 */

public class PlaneFragment extends Fragment {

    public PlaneFragment() {}

    private List<DestinationDB> list = DestinationDB.listAll(DestinationDB.class);
    private DestinationDB dDB = list.get(list.size() - 1);

    private EditText dnumFlight;
    private EditText dairport;
    private EditText dtimeHour;
    private EditText dtimeMinute;

    private EditText rnumFlight;
    private EditText rairport;
    private EditText rtimeHour;
    private EditText rtimeMinute;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plane, container, false);
        dnumFlight = (EditText) view.findViewById(R.id.dflight_no);
        dairport = (EditText) view.findViewById(R.id.dairport);
        dtimeHour = (EditText) view.findViewById(R.id.dtime_hour);
        dtimeMinute = (EditText) view.findViewById(R.id.dtime_minute);

        rnumFlight = (EditText) view.findViewById(R.id.rflight_no);
        rairport = (EditText) view.findViewById(R.id.rairport);
        rtimeHour = (EditText) view.findViewById(R.id.rtime_hour);
        rtimeMinute = (EditText) view.findViewById(R.id.rtime_minute);

        return view;
    }

    public String getDepartureFlightNum() {
        return String.valueOf(dnumFlight.getText());
    }

    public String getDepartureAirport() {
        return String.valueOf(dairport.getText());
    }

    public String getDepartureTime() {
        return String.valueOf(dtimeHour.getText()) + ":" + String.valueOf(dtimeMinute.getText());
    }

    public String getReturnFlightNum() {
        return String.valueOf(rnumFlight.getText());
    }

    public String getReturnAirport() {
        return String.valueOf(rairport.getText());
    }

    public String getReturnTime() {
        return String.valueOf(rtimeHour.getText()) + ":" + String.valueOf(rtimeMinute.getText());
    }
}
