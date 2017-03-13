package com.example.nayak.smartblindsystem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Created by Ameya nayak and Rajesh Shetty
Displays a listview of all notifications
 */

public class Notifications extends ActionBarActivity {
    ArrayList<Map<String,String>> notifications = new ArrayList<Map<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //get the list if notifications from the previous activity
        Bundle p  = this.getIntent().getExtras();
        ArrayList<String> notify = p.getStringArrayList("notification");
        ListView listView = (ListView) findViewById(R.id.notification);
        //create an arraylist of map for simple adapter
        for(int i = 0; i<notify.size();i++)
            notifications.add(createNotification("notification", notify.get(i)));
        SimpleAdapter adapter = new SimpleAdapter(this, notifications, android.R.layout.simple_list_item_1, new String[] {"notification"}, new int[] {android.R.id.text1});
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    //Returns a map which is inserted into notifications
    private HashMap<String, String> createNotification(String key, String name) {
        HashMap<String, String> notify = new HashMap<String, String>();
        notify.put(key, name);
        return notify;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
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
