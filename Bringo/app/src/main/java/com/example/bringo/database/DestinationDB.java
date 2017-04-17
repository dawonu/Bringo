package com.example.bringo.database;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by huojing on 4/17/17.
 */

public class DestinationDB extends SugarRecord{
    private int id;

    private String destination;

    private Calendar departureDate;

    private Calendar returnDate;

    private List<String> departureFlightInfo;

    private List<String> returnFlightInfo;

    private List<String> departureTrainInfo;

    private List<String> returnTrainInfo;

    private String departureCarInfo;

    private String returnCarInfo;

    private boolean workOrLeisure;

    // not sure
    private Map<Integer, String> items;

    public DestinationDB() {}

    public DestinationDB(int id) {
        this.id = id;
    }

    public void setDestination(String destination) {
        if (destination == null || destination.trim().length() == 0) {
            return;
        }
        this.destination = destination;
    }
    public String getDestination() {
        return destination;
    }

    public int getID() {
        return id;
    }

    public void setDepartureDate(String month, String day) {
        if (month == null || month.trim().length() == 0) {
            return;
        }
        if (day == null || day.trim().length() == 0) {
            return;
        }
        int m, d;
        try {
            m = Integer.parseInt(month);
            d = Integer.parseInt(day);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        departureDate = new GregorianCalendar(2017, m-1, d,0,0,0);
    }
    public String getDepartureDate() {
        if (departureDate == null) {
            return null;
        }
        return departureDate.get(Calendar.MONTH) + 1 + "/" + departureDate.get(Calendar.DAY_OF_MONTH);
    }

    public void setReturnDate(String month, String day) {
        if (month == null || month.trim().length() == 0) {
            return;
        }
        if (day == null || day.trim().length() == 0) {
            return;
        }
        int m, d;
        try {
            m = Integer.parseInt(month);
            d = Integer.parseInt(day);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        returnDate = new GregorianCalendar(2017, m-1, d,0,0,0);
    }
    public String getReturnDate() {
        if (returnDate == null) {
            return null;
        }
        return returnDate.get(Calendar.MONTH) + 1 + "/" + returnDate.get(Calendar.DAY_OF_MONTH);
    }

    public void addDepartureFlightInfo(String numFlight, String airport, String time) {
        if (numFlight == null || airport == null || time == null) {
            return;
        }
        if (departureFlightInfo == null) {
            departureFlightInfo = new LinkedList<>();
        }
        departureFlightInfo.add(numFlight + "," + airport + "," + time);
    }

    // id is the index of the transfer flight
    private String[] getDepartureFlightInfo(int id) {
        String infoAll = departureFlightInfo.get(id);
        String[] info = infoAll.split(",");
        return info;
    }

    public String getDepatureFlightNum(int id) {
        try {
            return getDepartureFlightInfo(id)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDepartureAirport(int id) {
        try {
            return getDepartureFlightInfo(id)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDepartureFlightTime(int id) {
        try {
            return getDepartureFlightInfo(id)[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addReturnFlightInfo(String numFlight, String airport, String time) {
        if (numFlight == null || airport == null || time == null) {
            return;
        }
        if (returnFlightInfo == null) {
            returnFlightInfo = new LinkedList<>();
        }
        returnFlightInfo.add(numFlight + "," + airport + "," + time);
    }

    // id is the index of the transfer flight
    private String[] getReturnFlightInfo(int id) {
        String infoAll = returnFlightInfo.get(id);
        String[] info = infoAll.split(",");
        return info;
    }

    public String getReturnFlightNum(int id) {
        try {
            return getReturnFlightInfo(id)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReturnAirport(int id) {
        try {
            return getReturnFlightInfo(id)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReturnFlightTime(int id) {
        try {
            return getReturnFlightInfo(id)[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addDepartureTrainInfo(String location, String time) {
        if (location == null || time == null) {
            return;
        }
        if (departureTrainInfo == null) {
            departureTrainInfo = new LinkedList<>();
        }
        departureTrainInfo.add(location + "," + time);
    }

    // id is the index of the transfer flight
    private String[] getDepartureTrainInfo(int id) {
        String infoAll = departureTrainInfo.get(id);
        String[] info = infoAll.split(",");
        return info;
    }

    public String getDepartureTrainStation(int id) {
        try {
            return getDepartureTrainInfo(id)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDepartureTrainNum(int id) {
        try {
            return getDepartureTrainInfo(id)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addReturnTrainInfo(String location, String time) {
        if (location == null || time == null) {
            return;
        }
        if (returnTrainInfo == null) {
            returnTrainInfo = new LinkedList<>();
        }
        returnTrainInfo.add(location + "," + time);
    }

    // id is the index of the transfer flight
    private String[] getReturnTrainInfo(int id) {
        String infoAll = returnTrainInfo.get(id);
        String[] info = infoAll.split(",");
        return info;
    }
    public String getReturnTrainStation(int id) {
        try {
            return getReturnTrainInfo(id)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getReturnTrainNum(int id) {
        try {
            return getReturnTrainInfo(id)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setDepartureCarInfo(String time) {
        if (time == null) {
            return;
        }
        departureCarInfo = time;
    }
    public String getDepartureCarInfo() {
        return departureCarInfo;
    }

    public void setReturnCarInfo(String time) {
        if (time == null) {
            return;
        }
        returnCarInfo = time;
    }
    public String getReturnCarInfo() {
        return returnCarInfo;
    }

    // work true, leisure false
    public void setWorkOrLeisure(boolean choice) {
        workOrLeisure = choice;
    }
    public boolean getWorkOrLeisure() {
        return workOrLeisure;
    }

}
