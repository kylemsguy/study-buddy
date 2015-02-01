package com.kylemsguy.studybuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.List;

/**
 * Created by kyle on 31/01/15.
 */
public class ListCalendarsTask extends AsyncTask<Void, Void, List<CalendarListEntry>> {
    Activity mActivity;
    String mScope;
    String mEmail;

    ListCalendarsTask(Activity activity, String name, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected List<CalendarListEntry> doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                // Insert the good stuff here.
                // Use the token to access the user's Google data.
                Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credentials)
                        .setApplicationName("StudyBuddy").build();
                String pageToken = null;
                CalendarList calendarList = service.calendarList().list()
                        .setPageToken(pageToken).execute();
                List<CalendarListEntry> items = calendarList.getItems();
                return items;
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            // TODO something went wrong q_q
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            //mActivity.handleException(userRecoverableException);
            AlertDialog dialog = new AlertDialog.Builder(mActivity)
                    .setTitle("Google Play Services Error")
                    .setMessage("Please install/update Google Play Services.")
                    .create();
            dialog.show();
            userRecoverableException.printStackTrace();
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            // TODO implement. RIP
            fatalException.printStackTrace();
        }
        return null;
    }

}