package com.markshoe.stashapp.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.markshoe.stashapp.data.BagContract.BagEntry;
import com.markshoe.stashapp.data.BagContract.ItemEntry;
import com.markshoe.stashapp.data.BagContract.ItemTransactionEntry;
import com.markshoe.stashapp.data.BagContract.TransactionEntry;


import java.util.ArrayList;

/**
 * Created by shoe on 15-05-19.
 */
public class TestProvider extends AndroidTestCase {

    ArrayList<Integer> mItemIDs = new ArrayList<>();
    ContentResolver cr ;
    //ArrayList<Integer> mItemIDs = new ArrayList<>();

    public void testQueries(){
        mContext.deleteDatabase(BagDbHelper.DATABASE_NAME);
        /*
         Create a fake database to do some queries on.
         */
        cr = mContext.getContentResolver();
        long bbItemId = quickItemCreate("BB129039", "Baseball Bat", 0, "shoe");
        long chargerId = quickItemCreate("BB19039", "Charger", 0, "shoe");
        long hatId =  quickItemCreate("BB1290X39", "Hat", 1, "shoe");
        long bbItemId2 = quickItemCreate("asdf2", "Baseball Bat", 0, "shoe");
        long chargerId2 = quickItemCreate("gads", "shoe", 2, "shoe");
        long hatId3 =  quickItemCreate("qewr", "momma", 1, "shoe");
        long bbItemId4 = quickItemCreate("qwerew", "asdfk Bat", 2, "shoe");
        long chargerId5 = quickItemCreate("wtry", "asfd", 2, "shoe");
        long hatId6 =  quickItemCreate("fjhgj", "Hat", 1, "shoe");

        /*
            Backpacks
        */
        long backpack1 = quickBagCreate("Main Backpack", "backpack", 99.9, 99.9);
        long backpack2 = quickBagCreate("Gym bag", "dufflebag", 100.1, 100.1);


//        /*
//                 TRANACTION_BY_ID
//         */
//        long transactionId = quickTransactionCreate(backpack1,33.3,33.3);
//
//        /*
//                 ITEM_TRANSACTION_BY_ID
//         */
//        // Hat and baseball going into the bag charger is going out
//        // This mimics initial creation of the items
//        long itemTransactionId = quickItemTransaction(bbItemId,transactionId,1,backpack1);
//        long itemTransactionId2 = quickItemTransaction(chargerId, transactionId, 1,backpack1);
//        long itemTransactionId3 = quickItemTransaction(hatId, transactionId, 0,backpack1);


        Uri uri = BagEntry.buildBagItemsUriById(0);
        Cursor c = cr.query(uri, null , null, null, null);
        TestUtilities.printCursor(c);
        mContext.deleteDatabase(BagDbHelper.DATABASE_NAME);





//
//        /*
//            ITEMS   , ALL ITEMS QUERY
//         */
//        HashMap<String ,Integer> expectedSet = new HashMap<>();
//        expectedSet.put("Baseball",1);
//        expectedSet.put("Charger",0);
//        expectedSet.put("Hat", 1);
//        boolean cursorActive = c.moveToFirst();
//        while(cursorActive){
//            int cIndex = c.getColumnIndex("item_name");
//            int mIndex = c.getColumnIndex("movement");
//            String cur = c.getString(cIndex);
//            Integer expected = expectedSet.get(cur);
//            if (expected != null) {
//                assertTrue(expected.equals(c.getInt(mIndex)));
//            }else{
//                assertTrue( "EXPECTEDSET != cursor for items query", false); //autofail.
//            }
//            cursorActive = c.moveToNext();
//        }
//
//        try{Thread.sleep(1000);}catch(Exception e){}; // Wait time between transactions
//
//        /*     Baseball is leaving and the charger is coming in  */
//        ContentValues transactionValues2 = TransactionEntry.createContentValues(
//                2000.1, 2000.1, System.currentTimeMillis());
//        uri = cr.insert(TransactionEntry.CONTENT_URI, transactionValues2);
//        long transactionId2 = Long.parseLong(TransactionEntry.getTransactionIDFromUri(uri));
//        c = cr.query(uri, null, null, null, null);
//        TestUtilities.validateCursor("Bad TransactionValues ",c,transactionValues2);
//        ContentValues chargerTransaction2 = ItemTransactionEntry.createContentValues(
//                chargerId, transactionId2, System.currentTimeMillis(), 1);
//        ContentValues baseballTransaction2 = ItemTransactionEntry.createContentValues(
//                bbItemId, transactionId2, System.currentTimeMillis(), 0);
//        cr.insert(ItemTransactionEntry.CONTENT_URI, chargerTransaction2);
//        cr.insert(ItemTransactionEntry.CONTENT_URI, baseballTransaction2);
//        uri = ItemEntry.buildAllItemsUri();
//        c = cr.query(uri, new String[]{"item_name", "movement"}, null, null, null);
//        expectedSet = new HashMap<>();
//        expectedSet.put("Baseball",0);
//        expectedSet.put("Charger",1);
//        expectedSet.put("Hat", 1);
//        cursorActive = c.moveToFirst();
//        while(cursorActive){
//            int cIndex = c.getColumnIndex("item_name");
//            int mIndex = c.getColumnIndex("movement");
//            String cur = c.getString(cIndex);
//            Integer expected = expectedSet.get(cur);
//            if (expected != null) {
//                assertTrue(expected.equals(c.getInt(mIndex)));
//            }else{
//                assertTrue( "EXPECTEDSET != cursor for items query", false); //autofail.
//            }
//            cursorActive = c.moveToNext();
//        }
//
//
//        /*
//                  TRANSACTIONS_WITH_ITEM
//         */
//
//
//        //Test for Baseball
//        uri = TransactionEntry.buildTransactionWithItemUri(bbItemId);
//        c = cr.query(uri, new String[]{"bag_transaction._id","coord_long","coord_lat", "movement","transaction_time"},
//                null, null, null);
//        HashMap<Integer ,Integer> expectedSet2 = new HashMap<>();
//        expectedSet2.put(1, 1); // Transaction 1 : moves in
//        expectedSet2.put(2,0); // Transaction 2 : moves out
//        cursorActive = c.moveToFirst();
//        while(cursorActive){
//            int idIndex = c.getColumnIndex("bag_transaction._id");
//            int mIndex = c.getColumnIndex("movement");
//            int id = c.getInt(idIndex);
//            Integer expected = expectedSet2.get(id);
//            if (expected != null) {
//                assertTrue(expected.equals(c.getInt(mIndex)));
//            }else{
//                assertTrue( "EXPECTEDSET != cursor for items query", false); //autofail.
//            }
//            cursorActive = c.moveToNext();
//        }
//
//        // Test for hat
//        uri = TransactionEntry.buildTransactionWithItemUri(hatId);
//        c = cr.query(uri, new String[]{"bag_transaction._id", "coord_long", "coord_lat", "movement", "transaction_time"},
//                null, null, null);
//        c.moveToFirst();
//        assertTrue(1 == c.getInt(c.getColumnIndex("movement")) &&
//                1 == c.getInt(c.getColumnIndex("bag_transaction._id")));
//
//        /*
//                   ITEM_TRANSACTION_SINGLE
//         */
//        uri = ItemTransactionEntry.buildItemTransactionUri(transactionId);
//        c = cr.query(uri, new String[]{"item_name", "tag_id"}, null, null, null);
//        HashSet<String> hs = new HashSet<>();
//        hs.add("Baseball");
//        hs.add("Hat");
//        hs.add("Charger");
//        c.moveToFirst();
//        do {
//            hs.remove(c.getString(0));
//        }while(c.moveToNext());
//        assertTrue("All Items not found in itemTransaction_single", hs.isEmpty());
//
//        uri = ItemTransactionEntry.buildItemTransactionUri(transactionId2);
//        c = cr.query(uri, new String[]{"item_name","tag_id"} , null, null, null);
//        hs.add("Baseball");
//        hs.add("Charger");
//        c.moveToFirst();
//        do {
//            hs.remove(c.getString(0));
//        }while(c.moveToNext());
//        assertTrue("All Items not found in itemTransaction_single", hs.isEmpty());
    }


