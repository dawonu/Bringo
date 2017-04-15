package com.example.bringo;
import android.content.Context;
import android.content.SharedPreferences;


public class AppSharedPreferences {
    private static String inquestPreferences = "BringoPreferences";

    public static SharedPreferences.Editor editor(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(inquestPreferences, Context.MODE_PRIVATE);

        return sharedpreferences.edit();
    }

    public static SharedPreferences getSharedPreferences(Context context) {

        return context.getSharedPreferences(inquestPreferences, Context.MODE_PRIVATE);
    }

}
