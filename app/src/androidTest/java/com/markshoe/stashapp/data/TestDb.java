package com.markshoe.stashapp.data;

/**
 * Created by shoe on 15-05-18.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.markshoe.stashapp.data.BagContract.BagEntry;
import com.markshoe.stashapp.data.BagContract.ItemEntry;
import com.markshoe.stashapp.data.BagContract.ItemTransactionEntry;
import com.markshoe.stashapp.data.BagContract.TransactionEntry;

import java.util.ArrayList;
import java.util.HashSet;//723 rymal rd west , west mount medical center

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(BagDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ItemEntry.TABLE_NAME);
        tableNameHashSet.add(ItemTransactionEntry.TABLE_NAME);
        tableNameHashSet.add(TransactionEntry.TABLE_NAME);
        tableNameHashSet.add(BagContract.BagEntry.TABLE_NAME);

        mContext.deleteDatabase(BagDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new BagDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        /* Testing all tables created */
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("DB  not been created correctly", c.moveToFirst());
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("DB does not contain all tables", tableNameHashSet.isEmpty());

        /* Testing most basic query from db */
        c = db.rawQuery("PRAGMA table_info(" + ItemEntry.TABLE_NAME + ")", null);
        assertTrue("Unable to query db for information. Item table", c.moveToFirst());


        /* Testing to see if the Item Entry table was created correctly */
        final ArrayList<String> ItemColumns = new ArrayList<String>();
        ItemColumns.add(ItemEntry.COLUMN_ITEM_ID);
        ItemColumns.add(ItemEntry.COLUMN_TAG_ID);
        ItemColumns.add(ItemEntry.COLUMN_NAME);
        ItemColumns.add(ItemEntry.COLUMN_DESC);
        ItemColumns.add(ItemEntry.COLUMN_CURRENT_BAG);
        ItemColumns.add(ItemEntry.COLUMN_DATE_DESC);
        ItemColumns.add(ItemEntry.COLUMN_COLOR);
        ItemColumns.add(ItemEntry.COLUMN_ICON);
        ItemColumns.add(ItemEntry.COLUMN_IMAGE_CODE);
        ItemColumns.add(ItemEntry.COLUMN_BLOB_IMAGE);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            assertTrue("Db does not contain "+ columnName, ItemColumns.contains(columnName));
            ItemColumns.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Db does not contain all Item Table Columns", ItemColumns.isEmpty());

        /* Testing to see if the item_transaction table was created correctly */
        final ArrayList<String> ItemTransactionColumns = new ArrayList<String>();
        ItemTransactionColumns.add(ItemTransactionEntry._ID);
        ItemTransactionColumns.add(ItemTransactionEntry.COLUMN_ITEM_KEY);
        ItemTransactionColumns.add(ItemTransactionEntry.COLUMN_TRANSACTION_KEY);
        ItemTransactionColumns.add(ItemTransactionEntry.COLUMN_TIME_EPOCH);
        ItemTransactionColumns.add(ItemTransactionEntry.COLUMN_MOVEMENT);

        c = db.rawQuery("PRAGMA table_info(" + ItemTransactionEntry.TABLE_NAME + ")", null);
        assertTrue("Unable to query db for information. Item_Transaction", c.moveToFirst());
        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            assertTrue("Db does not contain "+ columnName, ItemTransactionColumns.contains(columnName));
            ItemTransactionColumns.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Db does not contain all item_transaction Table Columns", ItemTransactionColumns.isEmpty());


        /* Testing to see if the bag_transaction table was created correctly */
        final ArrayList<String> BagTransactionColumns = new ArrayList<String>();
        BagTransactionColumns.add(TransactionEntry.COLUMN_TRANSACTION_ID);
        BagTransactionColumns.add(TransactionEntry.COLUMN_COORD_LAT);
        BagTransactionColumns.add(TransactionEntry.COLUMN_COORD_LONG);
        BagTransactionColumns.add(TransactionEntry.COLUMN_TIME_EPOCH);
        BagTransactionColumns.add(TransactionEntry.COLUMN_BAG_KEY);
        c = db.rawQuery("PRAGMA table_info(" + TransactionEntry.TABLE_NAME + ")", null);
        assertTrue("Unable to query db for information for Bag_Transaction Table.", c.moveToFirst());
        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            assertTrue("Db does not contain "+ columnName, BagTransactionColumns.contains(columnName));
            BagTransactionColumns.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Db does not contain all Bag_transaction Table Columns", BagTransactionColumns.isEmpty());


        final ArrayList<String> BagEntryColumns = new ArrayList<String>();
        BagEntryColumns.add(BagEntry.COLUMN_ID);
        BagEntryColumns.add(BagEntry.COLUMN_NAME);
        BagEntryColumns.add(BagEntry.COLUMN_COORD_LAT);
        BagEntryColumns.add(BagEntry.COLUMN_COORD_LONG);
        BagEntryColumns.add(BagEntry.COLUMN_DRAWABLE_NAME);
        BagEntryColumns.add(BagEntry.COLUMN_IMAGE_CODE);
        BagEntryColumns.add(BagEntry.COLUMN_BLOB_IMAGE);

        c = db.rawQuery("PRAGMA table_info(" + BagEntry.TABLE_NAME + ")", null);
        assertTrue("Unable to query db for information for Bag Table.", c.moveToFirst());
        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            assertTrue("Db does not contain "+ columnName, BagEntryColumns.contains(columnName));
            BagEntryColumns.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Db does not contain all Bag_transaction Table Columns", BagEntryColumns.isEmpty());


        // Good guy shoe
        db.close();
    }

    public void testItemTable(){
        ContentValues testValues = TestUtilities.createHatItem();
        TestUtilities.createItem(testValues,mContext);
        deleteTheDatabase();
        ContentValues testValues2 = TestUtilities.createBaseballItem();
        TestUtilities.createItem(testValues2,mContext);
        deleteTheDatabase();
        ContentValues testValues3 = TestUtilities.createChargerItem();
        TestUtilities.createItem(testValues3,mContext);
        deleteTheDatabase();
    }

    public void testBagTable() {
        ContentValues testValues = TestUtilities.createBag1();
        long bagID = TestUtilities.createBag(testValues, mContext);
        deleteTheDatabase();
        ContentValues testValues2 = TestUtilities.createBag1();
        long bagID2 = TestUtilities.createBag(testValues2, mContext);
        deleteTheDatabase();
    }



    public void testTransactionTable() {
        /* Inserting without a location */
        ContentValues testValues = TestUtilities.createBag1();
        long bagID = TestUtilities.createBag(testValues, mContext);

        ContentValues testValues2 = TransactionEntry.createContentValues(23.1,12.3, System.currentTimeMillis(),bagID);
        TestUtilities.createTransaction(testValues2, mContext);
    }

    public void testItemTransactionTable(){
        /* Check the Logs on this one , You can see SQLLiteLog throw the error for the FK
        constraints which is useful */
        deleteTheDatabase();
        ContentValues itemValues = TestUtilities.createChargerItem();
        long itemId=TestUtilities.createItem(itemValues, mContext);

        ContentValues testValues = TestUtilities.createBag1();
        long bagID = TestUtilities.createBag(testValues, mContext);
        ContentValues transactionValues = TransactionEntry.createContentValues(23.1, 12.3, System.currentTimeMillis(), bagID);
        long transactionId=TestUtilities.createTransaction(transactionValues, mContext);
        ContentValues itemTransactionTestValues = ItemTransactionEntry.createContentValues(
                itemId, transactionId, System.currentTimeMillis(), 1);
        TestUtilities.createItemTransaction(itemTransactionTestValues, mContext);

        /* Now we need to make sure its foreign key constraints are working no transaction set*/
        deleteTheDatabase();
        itemId=TestUtilities.createItem(itemValues, mContext);
        transactionId= 1043; // Fake number not in
        itemTransactionTestValues = ItemTransactionEntry.createContentValues(
                itemId, transactionId, System.currentTimeMillis(), 1);
        BagDbHelper dbHelper = new BagDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long itemTransId = db.insert(ItemTransactionEntry.TABLE_NAME, null, itemTransactionTestValues);
        assertTrue("Inserted a row, shouldnt be possible , check fk constraint= ON ", itemTransId == -1);
        Cursor cursor = db.query(ItemTransactionEntry.TABLE_NAME, null, null, null, null, null, null);
        assertFalse("Error: Records returned from transaction query should not be",
                cursor.moveToFirst());

        /* Check if Fk constraint works the other way */
        deleteTheDatabase();
        itemId = 1000;

        testValues = TestUtilities.createBag1();
        bagID = TestUtilities.createBag(testValues, mContext);
        transactionValues = TransactionEntry.createContentValues(23.1, 12.3, System.currentTimeMillis(), bagID);
        transactionId=TestUtilities.createTransaction(transactionValues, mContext);
        itemTransactionTestValues = ItemTransactionEntry.createContentValues(
                itemId, transactionId, System.currentTimeMillis(), 1);
        itemTransId = db.insert(ItemTransactionEntry.TABLE_NAME, null, itemTransactionTestValues);
        assertTrue("Inserted a row, shouldnt be possible , check fk constraint= ON ", itemTransId == -1);
        assertFalse("Error: Records returned from transaction query should not be",
                cursor.moveToFirst());
        db.close();
        cursor.close();
    }
}