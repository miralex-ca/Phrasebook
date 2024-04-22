package com.online.languages.study.lang;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();

        App.context = getApplicationContext();
        appContainer = new AppContainer(getApplicationContext());
    }

    public static Context getAppContext() {
        return App.context;
    }

}
