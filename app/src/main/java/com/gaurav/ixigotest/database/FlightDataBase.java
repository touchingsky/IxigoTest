package com.gaurav.ixigotest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author gaurav
 * @version 1.0
 * @since 4/2/17
 */
public class FlightDataBase extends SQLiteOpenHelper {

    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DEPARTURE = "departure";
    public static final String COLUMN_ARRIVAL = "arrival";
    public static final String JSON_FORMAT = "jsonFormat";
    private static FlightDataBase flightDataBase;

    static final String TABLE_NAME = "flights";

    private FlightDataBase(Context context) {
        super(context, "ixigoTest.db", null, 1);
    }

    synchronized static FlightDataBase getInstance(Context context) {
        if (flightDataBase == null) {
            flightDataBase = new FlightDataBase(context);
        }
        return flightDataBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY," +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_DEPARTURE + " TEXT, " +
                COLUMN_ARRIVAL +" TEXT, " +
                JSON_FORMAT + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
