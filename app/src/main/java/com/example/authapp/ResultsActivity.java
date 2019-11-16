package com.example.authapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import android.util.Log;

public class ResultsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");

        JSONArray jsonArray = null;
        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayList<UserData> list = new ArrayList<UserData>();
        try {
            JSONObject obj = new JSONObject(json);
            Log.e("RESPONSE JSON", json);
            if(obj.has("message")) {
                list.add(new UserData("message", obj.getString("message")));
            } else {
                list.add(new UserData(obj.getString("username"), obj.getString("logintime")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(this, list, R.layout.info,
                new String[]{UserData.USERNAME, UserData.LOGIN_TIME},
                new int[]{R.id.info_name, R.id.info_year});
        listView.setAdapter(adapter);
    }
}