package com.sportus.sportus.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = DbHelper.class.getSimpleName();

    public static final String DB_NAME = "SportUsDatabase.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "eventos";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String LEVEL = "level";
    public static final String ADDRESS = "address";
    public static final String DATE = "date";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ICON = "icon";
    public static final String PAY_METHOD = "pay_method";
    public static final String COST = "cost";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + TITLE + " text, "
                + AUTHOR + " text, "
                + TYPE + " text, "
                + LEVEL + " text, "
                + ADDRESS + " text, "
                + DATE + " text, "
                + LATITUDE + " text, "
                + LONGITUDE + " text, "
                + ICON + " text, "
                + PAY_METHOD + " text, "
                + COST + " integer "
                + ") ";

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static DbHelper mDbHelper;

    public static synchronized DbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (mDbHelper == null) {
            mDbHelper = new DbHelper(context.getApplicationContext());
        }
        return mDbHelper;
    }


     /*
   Insert a  user detail into database
   */

    public void insertEvent(EventData eventData) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(DbHelper.TITLE, eventData.title);
            values.put(DbHelper.AUTHOR,  eventData.author);
            values.put(DbHelper.TYPE,  eventData.type);
            values.put(DbHelper.LEVEL,  eventData.level);
            values.put(DbHelper.ADDRESS,  eventData.address);
            values.put(DbHelper.DATE,  eventData.date);
            values.put(DbHelper.LATITUDE,  eventData.latitude);
            values.put(DbHelper.LONGITUDE,  eventData.longitude);
            values.put(DbHelper.ICON,  eventData.icon);
            values.put(DbHelper.PAY_METHOD,  eventData.payMethod);
            values.put(DbHelper.COST,  eventData.cost);

            db.insertOrThrow(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to add post to database");
        } finally {

            db.endTransaction();
        }
    }

    /*
   fetch all data from UserTable
    */

    public List<EventData> getAllEvents() {

        List<EventData> events = new ArrayList<>();

        String EVENT_DETAIL_SELECT_QUERY = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(EVENT_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    EventData eventData = new EventData();
                    eventData.id = cursor.getInt(cursor.getColumnIndex(ID));
                    eventData.title = cursor.getString(cursor.getColumnIndex(TITLE));
                    eventData.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
                    eventData.type = cursor.getString(cursor.getColumnIndex(TYPE));
                    eventData.level = cursor.getString(cursor.getColumnIndex(LEVEL));
                    eventData.address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                    eventData.date = cursor.getString(cursor.getColumnIndex(DATE));
                    eventData.latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                    eventData.longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                    eventData.icon = cursor.getInt(cursor.getColumnIndex(ICON));
                    eventData.payMethod = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(PAY_METHOD)));
                    eventData.cost = cursor.getString(cursor.getColumnIndex(COST));

                    events.add(eventData);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return events;

    }

    /*
   Delete single row from UserTable
     */
    void deleteRow(String name) {
        SQLiteDatabase db = getWritableDatabase();


        try {
            db.beginTransaction();
            db.execSQL("delete from " + TABLE_NAME + " where name ='" + name + "'");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d(TAG, "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }


    }
}
