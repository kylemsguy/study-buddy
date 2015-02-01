package com.kylemsguy.studybuddy;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kylemsguy.studybuddy.backend.GPSTracker;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnShowLocation;
    private Button btnCourses;
    private boolean flag = false;

    AsyncTask getcalendars = null;

    //Tracker class for gps
    GPSTracker gps;

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final String USERINFO_SCOPE =
            "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private static final String GCAL_SCOPE =
            "https://www.googleapis.com/auth/calendar.readonly"; // only need read-only
    private static final String mScopes
            = "oauth2:" + USERINFO_SCOPE + " " + GCAL_SCOPE;

    private String mEmail = null; // Received from newChooseAccountIntent(); passed to getToken()
    ConnectivityManager cm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        btnCourses = (Button) findViewById(R.id.courses);
        btnCourses.setOnClickListener((View.OnClickListener) this);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude +
                            "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getCalendarList();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
        // Later, more code will go here to handle the result from some exceptions...
    }

    public void getCalendarList() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                getcalendars = new ListCalendarsTask(MainActivity.this, mEmail, mScopes).execute();
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isDeviceOnline() {
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else {
            return true;
        }
    }

    public void loginButtonHandler(View view) {
        if (mEmail == null) {
            pickUserAccount();
        }
    }

    public void handleException(Exception e) {
        e.printStackTrace();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
        if (v == btnCourses &&  flag){
            System.out.println("courses and registerd");
            Intent intent = new Intent(this, CourseActivity.class);
            startActivityForResult(intent, 0);
        }
        if (v == btnCourses && !flag){
            System.out.println("courses and not registered");
            Toast.makeText(getApplicationContext(), "You need to register to start!!!!!!!", Toast.LENGTH_LONG).show();
        }
    }
}
