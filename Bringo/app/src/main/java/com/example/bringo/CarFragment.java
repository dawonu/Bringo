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

public class CarFragment extends Fragment{

    private EditText dtimeHour;
    private EditText dtimeMinute;

    private EditText rtimeHour;
    private EditText rtimeMinute;

    public CarFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);

        dtimeHour = (EditText) view.findViewById(R.id.dtime_hour);
        dtimeMinute = (EditText) view.findViewById(R.id.dtime_minute);
        rtimeHour = (EditText) view.findViewById(R.id.rtime_hour);
        rtimeMinute = (EditText) view.findViewById(R.id.rtime_minute);

        return view;
    }

    public String getDepartureTime() {
        return String.valueOf(dtimeHour.getText()) + ":" + String.valueOf(dtimeMinute.getText());
    }

    public String getReturnTime() {
        return String.valueOf(rtimeHour.getText()) + ":" + String.valueOf(rtimeMinute.getText());
    }
}
