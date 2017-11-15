package com.pushtorefresh.storio2.sample.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sample.R;
import com.pushtorefresh.storio2.sample.ui.fragment.TweetsContentResolverFragment;

public class TweetsContentResolverSampleActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tweets_sample_tweets_fragment, new TweetsContentResolverFragment())
                    .commit();
        }
    }
}
