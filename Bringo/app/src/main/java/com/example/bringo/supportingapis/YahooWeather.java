package com.example.bringo.supportingapis;

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
 * Created by huojing on 4/7/17.
 * Reference: Youtube Video https://www.youtube.com/watch?v=gJ9Ny_J3tcM
 *
 * how to use this class, eg:
 * YahooWeather weather = new YahooWeather();
 * weather.refreshWeather("pittsburgh"); -> argument is the location;
 * String condition = weather.getWeather("07 Apr 2017"); -> argument is the date, in this format.
 *
 */

public class YahooWeather {

    private Exception error;

    private Map<String, String> weatherMap = new HashMap<>();

    public void refreshWeather(String location) {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String[] locations) {
                String location = locations[0];
                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")", location);
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));
            try {
                URL url = new URL(endpoint);

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

                System.out.println("result:" + result.toString());

                JSONObject data = new JSONObject(result.toString());

                JSONObject queryResults = data.optJSONObject("query");
                System.out.println("query:" + queryResults);

                int count = queryResults.optInt("count");
                System.out.println("count:" + count);

                if (count == 0) {
                    error = new LocationWeatherException("No weather information found for " + location);
                    return null;
                }

                JSONArray forecastJSON = queryResults.optJSONObject("results").optJSONObject("channel")
                        .optJSONObject("item").optJSONArray("forecast");
                System.out.println("forecast:" + forecastJSON.get(0));
                for (int i = 0; i < forecastJSON.length(); i++) {
                    JSONObject temp = forecastJSON.getJSONObject(i);
                    String date = temp.getString("date");
                    String condition = temp.getString("text");
                    System.out.println("date:" + date);
                    System.out.println("condition:" + condition);
                    weatherMap.put(date, condition);
                }

                return null;

            } catch (Exception e) {
                error = e;
                error.printStackTrace();
            }
                return null;
            }
        }.execute(location);
    }

    public String getWeather(String date) {
        return weatherMap.get(date);
    }

    private class LocationWeatherException extends Exception {
        LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }

}
