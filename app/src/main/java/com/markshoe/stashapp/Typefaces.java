package com.markshoe.stashapp;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by shoe on 15-07-30.
 * MEMORY LEAK IN 4.0 so this is the better way of creating fonts
 */
public class Typefaces{

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String name){
        synchronized(cache){
            if(!cache.containsKey(name)){
                Typeface t = Typeface.createFromAsset(
                        c.getAssets(),
                        name
                );
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }

}
