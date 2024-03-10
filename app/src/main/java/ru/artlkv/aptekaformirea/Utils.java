package ru.artlkv.aptekaformirea;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import rx.Observable;

public class Utils {
    public static Observable<Void> isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Application.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return Observable.create(subscriber -> {
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Throwable("Network not connected"));
            }
        });
    }
}
