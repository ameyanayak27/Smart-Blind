package com.example.nayak.smartblindsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/*
Created by Ameya nayak and Rajesh Shetty
Displays the Rules as a listview whic the user can update and delete
 */
public class ListRulesActivity extends ActionBarActivity {
    //SimpleAdapter needs an arraylist of map
    ArrayList <Map<String,String>> ruleslist = new ArrayList<Map<String,String>>();
    ListView listView;
    SimpleAdapter adapter;
    String rule_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rules);
        initList();
        //Create a listview
        listView = (ListView) findViewById(R.id.listView);
        try {
            //wait to populate the list
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new SimpleAdapter(this, ruleslist, android.R.layout.simple_list_item_1, new String[] {"rules"}, new int[] {android.R.id.text1});
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }
    //populate the context menu for long click on an item
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        HashMap map = (HashMap) adapter.getItem(info.position);
        menu.setHeaderTitle("Options for " + map.get("rules"));
        rule_name = map.get("rules").toString();
        //Default rules are viewable only
        if (map.get("rules").equals("1") || map.get("rules").equals("2") || map.get("rules").equals("3") || map.get("rules").equals("4") || map.get("rules").equals("5"))
            menu.add(1, 1, 1, "View");
        else
        {
            menu.add(1, 1, 1, "Delete");
            menu.add(1, 2, 2, "Modify");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_rules, menu);
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
    //Initialize the arraylist with rules retrieved  from the firebase db
    private void initList(){
        Firebase connection = new Firebase("https://sizzling-torch-8716.firebaseio.com/");
        // Listener to read the data
        connection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ruleslist.clear(); //so as to avoid duplicate data
                for (DataSnapshot snap: snapshot.getChildren()) {
                    ruleslist.add(createRules("rules", snap.getKey().toString()));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    //Returns a map which is inserted into ruleslist
    private HashMap<String, String> createRules(String key, String name) {
        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put(key, name);
        return rules;
    }

    //on selecting a contextItem start a new activity
    public boolean onContextItemSelected(MenuItem item) {
        String text  = item.getTitle().toString();
        Intent intent = new Intent(this, ModifyRulesActivity.class);
        intent.putExtra("button", text); //the function of the button , that is, view/ update or delete
        intent.putExtra("name", rule_name); //the name of the rule
        startActivity(intent);
        finish();
        return true;
    }



}