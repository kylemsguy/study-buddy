package com.kylemsguy.studybuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kylemsguy.studybuddy.backend.ConnectionManager;

import java.util.List;


public class ChatActivity extends ActionBarActivity {

    ConnectionManager cm;
    List<Integer> nearbyUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        cm = new ConnectionManager();
        nearbyUsers = getIntent().getExtras().getIntegerArrayList("server_ids");
        SharedPreferences prefs = getSharedPreferences(ChatActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        try {
            String convo = cm.startConvo("Conversation");
            cm.addUsertoConvo(convo, nearbyUsers[0]);
            cm.addUsertoConvo(convo, prefs.getString("server_id", null));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
