package com.kylemsguy.studybuddy;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.kylemsguy.studybuddy.backend.ListCalendarsTask;


public class MainActivity extends ActionBarActivity {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final String USERINFO_SCOPE =
            "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private static final String GCAL_SCOPE =
            "https://www.googleapis.com/auth/calendar.readonly"; // only need read-only
    private static final String mScopes
            = "oauth2:" + USERINFO_SCOPE + " " + GCAL_SCOPE;

    private String mEmail = null; // Received from newChooseAccountIntent(); passed to getToken()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void getCalendarList(){
        if(mEmail == null){
            pickUserAccount();
        } else {
            if(isDeviceOnline()){
                new ListCalendarsTask()
            }
        }
    }

    public void loginButtonHandler(View view){
        if(mEmail == null){
            pickUserAccount();
        }
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
}
