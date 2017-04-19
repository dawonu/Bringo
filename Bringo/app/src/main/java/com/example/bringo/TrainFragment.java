package com.example.bringo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by huojing on 4/18/17.
 */

public class TrainFragment extends Fragment{

    private EditText dstation;
    private EditText dtimeHour;
    private EditText dtimeMinute;

    private EditText rstation;
    private EditText rtimeHour;
    private EditText rtimeMinute;

    public TrainFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train, container, false);

        dstation = (EditText) view.findViewById(R.id.dstation);
        dtimeHour = (EditText) view.findViewById(R.id.dtime_hour);
        dtimeMinute = (EditText) view.findViewById(R.id.dtime_minute);

        rstation= (EditText) view.findViewById(R.id.rstation);
        rtimeHour = (EditText) view.findViewById(R.id.rtime_hour);
        rtimeMinute = (EditText) view.findViewById(R.id.rtime_minute);

        return view;
    }

    public String getDepartureStation() {
        return String.valueOf(dstation.getText());
    }

    public String getDepartureTime() {
        return String.valueOf(dtimeHour.getText()) + ":" + String.valueOf(dtimeMinute.getText());
    }

    public String getReturnStation() {
        return String.valueOf(rstation.getText());
    }

    public String getReturnTime() {
        return String.valueOf(rtimeHour.getText()) + ":" + String.valueOf(rtimeMinute.getText());
    }
}