    public long quickItemCreate(String tagID, String name, long currentbag, String resourseName){
        Uri insertUri = ItemEntry.CONTENT_URI;
        ContentValues cv = ItemEntry.createContentValues(tagID, name, currentbag, "Put this on your head to stay warm",
                System.currentTimeMillis(), Color.RED, resourseName,1,null);
        Uri uri = cr.insert(insertUri, cv);
        Cursor c = cr.query(uri, null, null, null, null);
        TestUtilities.validateCursor("Bad Item Cursor ", c, cv);
        c.close();
        return Long.parseLong(ItemEntry.getItemIdfromUri(uri));
    }

    public long quickBagCreate(String bagName, String drawableName, double lat, double lng){
        Uri insertUri = BagEntry.CONTENT_URI;
        ContentValues cv = BagEntry.createContentValues(bagName, drawableName, lat, lng,BagEntry.NO_IMAGE,null);
        Uri uri = cr.insert(insertUri, cv);
        Cursor c = cr.query(uri, null, null, null, null);
        TestUtilities.validateCursor("Bad Item Cursor ", c, cv);
        c.close();
        return Long.parseLong(BagEntry.getBagIdfromUri(uri));
    }
    public long quickTransactionCreate(long bagId, double lat, double lng){
        ContentValues transactionValues = TransactionEntry.createContentValues(
                lat, lng, System.currentTimeMillis(), bagId);
        Uri uri = cr.insert(TransactionEntry.CONTENT_URI, transactionValues);
        Cursor c = cr.query(uri, null, null, null, null);
        TestUtilities.validateCursor("Bad TransactionValues ", c, transactionValues);
        return Long.parseLong(TransactionEntry.getTransactionIDFromUri(uri));
    }

    public long quickItemTransaction(long itemId, long transactionId, int moveDirection , long bagID){
        ContentValues myTransaction =  ItemTransactionEntry.createContentValues(
                itemId, transactionId, System.currentTimeMillis(), moveDirection);
        Uri uri = cr.insert(ItemTransactionEntry.CONTENT_URI, myTransaction);
        Cursor c = cr.query(uri, null, null, null, null);
        TestUtilities.validateCursor("baseball transaction failed ", c, myTransaction);

        if (moveDirection == 0)
            bagID = 0L;

        quickUpdateItem(itemId, bagID);
        return Long.parseLong(ItemTransactionEntry.getIdFromUri(uri));
    }

    public void quickUpdateItem(long itemId, long bagId){
        ContentValues cvs = new ContentValues();
        cvs.put(ItemEntry.COLUMN_CURRENT_BAG,bagId);
        Uri uri = ItemEntry.buildItemUriById(itemId);
        cr.update(uri,cvs,null,null);
    }



}
