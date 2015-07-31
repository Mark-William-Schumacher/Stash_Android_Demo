package com.markshoe.stashapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.markshoe.stashapp.data.BagContract.BagEntry;
import com.markshoe.stashapp.data.BagContract.ItemEntry;
import com.markshoe.stashapp.data.BagContract.ItemTransactionEntry;
import com.markshoe.stashapp.data.BagContract.TransactionEntry;

/**
 * Created by shoe on 15-05-13.
 */

public class BagDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;

    public static final String DATABASE_NAME = "bag.db";

    public BagDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BAG_TABLE =
                " CREATE TABLE " + BagContract.BagEntry.TABLE_NAME + " (" +
                        BagEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BagEntry.COLUMN_NAME + " STRING NOT NULL, " +
                        BagEntry.COLUMN_COORD_LAT + " REAL, " +
                        BagEntry.COLUMN_COORD_LONG + " REAL, " +
                        BagEntry.COLUMN_DRAWABLE_NAME + " STRING NOT NULL);";

        final String SQL_CREATE_ITEM_TABLE =
                "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                        ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ItemEntry.COLUMN_TAG_ID + " STRING UNIQUE NOT NULL, " +
                        ItemEntry.COLUMN_NAME + " STRING NOT NULL, " +
                        ItemEntry.COLUMN_DESC + " STRING NOT NULL, " +
                        ItemEntry.COLUMN_CURRENT_BAG + " INTEGER NOT NULL," +
                        ItemEntry.COLUMN_DATE_DESC + " INTEGER NOT NULL," +
                        ItemEntry.COLUMN_COLOR + " INTEGER NOT NULL," +
                        ItemEntry.COLUMN_ICON + " STRING NOT NULL," +
                        ItemEntry.COLUMN_IMAGE_CODE + " INTEGER NOT NULL, " +
                        ItemEntry.COLUMN_BLOB_IMAGE + " BLOB);";

        final String SQL_CREATE_ITEM_TRANSACTION_TABLE =
                "CREATE TABLE " + ItemTransactionEntry.TABLE_NAME + " (" +
                        ItemTransactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ItemTransactionEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                        ItemTransactionEntry.COLUMN_TRANSACTION_KEY + " INTEGER NOT NULL, " +
                        ItemTransactionEntry.COLUMN_TIME_EPOCH + " INTEGER NOT NULL, " +
                        ItemTransactionEntry.COLUMN_MOVEMENT + " INTEGER NOT NULL, " +
                        " FOREIGN KEY (" + ItemTransactionEntry.COLUMN_TRANSACTION_KEY + ") REFERENCES " +
                        TransactionEntry.TABLE_NAME + " (" + TransactionEntry._ID + "), " +
                        " FOREIGN KEY (" + ItemTransactionEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                        ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "));";

        final String SQL_CREATE_TRANSACTION_TABLE =
                "CREATE TABLE " + TransactionEntry.TABLE_NAME + " (" +
                        TransactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TransactionEntry.COLUMN_COORD_LAT + " REAL, " +
                        TransactionEntry.COLUMN_BAG_KEY + " INTEGER NOT NULL, " +
                        TransactionEntry.COLUMN_COORD_LONG + " REAL, " +
                        TransactionEntry.COLUMN_TIME_EPOCH + " INTEGER NOT NULL," +
                        " FOREIGN KEY (" + TransactionEntry.COLUMN_BAG_KEY + ") REFERENCES " +
                        BagEntry.TABLE_NAME + " (" + BagEntry._ID + "));";


        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TRANSACTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemTransactionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BagEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
