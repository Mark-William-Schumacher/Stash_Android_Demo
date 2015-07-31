package com.markshoe.stashapp;

import java.util.List;

/**
 * Created by shoe on 15-06-19.
 */
public class Transaction {
    String bagName;
    long transactionId;
    int bagIcon;
    String dateText;
    String latText;
    String lngText;
    List<ItemTransaction> itemTransactions;
    boolean isExpanded; // Determines whether the view is currently expanded in the layout

    public Transaction (int bagIcon,  String bagName, String dateText,
                        String latText, String lngText,List<ItemTransaction> itemTransactions, long transactionId){
        this.transactionId= transactionId;
        this.bagName=bagName;
        this.bagIcon=bagIcon;
        this.dateText=dateText;
        this.latText=latText;
        this.lngText=lngText;
        this.itemTransactions=itemTransactions;
    }


    public static class ItemTransaction extends Backpack.Item {
        int direction;
        public ItemTransaction(int resId, String title, int c,int moveDirection,long itemId){
            super(resId, title, c ,itemId);
            direction=moveDirection;
        }
    }

}
