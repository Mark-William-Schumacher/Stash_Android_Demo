package com.markshoe.stashapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import com.markshoe.stashapp.data.DbUtility;

/**
 * Created by shoe on 15-05-03.
 */
public class BagContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.markshoe.stashapp";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://android.example.com.app.sunshine/weather/ is a valid path for
    // looking at weather data. content://android.example.com.app.sunshine/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_ITEM = "item";
    public static final String PATH_ITEM_TRANSACTION = "item_transaction";
    public static final String PATH_TRANSACTION = "bag_transaction";
    public static final String PATH_BAG ="bag";



    public static final class BagEntry implements BaseColumns {
        public static final int NO_IMAGE = 0;
        public static final int REAL_IMAGE = 1;
        public static final int ICON_IMAGE = 2;

        public static final String TABLE_NAME = "bag";

        // INT FIELD: Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_ID = "_id";
        // TEXT FIELD:
        public static final String COLUMN_NAME = "bag_name";
        // Float FIELD: long and lat for location
        public static final String COLUMN_COORD_LAT="coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        // TEXT FIELD: picture of the item - we are storing
        public static final String COLUMN_DRAWABLE_NAME = "bag_res_name";
        // Int Field : storing the code for which type of image to use. 0 = none , 1 = image , 2 = icon
        public static final String COLUMN_IMAGE_CODE = "bag_image_code";
        // Blob Field: storing a blob for image
        public static final String COLUMN_BLOB_IMAGE = "bag_image_blob";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BAG).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BAG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BAG;

