package com.laioffer.matrix;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.messaging.FirebaseMessaging;

public class MatrixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        FirebaseMessaging.getInstance().subscribeToTopic("android");
    }

}

