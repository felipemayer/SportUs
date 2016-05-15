package com.sportus.sportus.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class dbHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "banco.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "eventos";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String AUTOR = "autor";
    private static final String TYPE = "type";
    private static final String LEVEL = "level";
    private static final String ADDRESS = "address";
    private static final String DATE = "date";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ICON = "icon";
    private static final String PAY_METHOD = "pay_method";
    private static final String COST = "cost";

    private static final String CREATE_TABLE = "CREATE TABLE" + TABLE_NAME + " ( "
                    + ID + " integer primary key autoincrement, "
                    + TITLE + " text, "
                    + AUTOR + " text, "
                    + TYPE + " text, "
                    + LEVEL + " text, "
                    + ADDRESS + " text, "
                    + DATE + " text, "
                    + LATITUDE + " text, "
                    + LONGITUDE + " text, "
                    + ICON + " text, "
                    + PAY_METHOD + " text, "
                    + COST + " integer "
                    + " ) ";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public dbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public dbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
}
