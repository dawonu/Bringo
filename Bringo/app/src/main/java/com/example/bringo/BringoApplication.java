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
        System.out.println("FUCK YOU ON THE FACE AND EAT PIG SHIT");

        super.onCreate();

        //SugarContext.init(getApplicationContext());
        SugarContext.init(this);
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
