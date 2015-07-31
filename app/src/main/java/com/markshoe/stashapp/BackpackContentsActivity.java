package com.markshoe.stashapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import com.markshoe.stashapp.CustomViews.BackpackNavBar;
import com.markshoe.stashapp.fragment.BackpackItemsGridFragment;
import com.markshoe.stashapp.fragment.BackpackItemsListFragment;
import com.markshoe.stashapp.fragment.ItemsFragment;


/**
 * Created by shoe on 15-07-09.
 */
public class BackpackContentsActivity  extends AppCompatActivity  {
    long bagId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backpack_contents_layout);
        ViewGroup topLevelView = (ViewGroup) findViewById(R.id.topLevelLayout);

        EditText et = (EditText) findViewById(R.id.edit_text);
        Typeface font = Typefaces.get(this, "Roboto-Thin.ttf");
        et.setTypeface(font);
       // ImageView iv = (ImageView) findViewById(R.id.search_icon);
        //iv.setColorFilter(getResources().getColor(R.color.primary_dark));

        String bagName = "";
        Intent i = getIntent();
        if (i != null){
            bagId = i.getLongExtra("BACKPACK_ID",0);
            bagName = i.getStringExtra("BAG_NAME");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(bagName);
        }
        setFragment(R.id.list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backpack_contents_activity_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                Log.e("Shoe", "Edit Pressed");
                return false;
            case R.id.more_button:
                Log.e("Shoe","More button pressed");
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setFragment(int id){
        Bundle bundle = new Bundle();
        bundle.putLong("BACKPACK_ID", bagId);
        if (id == R.id.grid){
            BackpackItemsGridFragment gf = new BackpackItemsGridFragment();
            gf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, gf, "gridFrag")
                    .commit();
        }
        else if (id == R.id.list){
            BackpackItemsListFragment lf = new BackpackItemsListFragment();
            lf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, lf, "trans")
                    .commit();
        }
        else if (id == R.id.local){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ItemsFragment(), "it")
                    .commit();
        }
    }



//        navBar = (BackpackNavBar) findViewById(R.id.bp_nav_bar);
//        navBar.setNavBarListener(this);
//        navBar.setClicked(1);
//    class BPClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//            if (id == R.id.back_arrow){
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", 1);
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();
//            }
//        }
//    }

//    @Override
//    public void navButtonClicked(View view) {
//        int id = view.getId();
//        setFragment(id);
//    }


}



