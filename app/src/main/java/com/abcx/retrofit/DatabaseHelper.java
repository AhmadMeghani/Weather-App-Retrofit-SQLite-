package com.abcx.retrofit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "weather.db";
    public static final String TABLE_NAME = "weatherData_table";
    public static final String COL_1 = "CITY";
    public static final String COL_2 = "ICON";
    public static final String COL_3 = "DESCRIPTION";
    public static final String COL_4 = "TEMPERATURE";
    public static final String COL_5 = "PRESSURE";
    public static final String COL_6 = "HUMIDITY";
    public static final String COL_7 = "MINTEMP";
    public static final String COL_8 = "MAXTEMP";
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + "(CITY TEXT PRIMARY KEY, ICON TEXT, DESCRIPTION TEXT, TEMPERATURE TEXT, PRESSURE TEXT, HUMIDITY TEXT, MINTEMP TEXT, MAXTEMP TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS TABLE_NAME");
        onCreate(db);
    }

    public boolean insertData(String cityName, String Icon, String Description, String temp, String pressure, String humidity, String minT, String maxT){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,cityName);
        contentValues.put(COL_2,Icon);
        contentValues.put(COL_3,Description);
        contentValues.put(COL_4,temp);
        contentValues.put(COL_5,pressure);
        contentValues.put(COL_6,humidity);
        contentValues.put(COL_7,minT);
        contentValues.put(COL_8,maxT);

        Log.d(TAG, "insertData: "+ cityName);


        long result = db.insert(TABLE_NAME,null,contentValues);

        if (result==-1)
            return  false;
            else
                return  true;


    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;

    }

    public Cursor getWeather(String city){
        //String query = "select * from " + TABLE_NAME + " where "+ COL_1 + " = '" + city + "'";
        SQLiteDatabase sql = this.getReadableDatabase();
        Cursor cur = sql.rawQuery("select * from " + TABLE_NAME + " where " + COL_1 + " like '%" + city + "%'" , null);
        return cur;
    }
}
