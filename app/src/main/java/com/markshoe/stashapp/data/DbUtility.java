package com.markshoe.stashapp.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by shoe on 15-07-27.
 */
public class DbUtility {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap!=null) {
            bitmap.compress(CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }else{
            Log.e("Error Bitmap", "Bitmap is null");
            return new byte[0];
        }
    }

    public static byte[] getBytes(Context c , int resId){
        return DbUtility.getBytes(BitmapFactory.decodeResource(c.getResources(),resId));
    }


    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Drawable getDrawableFromByteArray(Context c , byte[] image){
        return new BitmapDrawable(c.getResources(), DbUtility.getImage(image));
    }


    public static void printCursor(Cursor c){
        String colNames = "";
        for (int i =  0 ; i <c.getColumnCount() ; i++){
            colNames = colNames.concat(c.getColumnName(i) + " ");
        }
        Log.e("XXXXXXXXX", colNames);
        String rows = "";
        boolean remaining = c.moveToFirst();
//        while (remaining) {
//            for (int i =  0 ; i <c.getColumnCount() ; i++){
//                rows = rows.concat(c.getString(i) + "     ");
//            }
//            Log.e("XXXXXXXXX", rows);
//            rows = "";
//            remaining= c.moveToNext();
//        }
    }

}