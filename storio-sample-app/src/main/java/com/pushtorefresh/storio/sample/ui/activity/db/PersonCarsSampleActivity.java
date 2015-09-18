package com.pushtorefresh.storio.sample.ui.activity.db;

import android.os.Bundle;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.ui.activity.BaseActivity;

public class PersonCarsSampleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_cars_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
