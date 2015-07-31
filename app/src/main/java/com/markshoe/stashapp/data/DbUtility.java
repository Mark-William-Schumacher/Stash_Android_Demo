package com.markshoe.stashapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
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
            return new byte[0];
        }
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}