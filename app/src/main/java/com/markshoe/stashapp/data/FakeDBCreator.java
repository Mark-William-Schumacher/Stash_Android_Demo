package com.markshoe.stashapp.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;

import com.markshoe.stashapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by shoe on 15-07-04.
 */
public class FakeDBCreator {

    ContentResolver cr;
    Context c ;
    List<Long> itemIds = new ArrayList<>();

    public FakeDBCreator(Context c){
        this.c  = c;
        this.cr = c.getContentResolver();
    }

    public static List<String[]> items = new ArrayList<String[]>(){{
        add(new String[] {"Mathbook", "noun_book_62494"});
        add(new String[] {"Phone Charger", "noun_charger_8972"});
        add(new String[] {"MacBook Charger", "noun_charger_59725"});
        add(new String[] {"Learning Android v2", "noun_book_62494"});
        add(new String[] {"Iphone Charger", "noun_charger_150532"});
        add(new String[] {"Ipad Mini", "noun_ipad_5486"});
        add(new String[] {"Football", "football"});
        add(new String[] {"Neon Frisbee", "frisbee"});
        add(new String[] {"Glasses Case", "glasses"});
        add(new String[] {"Deodorant", "deodorant"});
        add(new String[] {"Electric Shaver", "nounshaver"});
        add(new String[] {"Atlanta Braves Hat", "baseballhat"});
        add(new String[] {"Contact Lenses", "contactlenses"});
        add(new String[] {"USB Stick", "usbstick"});
        add(new String[] {"Apple Ear Buds", "earphones"});
        add(new String[] {"Programming 4 dummies", "textbooks2"});
        add(new String[] {"Keyboard", "keyboard2"});
        add(new String[] {"Flask", "flask"});
        add(new String[] {"Mouse", "mouse"});
        add(new String[] {"Running Shoes", "shoes"});
    }};

    public void insertRandomItem(long bagId){
        int c = getRandomTagColor();
        String uuid = UUID.randomUUID().toString();
        Random randomGenerator = new Random();
        String[] item = items.get(randomGenerator.nextInt(items.size()));
        quickItemCreate(uuid,item[0],bagId,item[1]);
    }

    public void populateDB(){
        BagDbHelper dbhelper = new BagDbHelper(c);
        itemIds.add(quickItemCreate("ab0", "Mathbook",               0,  "noun_book_62494"));
        itemIds.add(quickItemCreate("ab1", "Phone Charger",          0,  "noun_charger_8972"));
        itemIds.add(quickItemCreate("ab2", "MacBook Charger",        0,  "noun_charger_59725"));
        itemIds.add(quickItemCreate("ab3", "Learning Android v2",    0,  "noun_book_62494"));
        itemIds.add(quickItemCreate("ab4", "Iphone Charger",         0,  "noun_charger_150532"));
        itemIds.add(quickItemCreate("ab5", "Ipad Mini",              1,  "noun_ipad_4586"));
        itemIds.add(quickItemCreate("ab6", "Football",               1,  "football"));
        itemIds.add(quickItemCreate("ab7", "Neon Frisbee",           1,  "frisbee"));
        itemIds.add(quickItemCreate("ab8", "Glasses Case",           1,  "glasses"));
        itemIds.add(quickItemCreate("ab9", "Passport",               1,  "noun_passport_10896"));
        itemIds.add(quickItemCreate("ab10", "Deodorant",             1,  "deodorant"));
        itemIds.add(quickItemCreate("ab11", "Electric Shaver",       1,  "nounshaver"));
        itemIds.add(quickItemCreate("ab12", "Atlanta Braves Hat",    2,  "baseballhat"));
        itemIds.add(quickItemCreate("ab13", "Contact Lenses",        2,  "contactlenses"));
        itemIds.add(quickItemCreate("ab14", "USB Stick",             2,  "usbstick"));
        itemIds.add(quickItemCreate("ab15", "Apple Ear Buds",        2,  "earphones"));
        itemIds.add(quickItemCreate("ab16", "Programming 4 dummies", 2,  "textbooks2"));
        itemIds.add(quickItemCreate("ab17", "Keyboard",              2,  "keyboard2"));
        itemIds.add(quickItemCreate("ab18", "Flask",                 2,  "flask"));
        itemIds.add(quickItemCreate("ab19", "Mouse",                 2,  "mouse"));
        itemIds.add(quickItemCreate("ab20", "Running Shoes",         2,  "shoes"));
        itemIds.add(quickItemCreate("ab21", "Mathbook",              2,  "noun_book_62494"));
        itemIds.add(quickItemCreate("ab22", "Phone Charger",         2,  "noun_charger_8972"));
        itemIds.add(quickItemCreate("ab23", "MacBook Charger",       2,  "noun_charger_59725"));
        itemIds.add(quickItemCreate("ab24", "Learning Android v2",   2,  "noun_book_62494"));
        itemIds.add(quickItemCreate("ab25", "Iphone Charger",        2,  "noun_charger_150532"));
        itemIds.add(quickItemCreate("ab26", "Ipad Mini",             2,  "noun_ipad_4586"));
        itemIds.add(quickItemCreate("ab627", "Football",             2,  "football"));


        long backpack1 = quickBagCreate("Main Backpack", "backpack", 43.200061, -80.005033);
        long backpack2 = quickBagCreate("Gym bag", "dufflebag", 43.202099, -80.000984);
        // 43.198429, -80.001392


        /*
                 TRANACTION_BY_ID
         */
        long transactionId = quickTransactionCreate(backpack1,33.3,33.3);


    }

