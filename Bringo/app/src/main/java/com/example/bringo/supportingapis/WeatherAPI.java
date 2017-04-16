package com.example.bringo.supportingapis;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jing Huo on 4/16/17.
 */

public class WeatherAPI {

    private static final String API_KEY = "";

    private Exception error;

    private String address;

    private Map<String, String> weatherMap = new HashMap<>();

    public void refreshWeather(Location location) {
        new AsyncTask<Location, Void, Void>() {

            @Override
            protected Void doInBackground(Location... locations) {
                Location location = locations[0];
                System.out.println("refreshing location:" + location);

                String endpoint1 = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s", location.getLatitude(), location.getLongitude(), API_KEY);
                try {
                    URL url = new URL(endpoint1);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();

                    JSONObject data = new JSONObject(result.toString());
//                    System.out.println("data:" + data);

                    JSONArray results = data.optJSONArray("results");
//                    System.out.println("results:" + results);

                    if (results.length() == 0) {
                        error = new WeatherAPI.ReverseGeolocationException("Could not reverse geocode " + location.getLatitude() + ", " + location.getLongitude());
                    }
//                    System.out.println("results2:" + results.optJSONObject(0));
//                    System.out.println("results3:" + results.optJSONObject(0).optString("address_components"));
//                    System.out.println("results3:" + results.optJSONObject(0).optString("formatted_address"));
                    address = results.optJSONObject(0).optString("formatted_address");

                } catch (Exception e) {
                    error = e;
                }

                //sleep 1000

                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")", address);
                String endpoint2 = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));
                try {
                    URL url = new URL(endpoint2);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();

//                System.out.println("result:" + result.toString());

                    JSONObject data = new JSONObject(result.toString());

                    JSONObject queryResults = data.optJSONObject("query");
//                System.out.println("query:" + queryResults);

                    int count = queryResults.optInt("count");
//                System.out.println("count:" + count);

                    if (count == 0) {
                        error = new WeatherAPI.LocationWeatherException("No weather information found for " + location);
                        return null;
                    }

                    JSONArray forecastJSON = queryResults.optJSONObject("results").optJSONObject("channel")
                            .optJSONObject("item").optJSONArray("forecast");
//                System.out.println("forecast:" + forecastJSON.get(0));
                    if (forecastJSON.length() >= 2) {
                        weatherMap.put("today", forecastJSON.getJSONObject(0).getString("text"));
                        weatherMap.put("tomorrow", forecastJSON.getJSONObject(1).getString("text"));
                    }
                    for (int i = 2; i < forecastJSON.length(); i++) {
                        JSONObject temp = forecastJSON.getJSONObject(i);
                        String date = temp.getString("date");
                        String condition = temp.getString("text");
//                    System.out.println("date:" + date);
//                    System.out.println("condition:" + condition);
                        weatherMap.put(date, condition);
                    }
                    System.out.println("weather data:" + weatherMap);
                    return null;

                } catch (Exception e) {
                    error = e;
                    error.printStackTrace();
                }
                return null;
            }
        }.execute(location);
    }


    public String getAddress() {
        return address;
    }

    public String getWeather(String date) {
        return weatherMap.get(date);
    }

    public String getTodayWeather() {
        return weatherMap.get("today");
    }

    public String getTomorrowWeather() {
        return weatherMap.get("tomorrow");
    }

    private class ReverseGeolocationException extends Exception {
        public ReverseGeolocationException(String detailMessage) {
            super(detailMessage);
        }
    }

    private class LocationWeatherException extends Exception {
        LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }
}
