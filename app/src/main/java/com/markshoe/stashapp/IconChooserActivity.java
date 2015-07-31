package com.markshoe.stashapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.markshoe.stashapp.data.BagDbHelper;

/**
 * Created by shoe on 15-07-30.
 */
public class IconChooserActivity extends AppCompatActivity {
    static final String ICON_NAME_RESULT_KEY= "ICON_NAME_RESULT_KEY";

    final String[] iconNames = new String[] {"noun_book_62494","noun_charger_8972","noun_charger_59725"
            ,"noun_book_62494","noun_charger_150532","noun_ipad_4586","football","frisbee","glasses"
            ,"noun_passport_10896","deodorant","nounshaver","baseballhat","contactlenses","usbstick"
            ,"earphones","textbooks2","keyboard2","flask","mouse","shoes"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_icon);
        ViewGroup gridView = (ViewGroup) findViewById(R.id.item_grid);

        EditText et = (EditText) findViewById(R.id.edit_text);
        Typeface font = Typefaces.get(this, "Roboto-Thin.ttf");
        et.setTypeface(font);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Icon Chooser");
        }

        for (String s: iconNames){
            int resID = getResources().getIdentifier(s, "drawable", getPackageName());
            new IconWithName(gridView,s,this,resID);
        }

    }

    class IconWithName implements View.OnClickListener{
        String name =  "";

        public IconWithName(ViewGroup vg, String name,  Context c, int resId){
            this.name = name;
            View v = getLayoutInflater().from(c).inflate(R.layout.activity_choose_icon_item, vg, false);
            ((ImageView)v.findViewById(R.id.image_view)).setImageDrawable(c.getResources().getDrawable(resId));
            v.setOnClickListener(this);
            vg.addView(v);
        }
        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra(ICON_NAME_RESULT_KEY, name);
            IconChooserActivity.this.setResult(RESULT_OK, data);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
