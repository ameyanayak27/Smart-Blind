package com.example.nayak.smartblindsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/*
Created by Ameya nayak and Rajesh Shetty
Displays a form where the user can enter rule parameters
 */
public class ModifyRulesActivity extends ActionBarActivity {

    TextView ambienceField;
    TextView temperatureField;
    TextView statusField;
    Spinner statusValue;
    Spinner temperatureValue;
    Spinner ambientValue;
    Spinner conditionValue;
    EditText name;
    Button button;
    String button_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_rules);
        statusField = (TextView)findViewById(R.id.status_field2);
        statusField.setText("Blind Status");
        ambienceField = (TextView)findViewById(R.id.ambience_field2);
        ambienceField.setText("Ambient");
        temperatureField = (TextView)findViewById(R.id.temperature_field2);
        temperatureField.setText("Temperature");
        statusValue = (Spinner)findViewById(R.id.status_field_spinner);
        temperatureValue = (Spinner)findViewById(R.id.temperature_spinner);
        ambientValue = (Spinner)findViewById(R.id.ambience_spinner);
        conditionValue = (Spinner)findViewById(R.id.condition_spinner);
        name = (EditText) findViewById(R.id.name_value);
        //set textviews for spinners
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,R.array.blind_status, R.layout.spinner_textview_layout);
        statusAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        statusValue.setAdapter(statusAdapter);
        ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(this, R.array.temperature_values, R.layout.spinner_textview_layout);
        tempAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        temperatureValue.setAdapter(tempAdapter);
        ArrayAdapter<CharSequence> ambientAdapter = ArrayAdapter.createFromResource(this,
                R.array.ambient_values, R.layout.spinner_textview_layout); ambientAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        ambientValue.setAdapter(ambientAdapter);
        ArrayAdapter conditionAdapter = ArrayAdapter.createFromResource(this,R.array.conditions, R.layout.spinner_textview_layout);
        conditionAdapter.setDropDownViewResource(R.layout.spinner_textview_layout);
        conditionValue.setAdapter(conditionAdapter);

        //button_text would be add, update or delete depending on the user action on the previous activity
        Bundle p  = this.getIntent().getExtras();
        button_text = p.getString("button");
        button = (Button) findViewById(R.id.submit_button);
        //if the user is just viewing the non editable rules
        if (button_text.equals("View"))
            button.setVisibility(View.INVISIBLE);
        else
            button.setText(button_text);
        //if the text is not delete
        if(!button_text.equals("Add")) {
            final String rule_name = p.getString("name");
            //connect to the specific rule db
            Firebase connection = new Firebase("https://sizzling-torch-8716.firebaseio.com/" + rule_name);
            connection.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    String temperature_value = (String) snapshot.child("temperature").getValue();
                    String ambient_value = (String) snapshot.child("ambient").getValue();
                    String bs_value = (String) snapshot.child("blindsstatus").getValue();
                    String condition_value = (String) snapshot.child("condition").getValue();

                    ambientValue.setSelection(getIndex(ambient_value, ambientValue));
                    temperatureValue.setSelection(getIndex(temperature_value, temperatureValue));
                    statusValue.setSelection(getIndex(bs_value, statusValue));
                    conditionValue.setSelection(getIndex(condition_value, conditionValue));
                    name.setText(rule_name, TextView.BufferType.EDITABLE);
                }

                @Override
                public void onCancelled(FirebaseError error) {
                    System.out.println("Read failed: " + error.getMessage());
                }
            });
        }
    }

    //get index of selected item
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_rules, menu);
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
    //when the user clicks on add update or delete
    public void submit(View view) throws InterruptedException {
        Firebase connection_rules = new Firebase("https://sizzling-torch-8716.firebaseio.com/");
        String name_value = name.getText().toString();
        Log.d("Debug",name_value);
        //switch based on the user action
        switch (button_text) {
            case "Delete":
                //remove rule
                Firebase connection = new Firebase("https://sizzling-torch-8716.firebaseio.com/" + name_value);
                connection.setValue(null);
                break;
            default :
                //add , update rule
                String ambient = ambientValue.getSelectedItem().toString();
                String temperature = temperatureValue.getSelectedItem().toString();
                String blind_status = statusValue.getSelectedItem().toString();
                String condition = conditionValue.getSelectedItem().toString();
                connection_rules.child(name_value).child("ambient").setValue(ambient);
                connection_rules.child(name_value).child("temperature").setValue(temperature);
                connection_rules.child(name_value).child("blindsstatus").setValue(blind_status);
                connection_rules.child(name_value).child("condition").setValue(condition);

        }
        //load ListRulesActivity
        Intent intent = new Intent(this, ListRulesActivity.class);
        startActivity(intent);
        finish();
    }
}
