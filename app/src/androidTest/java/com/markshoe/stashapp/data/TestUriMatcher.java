package com.markshoe.stashapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.markshoe.stashapp.data.BagContract.BagEntry;
import com.markshoe.stashapp.data.BagContract.ItemEntry;
import com.markshoe.stashapp.data.BagContract.ItemTransactionEntry;
import com.markshoe.stashapp.data.BagContract.TransactionEntry;

/**
 * Created by shoe on 15-05-19.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static Uri TRANSACTIONS = TransactionEntry.buildAllTransactionsUri();
    private static Uri TRANSACTIONS_WITH_ITEM = TransactionEntry.buildTransactionWithItemUri(1);
    private static Uri TRANSACTIONS_BY_ID = TransactionEntry.buildTransactionByID(1);
    private static Uri ITEM_TRANSACTION_BY_ID = ItemTransactionEntry.buildItemTransactionUriById(1);
    private static Uri ITEM_TRANSACTION_SINGLE = ItemTransactionEntry.buildItemTransactionUri(1);
    private static Uri ITEMS = ItemEntry.buildAllItemsUri();
    private static Uri ITEM_BY_ID = ItemEntry.buildItemUriById(1);
    private static Uri BAG = BagEntry.CONTENT_URI;
    private static Uri BAG_BY_ID = BagEntry.buildBagUriById(1);
    private static Uri BAG_ITEMS = BagEntry.buildBagItemsUriById(1);
    private static Uri ITEM_LOCATIONS = ItemEntry.buildItemLocationsUri();

    public void testUriMatcher() {
        UriMatcher testMatcher = BagProvider.buildUriMatcher();
        assertEquals("Error: TRANSACTIONS was matched incorrectly.",
                testMatcher.match(TRANSACTIONS), BagProvider.TRANSACTIONS);
        assertEquals("Error: TRANSACTIONS_WITH_ITEM was matched incorrectly.",
                testMatcher.match(TRANSACTIONS_WITH_ITEM), BagProvider.TRANSACTIONS_WITH_ITEM);
        assertEquals("Error: The TRANSACTIONS_BY_ID was matched incorrectly.",
                testMatcher.match(TRANSACTIONS_BY_ID), BagProvider.TRANSACTIONS_BY_ID);
        assertEquals("Error: The ITEM_TRANSACTION_BY_IDS was matched incorrectly.",
                testMatcher.match(ITEM_TRANSACTION_BY_ID), BagProvider.ITEM_TRANSACTION_BY_ID);
        assertEquals("Error: The ITEM_TRANSACTION_SINGLE was matched incorrectly.",
                testMatcher.match(ITEM_TRANSACTION_SINGLE), BagProvider.ITEM_TRANSACTION_SINGLE);
        assertEquals("Error: The ITEMS was matched incorrectly.",
                testMatcher.match(ITEMS), BagProvider.ITEMS);
        assertEquals("Error: The ITEM_BY_ID URI was matched incorrectly.",
                testMatcher.match(ITEM_BY_ID), BagProvider.ITEM_BY_ID);
        assertEquals("Error: The BAG URI was matched incorrectly.",
                testMatcher.match(BAG), BagProvider.BAG);
        assertEquals("Error: The BAG_BY_ID URI was matched incorrectly.",
                testMatcher.match(BAG_BY_ID), BagProvider.BAG_BY_ID);
        assertEquals("Error: The item/locations URI matched incorrectly.",
                testMatcher.match(ITEM_LOCATIONS), BagProvider.ITEM_LOCATIONS);
        assertEquals("Error: The BAG_ITEMS URI matched incorrectly.",
                testMatcher.match(BAG_ITEMS), BagProvider.BAG_ITEMS);
    }
}
