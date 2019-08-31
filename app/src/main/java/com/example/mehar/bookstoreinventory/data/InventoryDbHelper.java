package com.example.mehar.bookstoreinventory.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mehar.bookstoreinventory.data.InventoryContract.StockEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STOCKS_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " ("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + StockEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + StockEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StockEntry.COLUMN_SUPPLIER + " INTEGER NOT NULL DEFAULT 0 , "
                + StockEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0 );";
        try {
            db.execSQL(SQL_CREATE_STOCKS_TABLE);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Problem in creating a table!!!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}