package com.kylemsguy.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;


import android.widget.TextView;
import android.widget.Toast;

import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import com.kylemsguy.studybuddy.backend.ConnectionManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CourseActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button zero;

    private String SENDER_ID = "505286379749";
    private ConnectionManager cm;


    /**
     * Tag used on log messages.
     */
    static final String TAG = "SB_GCM";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    List<Event> events = null;

    public void getCalendarEvents(){
        events = ((SBApp) getApplication()).getEvents();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        context = getApplicationContext();
        cm = new ConnectionManager();


        //button
        getCalendarEvents();
        Button one = (Button) findViewById(R.id.one);
        one.setText("CSC324");
        one.setOnClickListener((View.OnClickListener) this);
        Button four = (Button) findViewById(R.id.four);
        four.setText("CSC411");
        four.setOnClickListener((View.OnClickListener) this);
        Button two = (Button) findViewById(R.id.two);
        two.setText("CSC384");
        two.setOnClickListener((View.OnClickListener) this);
        Button three = (Button) findViewById(R.id.three);
        three.setText("CSC336");
        three.setOnClickListener((View.OnClickListener) this);
        Button zero = (Button) findViewById(R.id.zero);
        zero.setText("CSC343");
        zero.setOnClickListener((View.OnClickListener) this);










        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                // uncomment for debug
                //System.out.println(regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            Log.i(TAG, "Not changing regID because server will break. TODO: fix.");
            //return "";
        }
        return registrationId;
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(CourseActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // now register with server
                    try {
                        registerClientServer();
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                System.out.println(msg);
            }
        }.execute(null, null, null);
    }

    private void registerClientServer() throws Exception{
        System.out.println(regid);
        cm.register("username", "email", regid);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_acticity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == one ){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivityForResult(intent, 0);
        }
        if (v == two ){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivityForResult(intent, 0);
        }
        if (v == three ){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivityForResult(intent, 0);
        }
        if (v == four ){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivityForResult(intent, 0);
        }
        if (v == zero ){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivityForResult(intent, 0);
        }
    }

}
