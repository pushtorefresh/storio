package com.pushtorefresh.storio.sample.ui.activity.db;

import android.os.Bundle;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.ui.activity.BaseActivity;

public class QueenAntsSampleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queen_ants_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
