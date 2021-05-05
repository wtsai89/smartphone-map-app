package edu.sjsu.android.project4williamtsai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationsDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LOCATIONsDatabase";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "locations";
    private static final String ID = "_id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ZOOM = "zoom";
    private static final String TYPE = "type";
    static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " ("+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LATITUDE + " DOUBLE NOT NULL, "
                    + LONGITUDE + " DOUBLE NOT NULL, "
                    + ZOOM + " FLOAT NOT NULL, "
                    + TYPE + " INTEGER NOT NULL);";

    public LocationsDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("delete from "+ TABLE_NAME);
    }

    public Cursor getAllLocations(String orderBy) {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME,
                new String[]{ID, LATITUDE, LONGITUDE, ZOOM, TYPE},
                null, null, null, null, orderBy);
    }
}