//        /**
//         * Base URI for returning all items in our main activity Querys all items , sort by
//         * alphabetical
//         * @return The URI for getting the all the items and their status
//         */
//        public static Uri buildAllItemsUri(){
//            return CONTENT_URI;
//        }
//
//        /**
//         * "item/#" get the TransactionID from the uri for returning the LOCATION coords
//         * @param uri
//         * @return -1 if id not in parameter
//         */
//        public static String getTransactionIDfromUri(Uri uri){
//            String s = uri.getPathSegments().get(1);
//            return s;
//        }
//
        /**
         * From "bag/id/*" RETURNS a single row in the items table
         * or "bag/items/*"
         * @param uri
         * @return long of id from the uri
         */
        public static String getBagIdfromUri(Uri uri){
            String s1 = uri.getQueryParameter(COLUMN_ID);
            return s1;
        }

        /**
         * URI "item/id/*" RETURNS a single row in the items table
         * @param ItemId
         * @return uri
         */
        public static Uri buildBagUriById(long ItemId){
            String idKey = Long.toString(ItemId);
            return CONTENT_URI.buildUpon().appendEncodedPath("id/")
                    .appendQueryParameter(COLUMN_ID, idKey).build();
        }



        /**
         * URI "item/id/*" RETURNS all the items assosiated with a bag
         * @param ItemId
         * @return uri
         */
        public static Uri buildBagItemsUriById(long ItemId){
            String idKey = Long.toString(ItemId);
            return CONTENT_URI.buildUpon().appendEncodedPath("items/")
                    .appendQueryParameter(COLUMN_ID, idKey).build();
        }


        /**
         * Builder Function for creating content values to Insert into the BAG table
         * @return Content Values
         */
        public static ContentValues createContentValues
                (String name, String drawableName,double lat, double lng, int imageCode,byte[] imageBlob) {
            ContentValues BagColumns = new ContentValues();
            BagColumns.put(BagEntry.COLUMN_NAME,name);
            BagColumns.put(BagEntry.COLUMN_COORD_LAT,lat);
            BagColumns.put(BagEntry.COLUMN_COORD_LONG,lng);
            BagColumns.put(BagEntry.COLUMN_DRAWABLE_NAME,drawableName);
            BagColumns.put(BagEntry.COLUMN_IMAGE_CODE,imageCode);
            BagColumns.put(BagEntry.COLUMN_BLOB_IMAGE,imageBlob);
            return BagColumns;
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // ITEM TABLE
    // ------------------------------------------------------------------------------------------ //

    public static final class ItemEntry implements BaseColumns {
        public static final int NO_IMAGE = 0;
        public static final int REAL_IMAGE = 1;
        public static final int ICON_IMAGE = 2;

        public static final String TABLE_NAME = "item";
        public static final String LOCATION_PATH = "locations"; //  ...URI.../item/locations

        // INT FIELD: Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_ITEM_ID = "_id";
        // TEXT FIELD: Tag number of rfid
        public static final String COLUMN_TAG_ID = "tag_id";
        // TEXT FIELD: Descriptive name the user gives
        public static final String COLUMN_NAME = "item_name";
        // TEXT FIELD: Description
        public static final String COLUMN_DESC = "item_description";
        // INT Field ,the bag it is in currently  -- -1 will be reserved for not in bag
        public static final String COLUMN_CURRENT_BAG = "current_bag_key";
        // TEXT FIELD: Date Description - Used to give info
        public static final String COLUMN_DATE_DESC = "date_description";
        // TEXT FIELD: Color of tag
        public static final String COLUMN_COLOR = "item_color";
        // TEXT FIELD: picture of the item - we are storing
        public static final String COLUMN_ICON = "item_res_name";
        // Int Field : storing the code for which type of image to use. 0 = none , 1 = image , 2 = icon
        public static final String COLUMN_IMAGE_CODE = "item_image_code";
        // Blob Field: storing a blob for image
        public static final String COLUMN_BLOB_IMAGE = "item_image_blob";



        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        /**
         * Base URI for returning all items in our main activity Querys all items , sort by
         * alphabetical
         * @return The URI for getting the all the items and their status
         */
        public static Uri buildAllItemsUri(){
            return CONTENT_URI;
        }

        /**
         * "item/#" get the TransactionID from the uri for returning the LOCATION coords
         * @param uri
         * @return -1 if id not in parameter
         */
        public static String getTransactionIDfromUri(Uri uri){
            String s = uri.getPathSegments().get(1);
            return s;
        }

        /**
         * From "item/id/*" RETURNS a single row in the items table
         * @param uri
         * @return long of id from the uri
         */
        public static String getItemIdfromUri(Uri uri){
            String s1 = uri.getQueryParameter(COLUMN_ITEM_ID);
            return s1;
        }

        /**
         * URI "item/id/*" RETURNS a single row in the items table
         * @param ItemId
         * @return uri
         */
        public static Uri buildItemUriById(long ItemId){
            String idKey = Long.toString(ItemId);
            return CONTENT_URI.buildUpon().appendEncodedPath("id/")
                    .appendQueryParameter(COLUMN_ITEM_ID, idKey).build();
        }


        /**
         * All Location long and lat for all items .
         * @return
         */

        public static Uri buildItemLocationsUri(){
            return CONTENT_URI.buildUpon().appendEncodedPath(LOCATION_PATH).build();
        }

        /**
         * Builder Function for creating content values to Insert into the ITEMS table
         * @return Content Values
         */
        public static ContentValues createContentValues(
                String tagId, String name, long currentBag,
                String desc,long datedesc, int color, String iconPath, int imageCode, byte[] imageBlob) {
            ContentValues ItemColumns = new ContentValues();
            ItemColumns.put(ItemEntry.COLUMN_TAG_ID,tagId);
            ItemColumns.put(ItemEntry.COLUMN_NAME,name);
            ItemColumns.put(ItemEntry.COLUMN_DESC,desc);
            ItemColumns.put(ItemEntry.COLUMN_CURRENT_BAG, currentBag);
            ItemColumns.put(ItemEntry.COLUMN_DATE_DESC,datedesc);
            ItemColumns.put(ItemEntry.COLUMN_COLOR,color);
            ItemColumns.put(ItemEntry.COLUMN_ICON,iconPath);
            ItemColumns.put(ItemEntry.COLUMN_IMAGE_CODE,imageCode);
            ItemColumns.put(ItemEntry.COLUMN_BLOB_IMAGE,imageBlob);
            return ItemColumns;
        }
    }
    // ------------------------------------------------------------------------------------------ //
    // ------------------------------------------------------------------------------------------ //


    public static final class ItemTransactionEntry implements BaseColumns {

        public static final String TABLE_NAME = "item_transaction";

        // INT FIELD: Foreign key to Item Id in item table
        public static final String COLUMN_ITEM_KEY="item_id";
        // INT FIELD: Foreign key to tranasaction key in transaction table
        public static final String COLUMN_TRANSACTION_KEY="transaction_id";
        // INT FIELD: Epoch time
        public static final String COLUMN_TIME_EPOCH="item_transaction_time";
        // INT FIELD: movement in or out of the bag    (1 INTO the bag   0 OUT OF the bag)
        public static final String COLUMN_MOVEMENT="movement";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM_TRANSACTION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEM_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_ITEM_TRANSACTION;
        /**
         * @param transId  The id of the transaction that we are looking to explore
         * @return  The uri that provides us with a list of all the other and times belonging to the
         * transaction ID. (ALL ITEMS IN SINGLE TRANSACTION)
         */
        public static Uri buildItemTransactionUri(long transId) {
            return ContentUris.withAppendedId(CONTENT_URI, transId);
        }

        /**
         * "transaction_item/#" Gets the transactionID on the end of transaction_item/transactionID
         * @param uri
         * @return transactionID
         */
        public static String getTransactionIDFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        /**
         * "item_transaction/id/"
         * Returns the uri to get a item_transaction row
         * @param item_trans_id
         * @return The
         */
        public static Uri buildItemTransactionUriById (long item_trans_id){
            String itemIdKey = Long.toString(item_trans_id);
            return CONTENT_URI.buildUpon().appendEncodedPath("id/")
                    .appendQueryParameter(_ID, Long.toString(item_trans_id))
                    .build();
        }

        /**
         * /"item_transaction/id/*" Gets the two ids as Strings
         * @param uri
         * @return {item_id,transaction_id}
         */
        public static String getIdFromUri (Uri uri){
            String s1 = uri.getQueryParameter(_ID);
            return s1;
        }

        public static ContentValues createContentValues(long itemId, long transactionId, long epoch,
                                                          int movement){
            ContentValues ItemTransactionColumns = new ContentValues();
            ItemTransactionColumns.put(ItemTransactionEntry.COLUMN_ITEM_KEY,itemId);
            ItemTransactionColumns.put(ItemTransactionEntry.COLUMN_TRANSACTION_KEY,transactionId);
            ItemTransactionColumns.put(ItemTransactionEntry.COLUMN_MOVEMENT,movement);
            ItemTransactionColumns.put(ItemTransactionEntry.COLUMN_TIME_EPOCH,epoch);
            return ItemTransactionColumns;
        }

    }

    // ------------------------------------------------------------------------------------------ //
    // ------------------------------------------------------------------------------------------ //



    public static final class TransactionEntry implements BaseColumns {

        public static final String TABLE_NAME = "bag_transaction";

        // INT FIELD: Foreign key to tranasaction key in transaction table
        public static final String COLUMN_TRANSACTION_ID="_id";
        // Float FIELD: long and lat for location
        public static final String COLUMN_COORD_LAT="coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        // INT FIELD: Epoch time Initial time of the transaction
        public static final String COLUMN_TIME_EPOCH="transaction_time";
        // INT Field: bag id.
        public static final String COLUMN_BAG_KEY="bag_id";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =  ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;
        /**
         * /transaction see all transactions
         * @return The Uri to build a request to the content provider about all transactions
         */
        public static Uri buildAllTransactionsUri(){
            return CONTENT_URI;
        }

        /**
         * /transaction/id/transaction_id
         * @param TransId transaction id
         * @return the uri for retrieving a SINGLE row from the transaction table
         */
        public static Uri buildTransactionByID (long TransId){
            String idKey = Long.toString(TransId);
            return CONTENT_URI.buildUpon().appendEncodedPath("id/")
                    .appendQueryParameter(COLUMN_TRANSACTION_ID, idKey).build();
        }

        /**
         * /transaction/id/transaction_id
         * @param uri
         * @return the id
         */
        public static String getTransactionIDFromUri (Uri uri){
            String s = uri.getQueryParameter(COLUMN_TRANSACTION_ID);
            return s;
        }

        /**
         * /transaction/itemID Looking up all transactions for a single item ID
         * @param itemId  The ID of the item we are looking up transactions for
         * @return  The uri for retrieving all transactions belonging to an Item
         */
        public static Uri buildTransactionWithItemUri(long itemId) {
            return ContentUris.withAppendedId(CONTENT_URI, itemId);
        }

        /**
         * /transaction/itemID Looking up all transactions for a single item ID
         * @param uri
         * @return /transaction/itemID returns the itemID as a string
         */
        public static String getItemIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        /**
         * Builder function for creating the Content Values in an insert
         * @param lat
         * @param lon
         * @param time
         * @return Content Values that will insert into the Content Provider
         */
        public static ContentValues createContentValues(
                double lat, double lon, long time, long bagId){
            ContentValues TransactionColumns = new ContentValues();
            TransactionColumns.put(TransactionEntry.COLUMN_COORD_LAT, lat);
            TransactionColumns.put(TransactionEntry.COLUMN_COORD_LONG, lon);
            TransactionColumns.put(TransactionEntry.COLUMN_TIME_EPOCH, time);
            TransactionColumns.put(TransactionEntry.COLUMN_BAG_KEY, bagId);
            return TransactionColumns;
        }
    }
}


