package org.despairs.wmm;

import android.app.Application;
import android.content.Context;

/**
 * Created by Home on 11.04.2017.
 */

public class App extends Application {

    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
    }

}
