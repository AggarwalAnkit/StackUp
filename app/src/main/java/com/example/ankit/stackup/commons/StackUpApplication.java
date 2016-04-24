package com.example.ankit.stackup.commons;

import android.app.Application;
import android.content.Context;

public class StackUpApplication extends Application {

    public static Context APP_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;
    }
}
