package com.dvt.temperatureapps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private EditText ed1;
    private TextView txtTmp,txtWeather,txtMn,tmpCurrnt,txtMax,txtDay,txtIcon,tempWeek;
    //private ImageView tempIcon;
    private TextView tempIcon;

    ImageButton search_btn;

    private ImageView weather_rep,store_data,_ticked;

    //Arraylists for inserting the values
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayList2;

    private ArrayList<String> arrayListDays;
    private ArrayList<String> arrayListTemps;
    private ArrayList<String> arrayListMaxTemps;
    private ArrayList<String> arrayListMainDescription;
    private ArrayList<String> arrayListIcons;
    private ArrayList<String> arrayListDescriptions;
    private ArrayList<String> arrayListWeatherCondition;

    //My Botton Nav
    BottomNavigationView wethr_bottom_nav;

    //Temperature double to assist in calculation temperature
    private String jsonResult = "";
    double fnlTemp = 0.0;
    double minTemp = 0.0;
    double maxTemp = 0.0;
    double calcd = 0.0;
    double temperatureDouble = 0.0;
    double weekTemperatureDouble = 0.0;
    private String temperatureString = "";
    private String tempFnl = "";
    private String minTemps = "";
    private String maxTemps = "";
    private String wthrDescrip = "";
    private ConstraintLayout c_l,c_l2;

    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    WeatherDBHelper my_wthr_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Call the sqlite db class
        my_wthr_db = new WeatherDBHelper(getApplicationContext());

        c_l2 = (ConstraintLayout) findViewById(R.id.constraintLayout);
        c_l = (ConstraintLayout) findViewById(R.id.construct);
        ed1 = (EditText) findViewById(R.id.find_place);
        search_btn = (ImageButton) findViewById(R.id.search_btn);
        store_data = (ImageView) findViewById(R.id.store_data);
        _ticked = (ImageView) findViewById(R.id.tickx);
        weather_rep = (ImageView) findViewById(R.id.weather_rep);

        txtTmp = (TextView) findViewById(R.id.txtTemp);
        txtWeather = (TextView) findViewById(R.id.txtweathr);
        txtMn = (TextView) findViewById(R.id.mintemp);
        tmpCurrnt = (TextView) findViewById(R.id.currtemp);
        txtMax = (TextView) findViewById(R.id.maxtemp);
        txtDay = (TextView) findViewById(R.id.day_of_week);
        //tempIcon = (ImageView) findViewById(R.id.wheather_icon);
        tempIcon = (TextView) findViewById(R.id.wheather_icon);
        tempWeek = (TextView) findViewById(R.id.weekTemp);

        arrayList = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();
        arrayListMaxTemps = new ArrayList<String>();

        arrayListDays = new ArrayList<String>();
        arrayListIcons = new ArrayList<String>();
        arrayListTemps = new ArrayList<String>();
        arrayListIcons = new ArrayList<String>();
        arrayListDescriptions = new ArrayList<String>();
        arrayListWeatherCondition = new ArrayList<String>();
        arrayListMainDescription = new ArrayList<String>();

        //get Current Date
        SimpleDateFormat s_d_f = new SimpleDateFormat("yyyy-MM-dd");
        String currDate = s_d_f.format(new Date());

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if(ed1.getText().toString().equals("")){
                        txtWeather.setText("Please Type Place!!");
                        return;
                    }else{
                        arrayListTemps.clear();
                        arrayListWeatherCondition.clear();
                        arrayListDays.clear();
                        arrayListIcons.clear();

                        txtMn.setText("");
                        tmpCurrnt.setText("");
                        txtMax.setText("");

                        getCurrentWeather(ed1.getText().toString());
                        getWeeklyData(ed1.getText().toString());
                    }
                }
                catch (Exception a)
                {
                    Log.e(TAG, "Received an exception " + a.getMessage() );
                }
            }
        });

        store_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    my_wthr_db.insertWheather(ed1.getText().toString(),txtTmp.getText().toString(),currDate);
                    _ticked.setVisibility(View.VISIBLE);
                }
                catch (Exception g){
                    Log.e(TAG, "Received an exception " + g.getMessage() );
                }

            }
        });

        // Initialize my Bottom navigation Here
        wethr_bottom_nav = (BottomNavigationView) findViewById(R.id.wethr_bottom_nav);
        wethr_bottom_nav.getMenu().getItem(0).setCheckable(false);

        // Listen for item selected
        wethr_bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.favourites:

                        Intent wthr_int = new Intent(getApplicationContext(),WheatherViewerActivity.class);
                        startActivity(wthr_int);
                        break;
                    case R.id.mapped:
                        Toast.makeText(getApplicationContext(), "GOOGLE API WAS ASKING FOR MY PAYMENT DETAILS EVEN FOR A FREE ACCOUNT", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

    }

    //This method gets current wheather
    private void getCurrentWeather(String cityName)
    {

        //Reference the name of the city here name for here!
        String city = cityName;

        StringRequest stringRequest = new StringRequest("https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid=adbe96d8aaa0cf7c88dc6b4b98700a8e ",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        try
                        {

                            //Call the jsonObject that containts the volley json response from the Server.
                            JSONObject JsonObject = new JSONObject(response);
                            JSONObject main = jsnJSNObject(JsonObject, "main");
                            if(main != null)
                            {
                                // Convert Temperature from Json Payload to Celcius by subtracting 273.15
                                fnlTemp = (Double.parseDouble(jsonGetString(main, "temp")))-273.15;
                                minTemp = (Double.parseDouble(jsonGetString(main, "temp_min")))-273.15;
                                maxTemp = (Double.parseDouble(jsonGetString(main, "temp_max")))-273.15;

                                tempFnl = Double.toString(fnlTemp);
                                minTemps = Double.toString(minTemp);
                                maxTemps = Double.toString(maxTemp);

                            }


                            JSONArray my_weather = jsnHlpJSNrray(JsonObject, "weather");
                            for(int i=0; i<my_weather.length(); i++)
                            {
                                JSONObject thisWeather = my_weather.getJSONObject(i);
                                wthrDescrip = jsonGetString(thisWeather, "description") + "\n";
                            }


                            //Weather Values are diplayed on the textViews
                            txtTmp.setText(tempFnl.substring(0,4)+"°");
                            txtWeather.setText(wthrDescrip);
                            String weather_focus = txtWeather.getText().toString().trim();
                            if(weather_focus.equals("clear sky")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sunny));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sunny));
                                weather_rep.setBackgroundResource(R.drawable.forest_sunny);
                            } else if(weather_focus.equals("light rain")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.rainy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.rainy));
                                weather_rep.setBackgroundResource(R.drawable.forest_rainy);
                            } else if(weather_focus.equals("moderate rain")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.rainy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.rainy));
                                weather_rep.setBackgroundResource(R.drawable.sea_rainy);
                            }else if(weather_focus.equals("broken clouds")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                weather_rep.setBackgroundResource(R.drawable.forest_cloudy);
                            }else if(weather_focus.equals("cloudy")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                weather_rep.setBackgroundResource(R.drawable.forest_sunny);
                            }else if(weather_focus.equals("few clouds")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                weather_rep.setBackgroundResource(R.drawable.sea_cloudy);
                            }else if(weather_focus.equals("scattered clouds")){
                                c_l.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                c_l2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cloudy));
                                weather_rep.setBackgroundResource(R.drawable.sea_cloudy);
                            }
                            txtMn.setText(minTemps.substring(0,4)+"°");
                            tmpCurrnt.setText(tempFnl.substring(0,4)+"°");
                            txtMax.setText(maxTemps.substring(0,4)+"°");

                        }
                        catch (JSONException e)
                        {
                            Log.e(TAG, "Received an exception " + e.getMessage() );
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Received an exception " + error.getMessage() );
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //This method gets weekly weather data
    private void getWeeklyData(String cityName)
    {
        arrayListTemps.clear();
        arrayListWeatherCondition.clear();
        arrayListDays.clear();
        arrayListIcons.clear();

        String city = cityName;
        // This is the volley http connection initiated to retrieve the json payload
        StringRequest stringRequest = new StringRequest("https://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid=adbe96d8aaa0cf7c88dc6b4b98700a8e ",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject JsonObject = new JSONObject(response);
                            String mess = jsonGetString(JsonObject, "list");

                            JSONArray jsonarray = new JSONArray(mess);
                            for (int i = 0; i < jsonarray.length(); i++)
                            {
                                //For Loops to parse the json payload and extract the data i need for the View.(Pardon my rushed Work(;;)   )
                                //Here are the Json Objects from the json Payload (wheather api)
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                JSONObject jsonobjecDDt = jsnJSNObject(jsonobject, "main");


                                //Json Array from the json payload posted by API query
                                JSONArray my_weather = jsnHlpJSNrray(jsonobject, "weather");
                                String main = jsonobject.getString("main");
                                String weatherS = jsonobject.getString("weather");
                                String dateS = jsonobject.getString("dt_txt");


                                //Keystrings extracted from the json(Objects+Array)
                                String tempS = jsonGetString(jsonobjecDDt, "temp");
                                //Convert the tempetature to celsius
                                temperatureDouble = (Double.parseDouble(tempS))-273.15;
                                temperatureString = (Double.toString(temperatureDouble).substring(0,4));

                                String descriptionS = jsonGetString(jsonobjecDDt, "description");
                                weekTemperatureDouble =(Double.parseDouble(jsonGetString(jsonobjecDDt, "temp"))-273.15);
                                String weekTemp = (Double.toString(weekTemperatureDouble).substring(0,4));


                                for(int a=0; a<my_weather.length(); a++)
                                {
                                    JSONObject currentWeather = my_weather.getJSONObject(a);

                                    //Declare weather values extracted from weekly weather JsonObjects
                                    String weatherDescriptions = jsonGetString(currentWeather, "description");
                                    String weatherIcons = jsonGetString(currentWeather, "icon");

                                    //Add the payload values retrieved from json to arrayListS for later retrieval
                                    arrayListTemps.add(temperatureString);
                                    arrayListWeatherCondition.add(weatherDescriptions);
                                    arrayListDays.add(dateS);

                                }

                            }


                            //Populate the DAY textView with values from te ArrayList.
                            for(int i=0; i< arrayListDays.size(); i++)
                            {
                                //Convert String Dates to DAY_OF_WEEK
                                String date = arrayListDays.get(i++).toString();
                                String input_date = date;
                                SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dt1=format1.parse(input_date);
                                SimpleDateFormat frmd_2 =new SimpleDateFormat("EEEE");
                                String weeks_Day = frmd_2.format(dt1);
                                
                                //Append Day_OF_WEEK to TextView
                                txtDay.append(weeks_Day + "\n\n");

                            }


                            //Populate the Icons from The Json Payload
                            for(int i = 0; i < arrayListWeatherCondition.size(); i++)
                            {
                                String icon = arrayListWeatherCondition.get(i++).trim();
                                tempIcon.append(icon+"\n\n");
                            }

                            //Populate the TEMPERATURE from The Json Payload
                            for(int i = 0; i < arrayListTemps.size(); i++)
                            {
                                String tempAweek = arrayListTemps.get(i++);
                                tempWeek.append(tempAweek+"°"+"\n\n");
                            }


                        }
                        catch (JSONException | ParseException e)
                        {
                            Log.e(TAG, "Received an exception " + e.getMessage() );
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Received an exception " + error.getMessage() );
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String jsonGetString(JSONObject obj, String k){
        String v = null;
        try {
            v = obj.getString(k);
        } catch (JSONException e) {
            Log.e(TAG, "Received an exception " + e.getMessage() );
        }

        return v;
    }

    private JSONObject jsnJSNObject(JSONObject obj, String k){
        JSONObject o = null;

        try {
            o = obj.getJSONObject(k);
        } catch (JSONException e) {
            Log.e(TAG, "Received an exception " + e.getMessage() );
        }

        return o;
    }

    private JSONArray jsnHlpJSNrray(JSONObject obj, String k){
        JSONArray a = null;

        try {
            a = obj.getJSONArray(k);
        } catch (JSONException e) {
            Log.e(TAG, "Received an exception " + e.getMessage() );
        }

        return a;
    }
}