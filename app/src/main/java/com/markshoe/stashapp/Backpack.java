package com.markshoe.stashapp;

import java.util.List;

/**
 * Created by shoe on 15-06-21.
 */
public class Backpack {
    public int resId;
    public long bagId;
    public String nameOfBag;
    public List<Item> listOfItems;
    public int connectionStatus;
    public boolean isExpanded;


    public Backpack(String nameOfBag, int resId, List<Item> listOfItems, int connectionStatus,
                    boolean isExpanded,long bagId){
        this.bagId = bagId;
        this.resId = resId;
        this.nameOfBag=nameOfBag;
        this.listOfItems = listOfItems;
        this.connectionStatus=connectionStatus;
        this.isExpanded=isExpanded;
    }


    public static class Item {
        public long itemID;
        public int resId;
        public String title;
        public int color;

        public Item(int resId, String title, int c, long itemId) {
            this.itemID = itemId;
            this.resId = resId;
            this.title = title;
            this.color = c;
        }
    }
}

