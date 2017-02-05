package com.scorelab.kute.kute;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by nrv on 2/5/17.
 */

public class KuteApplication extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //Previous versions of Firebase


        //Newer version of Firebase
        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }

}