    public int getRandomTagColor(){
        // Random color
        List<Integer> colors = new ArrayList<>();
        colors.add(c.getResources().getColor(R.color.tag_blue));
        colors.add(c.getResources().getColor(R.color.tag_gold));
        colors.add(c.getResources().getColor(R.color.tag_green));
        colors.add(c.getResources().getColor(R.color.tag_orange));
        colors.add(c.getResources().getColor(R.color.tag_purple));
        Random randomGenerator = new Random();
        int color = colors.get(randomGenerator.nextInt(colors.size()));
        return color;
    }


    public long quickItemCreate(String tagID, String name, long currentbag, String resourseName){
        Uri insertUri = BagContract.ItemEntry.CONTENT_URI;
        int color = getRandomTagColor();

        ContentValues cv = BagContract.ItemEntry.createContentValues(tagID, name, currentbag, "Put this on your head to stay warm",
                System.currentTimeMillis(), color, resourseName, BagContract.ItemEntry.ICON_IMAGE,null);
        Uri uri = cr.insert(insertUri, cv);
        Cursor c = cr.query(uri, null, null, null, null);
        c.close();
        return Long.parseLong(BagContract.ItemEntry.getItemIdfromUri(uri));
    }

    public long quickBagCreate(String bagName, String drawableName, double lat, double lng){
        Uri insertUri = BagContract.BagEntry.CONTENT_URI;
        ContentValues cv = BagContract.BagEntry.createContentValues(bagName, drawableName, lat, lng);
        Uri uri = cr.insert(insertUri, cv);
        Cursor c = cr.query(uri, null, null, null, null);
        c.close();
        return Long.parseLong(BagContract.BagEntry.getBagIdfromUri(uri));
    }
    public long quickTransactionCreate(long bagId, double lat, double lng){
        ContentValues transactionValues = BagContract.TransactionEntry.createContentValues(
                lat, lng, System.currentTimeMillis(), bagId);
        Uri uri = cr.insert(BagContract.TransactionEntry.CONTENT_URI, transactionValues);
        Cursor c = cr.query(uri, null, null, null, null);
        return Long.parseLong(BagContract.TransactionEntry.getTransactionIDFromUri(uri));
    }

    public long quickItemTransaction(long itemId, long transactionId, int moveDirection , long bagID){
        ContentValues myTransaction =  BagContract.ItemTransactionEntry.createContentValues(
                itemId, transactionId, System.currentTimeMillis(), moveDirection);
        Uri uri = cr.insert(BagContract.ItemTransactionEntry.CONTENT_URI, myTransaction);
        Cursor c = cr.query(uri, null, null, null, null);
        if (moveDirection == 0)
            bagID = 0L;

        quickUpdateItem(itemId, bagID);
        return Long.parseLong(BagContract.ItemTransactionEntry.getIdFromUri(uri));
    }

    public void quickUpdateItem(long itemId, long bagId){
        ContentValues cvs = new ContentValues();
        cvs.put(BagContract.ItemEntry.COLUMN_CURRENT_BAG,bagId);
        Uri uri = BagContract.ItemEntry.buildItemUriById(itemId);
        cr.update(uri,cvs,null,null);
    }


}
