package com.example.nayak.smartblindsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;

/*
Created by Ameya nayak and Rajesh Shetty
Displays a form where the user can enter notifiaction rule parameters
 */

public class Notification_Rule extends ActionBarActivity {
    Spinner temp_condition;
    EditText temp;
    Bundle p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p  = this.getIntent().getExtras();
        String condition = p.getString("condition");
        String temperature = p.getString("temperature");

        setContentView(R.layout.activity_notification__rule);
        temp_condition = (Spinner) findViewById(R.id.temp_condition);
        temp = (EditText) findViewById(R.id.temp_value);
        temp.setText(temperature);
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(this,
                R.array.temp_condition, R.layout.spinner_textview_layout); //change the last argument here to your xml above.
        conditionAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        temp_condition.setAdapter(conditionAdapter);
        temp_condition.setSelection(getIndex(condition, temp_condition));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification__rule, menu);
        return true;
    }
    public int getIndex(String value, Spinner spinner)
    {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equals(value)){
                index = i;
                break;
            }
        }
        return index;
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

    //user clicks on update rule
    public void update(View view) {
        //read the user input and return to the main activity with the updated rules
        Intent intent = new Intent();
        Firebase connection = new Firebase("https://scorching-torch-3800.firebaseio.com/");

        connection.child("Notification_temperature").setValue(temp.getText().toString());
        System.out.println(temp.getText());
        connection.child("Notification_condition").setValue(temp_condition.getSelectedItem().toString());
        finish();
    }
}
