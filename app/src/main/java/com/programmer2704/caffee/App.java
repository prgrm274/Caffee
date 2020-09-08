package com.programmer2704.caffee;

import android.app.Application;

import com.programmer2704.caffee.constants.PrintKeyhash;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //mandatory
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //return sekali aja untuk generate key
        PrintKeyhash.print(this);
    }
}
