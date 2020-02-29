package com.laioffer.matrix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.laioffer.matrix.R;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemSelectListener{
    private ListFragment listFragment;
    private GridFragment gridFragment;

    @Override
    public void onItemSelected(int position) {
        gridFragment.onItemSelected(position);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add ListView
        if (getSupportFragmentManager().findFragmentById(R.id.list_container) == null) {
            listFragment = new ListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();
        }

        //add Gridview
        if (getSupportFragmentManager().findFragmentById(R.id.grid_container) == null && isTablet()) {
            gridFragment = new GridFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.grid_container, gridFragment).commit();
        }


    }




private boolean isTablet() {
    return (getApplicationContext().getResources().getConfiguration().screenLayout &
            Configuration.SCREENLAYOUT_SIZE_MASK) >=
            Configuration.SCREENLAYOUT_SIZE_LARGE;
}
}

