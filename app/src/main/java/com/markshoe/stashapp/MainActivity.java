package com.markshoe.stashapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.markshoe.stashapp.data.FakeDBCreator;

public class MainActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    private MaterialViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
        }



        mViewPager.getViewPager().setAdapter(new IconViewPagerAdapter(getSupportFragmentManager(),this,mViewPager));
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        //mViewPager.getViewPager().setCurrentItem(0);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        //Changing the menu items to black
//        for(int i = 0; i < menu.size(); i++) {
//            MenuItem item = menu.getItem(i);
//            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
//            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0); //fix the color to white
//            item.setTitle(spanString);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id ==R.id.add_item){
            Intent itemCreationActivity = new Intent(this, ItemCreationActivity.class);
            startActivity(itemCreationActivity);
//            AddItemDialogFragment addItemPopup = new AddItemDialogFragment();
//            addItemPopup.show(getFragmentManager(),"Add Item Dialog");
        }
        if (id==R.id.fake_db){
            FakeDBCreator fk = new FakeDBCreator(this);
            fk.populateDB();
        }
        if (id == R.id.add_random_item){
            FakeDBCreator fdb = new FakeDBCreator(this);
            fdb.insertRandomItem(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
