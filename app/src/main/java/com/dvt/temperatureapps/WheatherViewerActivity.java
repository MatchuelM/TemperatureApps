package com.dvt.temperatureapps;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WheatherViewerActivity extends AppCompatActivity {

    private ListView weather_list;
    private ArrayList<String> tmp_city;
    private ArrayList<String> tmp_degrees;
    private ArrayList<String> tmp_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheather_viewer);

        final WeatherDBHelper w_db_helper = new WeatherDBHelper(this);
        final ArrayList temp_list = w_db_helper.getAllTemps();

        weather_list = (ListView) findViewById(R.id.weather_list);
        tmp_city = new ArrayList<>();
        tmp_degrees = new ArrayList<>();
        tmp_date = new ArrayList<>();
        temp_list.clear();

        ArrayAdapter<String> wthr_adpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_text_file, tmp_city);
        weather_list.setAdapter(wthr_adpter);
        tmp_city.addAll(w_db_helper.getAllTemps());
        wthr_adpter.notifyDataSetChanged();

        weather_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3)
            {
                String selectedFromList = (String) (weather_list.getItemAtPosition(position).toString());
                String[] sep_str = selectedFromList.split("--");

                Toast.makeText(getApplicationContext(),sep_str[0]+"  Temperature was-"+sep_str[2],Toast.LENGTH_SHORT).show();

            }
        });
    }
}