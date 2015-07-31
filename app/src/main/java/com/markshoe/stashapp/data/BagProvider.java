package com.markshoe.stashapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by shoe on 15-05-13."
 *
 * Bag Content Provider
 */




public class BagProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher(); // URI Matcher
    private BagDbHelper mOpenHelper;

    public static final int TRANSACTIONS = 100;  // plain transaction table
    public static final int TRANSACTIONS_WITH_ITEM = 101;// Transactions with an item (ItemDetails Activity)
    public static final int TRANSACTIONS_BY_ID = 102; // Retrieve transaction row by ID

    public static final int ITEM_TRANSACTION = 200; // item_transaction for inserting
    public static final int ITEM_TRANSACTION_BY_ID = 201; // plain item_transaction table
    public static final int ITEM_TRANSACTION_SINGLE = 202; // All Items for 1 transaction (Transaction Log)

    public static final int ITEMS = 300; // Returns all item info + latest transaction id /info
    public static final int ITEM_BY_ID = 301; // Retrieve row of item table by id
    public static final int ITEM_LOCATIONS = 302; // Retrieve all item locations

    public static final int BAG = 400; // Return all the bags and the number of items they have
    public static final int BAG_BY_ID = 401; // Return a bag with the id (Single row)
    public static final int BAG_ITEMS = 402; // Return all the items in a certain bag.


    /* URI matchers will make our code very simple from the front end. This way we can simply just
       build a URI and send it to this content provider and our content provider will match the
       URI to a query in our system that identifies with one of the 4 transactions seen above.   */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BagContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "bag", BAG);
        matcher.addURI(authority, "bag/id/", BAG_BY_ID);
        matcher.addURI(authority, "bag/items/", BAG_ITEMS);
        matcher.addURI(authority, "bag_transaction", TRANSACTIONS);
        matcher.addURI(authority, "bag_transaction/#", TRANSACTIONS_WITH_ITEM);
        matcher.addURI(authority, "item_transaction/#", ITEM_TRANSACTION_SINGLE);
        matcher.addURI(authority, "item", ITEMS);
        matcher.addURI(authority, "item/id/", ITEM_BY_ID);
        matcher.addURI(authority, "item/locations",ITEM_LOCATIONS);
        matcher.addURI(authority, "bag_transaction/id/", TRANSACTIONS_BY_ID);
        matcher.addURI(authority, "item_transaction", ITEM_TRANSACTION);
        matcher.addURI(authority, "item_transaction/id/", ITEM_TRANSACTION_BY_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new BagDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BAG:
                return BagContract.BagEntry.CONTENT_TYPE;
            case BAG_BY_ID:
                return BagContract.BagEntry.CONTENT_ITEM_TYPE;
            case TRANSACTIONS:
                return BagContract.TransactionEntry.CONTENT_TYPE;
            case TRANSACTIONS_WITH_ITEM:
                return BagContract.TransactionEntry.CONTENT_TYPE;
            case ITEM_TRANSACTION_SINGLE:
                return BagContract.ItemTransactionEntry.CONTENT_TYPE;
            case ITEMS:
                return BagContract.ItemEntry.CONTENT_TYPE;
            case TRANSACTIONS_BY_ID:
                return BagContract.TransactionEntry.CONTENT_ITEM_TYPE;
            case ITEM_TRANSACTION_BY_ID:
                return BagContract.ItemTransactionEntry.CONTENT_ITEM_TYPE;
            case ITEM_BY_ID:
                return BagContract.ItemEntry.CONTENT_ITEM_TYPE;
            case ITEM_TRANSACTION:
                return BagContract.ItemTransactionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    // -----------------------------------C R U D operations------------------------------------- //
    // CREATE
    // ------------------------------------------------------------------------------------------ //

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case BAG:{
                long id = db.insert(BagContract.BagEntry.TABLE_NAME, null, values);
                if ( id > 0 ){
                    returnUri = BagContract.BagEntry.buildBagUriById(id);
                }
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break; }
            case TRANSACTIONS:{ // Insert into "transaction" table
                long id = db.insert(BagContract.TransactionEntry.TABLE_NAME, null, values);
                if ( id > 0 ){
                    returnUri = BagContract.TransactionEntry.buildTransactionByID(id);
                }
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break; }
            case ITEM_TRANSACTION:{// Insert into "item_transaction" table
                long id = db.insert(BagContract.ItemTransactionEntry.TABLE_NAME, null, values);
                if ( id > 0 ){
                    returnUri = BagContract.ItemTransactionEntry.buildItemTransactionUriById(id);
                }
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break; }
            case ITEMS:{             // Insert into "item" table
                long id = db.insert(BagContract.ItemEntry.TABLE_NAME, null, values);
                if ( id > 0 ){
                    returnUri = BagContract.ItemEntry.buildItemUriById(id);
                }
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break; }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    // -----------------------------------C R U D operations------------------------------------- //
    // READ
    // ------------------------------------------------------------------------------------------ //
    @Override
    public Cursor query(Uri uri, String[] proj, String select, String[] sArgs, String sortOrder) {
        /*  Note that we only really care about the uri , projection and sort order.
            Our Uri handles alot of the information that we need to send to the db query  */
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        String formatedProjection = formatString(proj);
        switch (match) {
            case TRANSACTIONS: {
                String[] args = { };
                String query = " SELECT * " +
                        " FROM bag_transaction a "+
                        " INNER JOIN ( " +
                        "   SELECT item_id, COUNT(*) " +
                        "   FROM item_transaction " +
                        "   GROUP BY item_id) AS b ON a._id = b.transaction_id";
                retCursor = db.rawQuery(query, args);
                break; }
            case TRANSACTIONS_BY_ID:{ // uri = "transactions/id/*
                String[] args = {BagContract.TransactionEntry.getTransactionIDFromUri(uri)};
                String query =  "SELECT " + formatedProjection +
                        " FROM bag_transaction a " +
                        " WHERE a._id = ?";
                retCursor = db.rawQuery(query, args);
                break; }
            case ITEM_TRANSACTION_BY_ID:{ // uri = "item_transaction/id/*"
                String[] args = {BagContract.ItemTransactionEntry.getIdFromUri(uri)};
                String query =  "SELECT " + formatedProjection +
                        " FROM item_transaction a " +
                        " WHERE a._id = ? ";
                retCursor = db.rawQuery(query, args);
                break; }
            case ITEM_BY_ID: { // uri = "item/id/*"
                String[] args = {BagContract.ItemEntry.getItemIdfromUri(uri)};
                String query =  "SELECT " + formatedProjection +
                        " FROM item a " +
                        " WHERE a._id = ?";
                retCursor = db.rawQuery(query, args);
                break; }
            case TRANSACTIONS_WITH_ITEM:{   // uri = "transactions/item_id"
                String[] args = {BagContract.TransactionEntry.getItemIDFromUri(uri)};
                String query =  "SELECT " + formatedProjection +
                                " FROM item_transaction " +
                                " INNER JOIN bag_transaction  " +
                                " ON item_transaction.transaction_id = bag_transaction._id" +
                                " WHERE item_transaction.item_id = ?";
                retCursor = db.rawQuery(query, args);
                break; }
            case ITEM_TRANSACTION_SINGLE:{ // uri = "item_transaction/transaction_id"
                String[] args = {BagContract.ItemTransactionEntry.getTransactionIDFromUri(uri)};
                String query =  "SELECT " + formatedProjection +
                                " FROM item a " +
                                " INNER JOIN item_transaction b " +
                                " ON a._id = b.item_id" +
                                " WHERE b.transaction_id = ?";
                retCursor = db.rawQuery(query, args);
                break; }
            case ITEMS:{
                String[] args = { };
                String query = "SELECT " + formatedProjection +
                                "FROM item";
                retCursor = db.rawQuery(query, args);
                break; }
            case ITEM_LOCATIONS:{
                String[] args = { };
                String query =  "   SELECT " +  formatedProjection +
                        "   FROM item a" +
                        "   INNER JOIN ( " +
                        "       SELECT transaction_id, item_id" +
                        "       FROM item_transaction "+
                        "       GROUP BY item_id " +
                        "       ORDER BY MAX(item_transaction_time) desc " +
                        "       ) AS b ON a._id = b.item_id" +
                        "   INNER JOIN ( " +
                        "       SELECT coord_lat as trans_lat, coord_long as trans_lng, _id " +
                        "       FROM bag_transaction "+
                        "       ) AS c ON c._id = b.transaction_id" +
                        "   LEFT OUTER JOIN bag" +
                        "   ON bag._id = a.current_bag_key";
                retCursor = db.rawQuery(query, args);
                break; }
            case BAG_BY_ID:{
                String[] args = { BagContract.BagEntry.getBagIdfromUri(uri)};
                String query =
                            "SELECT " + formatedProjection +
                            " FROM bag a " +
                            " WHERE a._id = ?";
                retCursor = db.rawQuery(query, args);
                break; }
            case BAG: {
                String[] args = { };
                String query =
                        "SELECT current_bag_key, bag_name, bag_res_name, count(current_bag_key) as numOfItems " +
                        "FROM item " +
                        " LEFT OUTER JOIN ( SELECT bag_name, bag_res_name, _id as bag_id" +
                        " FROM bag ) on bag_id = current_bag_key " +
                        "GROUP BY current_bag_key " +
                        "ORDER BY current_bag_key";
                retCursor = db.rawQuery(query, args);
                break; }
            case BAG_ITEMS:{
                String[] args = {BagContract.BagEntry.getBagIdfromUri(uri)};
                String query =
                        "   SELECT " +  formatedProjection +
                        "   FROM item a " +
                        "   WHERE current_bag_key = ? " +
                        "   ORDER BY item_color ";
                retCursor = db.rawQuery(query, args);
                break; }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri); //notifys if db change
        return retCursor;
    }

    // -----------------------------------C R U D operations------------------------------------- //
    // UPDATE
    // ------------------------------------------------------------------------------------------ //
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (match) {
            case ITEM_BY_ID:{
                String[] args = { BagContract.ItemEntry.getItemIdfromUri(uri)};
                db.update(BagContract.ItemEntry.TABLE_NAME, values, "_id = ?", args);
                break; }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    // The URI Matcher used by this content provider.



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    private String formatString(String[] A){  // Function used for projection in SQL querys
        if (A == null || A.length == 0) return " * ";
        String s=" ";
        for (int i=0; i<A.length ; i++){
            if (i<A.length-1){
                s= s.concat(A[i]).concat(", ");
            } else {
                s= s.concat(A[i]);
            }
        }
        s= s.concat(" ");
        return s;
    }


}
