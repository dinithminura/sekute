package com.scorelab.kute.kute;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scorelab.kute.kute.Activity.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new DelayTask().execute(); // This will delay the spalsh scrren and redierct to the login/register screen based
        //on the ststus of the user.
    }

    class DelayTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(4000);
            }
            catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences sharedPref =getPreferences(Context.MODE_PRIVATE);
            String userName = getResources().getString(R.string.sharedprefrenceusername);

            if(userName==null){ //User is not registered in the system.

                Intent regIntent =new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
            else{ //User registered in the system.

                Intent regIntent =new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }



        }
    }
}
