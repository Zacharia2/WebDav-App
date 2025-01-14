package xyz.realms.mws;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MwsApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
