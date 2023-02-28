package com.dvt.temperatureapps;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WeatherDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "WeatherConditions.db";
    public static final String TEMPS_TABLE = "currentWeather";
    public static final String CITY_NAME = "CityName";
    public static final String CITY_TEMP = "cityTemp";
    public static final String TEMP_DATE = "cityTemp";



    public WeatherDBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL("create table currentWeather " + "(id integer primary key,CityName text, cityTemp text,dateCaptured text)");
        }
        catch(Exception d)
        {
            Log.e(TAG, "Received an exception " + d.getMessage() );
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS currentWeather");
            onCreate(db);
        }
        catch(Exception d)
        {
            Log.e(TAG, "Received an exception " + d.getMessage() );
        }

    }

    public boolean insertWheather (String CityName,String ctyTemp,String dateCaptured )
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("CityName", CityName);
            contentValues.put("cityTemp", ctyTemp);
            contentValues.put("dateCaptured", dateCaptured);
            db.insert("currentWeather", null, contentValues);

        }
        catch(Exception d)
        {
            Log.e(TAG, "Received an exception " + d.getMessage() );
        }
        return true;
    }

    public ArrayList getAllTemps() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();
        Cursor res = db.rawQuery( "select * from "+TEMPS_TABLE, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            array_list.add(res.getString(1)+"--"+res.getString(3)+"--"+res.getString(2));
            res.moveToNext();
        }
        return array_list;
    }

}
