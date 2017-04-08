package com.example.bringo.supportingapis;

import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by huojing on 4/7/17.
 * reference: https://github.com/DigitalPhantom/PhantomWeatherAndroid/blob/master/app/src/main/java/net/digitalphantom/app/weatherapp/service/GoogleMapsGeocodingService.java
 */

public class GoogleGeocoding {

    private static final String API_KEY = "";

    private Exception error;

    private String address;

    public void refreshLocation(Location location) {
        new AsyncTask<Location, Void, Void>() {

            @Override
            protected Void doInBackground(Location... locations) {
                Location location = locations[0];
                System.out.println("refreshing location:" + location);

                String endpoint = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s", location.getLatitude(), location.getLongitude(), API_KEY);
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

                    JSONObject data = new JSONObject(result.toString());
//                    System.out.println("data:" + data);

                    JSONArray results = data.optJSONArray("results");
//                    System.out.println("results:" + results);

                    if (results.length() == 0) {
                        error = new ReverseGeolocationException("Could not reverse geocode " + location.getLatitude() + ", " + location.getLongitude());
                    }
//                    System.out.println("results2:" + results.optJSONObject(0));
//                    System.out.println("results3:" + results.optJSONObject(0).optString("address_components"));
//                    System.out.println("results3:" + results.optJSONObject(0).optString("formatted_address"));
                    address = results.optJSONObject(0).optString("formatted_address");

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            }.execute(location);
        }


    public String getAddress() {
        return address;
    }

    private class ReverseGeolocationException extends Exception {
        public ReverseGeolocationException(String detailMessage) {
            super(detailMessage);
        }
    }


}
