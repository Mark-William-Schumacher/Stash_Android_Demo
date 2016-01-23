package com.markshoe.stashapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.test.AndroidTestCase;
import android.util.Log;

import com.markshoe.stashapp.data.BagContract.BagEntry;
import com.markshoe.stashapp.data.BagContract.ItemEntry;
import com.markshoe.stashapp.data.BagContract.ItemTransactionEntry;
import com.markshoe.stashapp.data.BagContract.TransactionEntry;


import java.util.Map;
import java.util.Set;

/**
 * Created by shoe on 15-05-18.
 */
public class TestUtilities extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public static ContentValues createHatItem(){
        return ItemEntry.createContentValues(
                "HAT0902DD", "Hat",1, "Put this on your head to stay warm",
                System.currentTimeMillis(), Color.BLUE, "hat",1,null);
    }
    public static ContentValues createChargerItem(){
        return ItemEntry.createContentValues("Charger321", "Charger",1, "Put this on your head to stay warm",
                System.currentTimeMillis(), Color.RED, "hat",1,null);
    }
    public static ContentValues createBaseballItem(){
        return ItemEntry.createContentValues("BaseballBat13", "Baseball Bat",1, "Put this on your head to stay warm",
                System.currentTimeMillis(), Color.RED, "hat",1,null);
    }
    public static ContentValues TransactionEmptyLocation(){
        return TransactionEntry.createContentValues(-1, -1, System.currentTimeMillis(),1);
    }
    public static ContentValues TransactionWithLocation(){
        return TransactionEntry.createContentValues(39.349, 1.93, System.currentTimeMillis(),2);
    }
    public static ContentValues createBag1(){
        return BagEntry.createContentValues("Main Backpack","bagicon",23.2,23.1, BagEntry.NO_IMAGE,null);
    }
    public static ContentValues createBag2(){
        return BagEntry.createContentValues("Gym Bag","dufflebag",23.1,99.9,BagEntry.NO_IMAGE,null);
    }


    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            // We don't really need to do this we can safely call out the
            // items in the row based on their PROJECTION order in the SELECT statement
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            if (entry.getValue() == null){
                assertEquals(null,valueCursor.getString(idx));
            }else {
                String expectedValue = entry.getValue().toString();
                assertEquals("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
            }
        }
    }

    public static long createBag(ContentValues values, Context context){
        BagDbHelper dbHelper = new BagDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long bagRowId = db.insert(BagContract.BagEntry.TABLE_NAME, null, values);
        assertTrue(bagRowId != -1);
        Cursor cursor = db.query(BagContract.BagEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No Records returned from item query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Item Validation Failed", cursor, values);
        assertFalse("Multiple Items Returned", cursor.moveToNext());
        return bagRowId;
    }

    public static long createItem(ContentValues values, Context context){
        BagDbHelper dbHelper = new BagDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long itemRowId = db.insert(ItemEntry.TABLE_NAME, null, values);
        assertTrue(itemRowId != -1);
        Cursor cursor = db.query(ItemEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No Records returned from item query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Item Validation Failed", cursor, values);
        assertFalse("Multiple Items Returned", cursor.moveToNext());
        return itemRowId;
    }

    public static long createTransaction(ContentValues values, Context context) {
        BagDbHelper dbHelper = new BagDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long transactionID = db.insert(TransactionEntry.TABLE_NAME, null, values);
        assertTrue(transactionID != -1);
        Cursor cursor = db.query(TransactionEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No Records returned from transaction query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: transa Validation Failed", cursor, values);
        assertFalse("Multiple Items Returned", cursor.moveToNext());
        cursor.close();
        db.close();
        return transactionID;
    }

    public static long createItemTransaction(ContentValues values, Context context){
        BagDbHelper dbHelper = new BagDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long itemTransId = db.insert(ItemTransactionEntry.TABLE_NAME, null, values);
        assertTrue(itemTransId != -1);
        Cursor cursor = db.query(ItemTransactionEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No Records returned from transaction query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: transa Validation Failed", cursor, values);
        assertFalse("Multiple Items Returned", cursor.moveToNext());
        cursor.close();
        db.close();
        return itemTransId;
    }

    public static void printCursor(Cursor c){
        String colNames = "";
        for (int i =  0 ; i <c.getColumnCount() ; i++){
            colNames = colNames.concat(c.getColumnName(i) + " ");
        }
        Log.e("XXXXXXXXX", colNames);
        String rows = "";
        boolean remaining = c.moveToFirst();
        while (remaining) {
            for (int i =  0 ; i <c.getColumnCount() ; i++){
                rows = rows.concat(c.getString(i) + "     ");
            }
            Log.e("XXXXXXXXX", rows);
            rows = "";
            remaining= c.moveToNext();
        }
    }

}
