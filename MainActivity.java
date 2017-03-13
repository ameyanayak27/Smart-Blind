package com.example.nayak.smartblindsystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*
Created by Ameya nayak and Rajesh Shetty
Displays the weather details as per pi readings
 */

public class MainActivity extends ActionBarActivity {
    TextView timeField;
    TextView ambienceField;
    TextView temperatureField;
    TextView statusField;
    public Double temp;
    public String notification_condition;
    public Bundle p;
    double temperature_previous = -100.0;
    ArrayList<String> notificationsList = new ArrayList<String>();
    TextView spinnerText;
    ArrayAdapter<CharSequence> triggerAdapter;
    Spinner manualTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p  = this.getIntent().getExtras();
        Firebase.setAndroidContext(this);
        Firebase connection = new Firebase("https://scorching-torch-3800.firebaseio.com/");
        connection.child("Notification_temperature").setValue("2");
        connection.child("Notification_condition").setValue("up");
        setContentView(R.layout.activity_main);
        timeField = (TextView)findViewById(R.id.time_field);
        statusField = (TextView)findViewById(R.id.status_field);
        ambienceField = (TextView)findViewById(R.id.ambience_field);
        temperatureField = (TextView)findViewById(R.id.temperature_field);
        //Set the spinner layouts textview
        manualTrigger = (Spinner) findViewById(R.id.manual_spinner);
        triggerAdapter = ArrayAdapter.createFromResource(this, R.array.manual_trigger, R.layout.spinner_textview_layout);
        triggerAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        manualTrigger.setAdapter(triggerAdapter);
        //if the user selects a value from manual trigger
        manualTrigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Firebase connection = new Firebase("https://scorching-torch-3800.firebaseio.com/");
                connection.child("Manual_Trigger").setValue(manualTrigger.getSelectedItem().toString());
                connection.child("TimeStamp").setValue(new Date().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //run renderWeather method in a separate thread
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    renderWeather();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

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

    //set the values based on the firebase db data
    private void renderWeather(){
        //establish connection to firebase db
        Firebase connection = new Firebase("https://scorching-torch-3800.firebaseio.com/");
        connection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //get all the values
                notification_condition = (String) snapshot.child("Notification_condition").getValue();
                temp = Double.valueOf((String) snapshot.child("Notification_temperature").getValue());
                String temperature = (String) snapshot.child("Temperature").getValue();
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.main);
                String manual_trigger = (String) snapshot.child("Manual_Trigger").getValue();
                String ambience = (String) snapshot.child("Ambient_Status").getValue();
                String blind = (String) snapshot.child("Blinds").getValue();
                String timestamp = (String) snapshot.child("TimeStamp").getValue();
                //if blind status is to be changed manually
                if (!manual_trigger.equals("manual off"))
                    blind = manual_trigger;
                //Based on the ambience change background and text colour
                switch (ambience) {
                    case "Bright":
                        layout.setBackgroundResource(0);
                        layout.setBackgroundResource(R.drawable.bright);
                        timeField.setTextColor(Color.BLACK);
                        ambienceField.setTextColor(Color.BLACK);
                        temperatureField.setTextColor(Color.BLACK);
                        statusField.setTextColor(Color.BLACK);
                        break;
                    case "Dim":
                        layout.setBackgroundResource(0);
                        layout.setBackgroundResource(R.drawable.dim);
                        break;
                    case "Dark":
                        layout.setBackgroundResource(0);
                        layout.setBackgroundResource(R.drawable.dark);
                        timeField.setTextColor(Color.WHITE);
                        ambienceField.setTextColor(Color.WHITE);
                        temperatureField.setTextColor(Color.WHITE);
                        statusField.setTextColor(Color.WHITE);
                }
                statusField.setText("Blind Status: " + blind);
                timeField.setText(timestamp);
                ambienceField.setText(ambience);
                double temperature_toDouble = Double.valueOf(temperature);
                //change the display temperature and add to the notification arraylist only if conditions match
                if ((notification_condition.equals("up") && (temperature_toDouble - temperature_previous ) >= temp) || (notification_condition.equals("down") && (temperature_previous - temperature_toDouble) >= temp)) {
                    System.out.println("in " + temperature);
                    temperature_previous = temperature_toDouble;
                    notificationsList.add(timestamp + ", " + temperature );
                    temperatureField.setText(temperature + " C");
                    setNotification("SmartBlind Notification Temperature Change ", timestamp + " " + temperature);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    //start ModifyRulesActivity when the user clicks on Add rules

    public void modifyRules(View view) {
        Intent intent = new Intent(this, ModifyRulesActivity.class);
        String x = "Add";
        intent.putExtra("button", x);
        startActivity(intent);
    }

    //start ListRulesActivity when the user clicks on List rules
    public void listRules(View view){
        Intent intent = new Intent(this, ListRulesActivity.class);
        startActivity(intent);
    }


    //update notification rules whent the user clicks on Add Notification Rule
    public void addNRules(View view) {
        Intent intent = new Intent(this, Notification_Rule.class);
        intent.putExtra("condition", notification_condition);
        intent.putExtra("temperature", ""+temp);
        startActivity(intent);
    }

    public void notification(View view) {
        Intent intent = new Intent(this, Notifications.class);
        Collections.reverse(notificationsList); //to keep latest updates first
        intent.putStringArrayListExtra("notification", notificationsList );
        Collections.reverse(notificationsList);//to revert  the previous reverse
        startActivity(intent);
    }

    //add notifications based on notification rules
    public void setNotification(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher,"New Notification", System.currentTimeMillis());
        Intent intent = new Intent(this,Notifications.class);
        Collections.reverse(notificationsList); //to keep latest updates first
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putStringArrayListExtra("notification", notificationsList);
        Collections.reverse(notificationsList);//to revert  the previous reverse
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT); //FLAG_UPDATE_CURRENT allows to pass extra whch will be recognized by Notifications Activity
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //so that notification is removed on click
        notification.setLatestEventInfo(MainActivity.this, notificationTitle,notificationMessage, pending);
        notificationManager.notify(0, notification);
    }

}
