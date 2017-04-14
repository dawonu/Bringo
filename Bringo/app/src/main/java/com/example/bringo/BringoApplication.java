package com.example.bringo;

import android.content.res.Configuration;

import com.orm.SchemaGenerator;
import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;

/**
 * Created by alisonwang on 4/10/17.
 */

public class BringoApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(getApplicationContext());
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
    }
}
