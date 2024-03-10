package ru.artlkv.aptekaformirea;

import android.content.Context;

import com.google.firebase.BuildConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;

public class Application extends android.app.Application {
    private static Context globalContext;
    private static FirebaseDatabase globalDatabase;


    @Override
    public void onCreate() {
        super.onCreate();

        globalDatabase = FirebaseDatabase.getInstance();
        globalDatabase.setPersistenceEnabled(true);
        globalContext = this;

        // TODO: Добавить FirebaseDatabase в качестве Backend'а
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(BuildConfig.DEBUG);
    }

    // Позволяет получить доступ к единому глобальному состоянию приложения
    public static Context getGlobalContext() {
        return globalContext;
    }
}
