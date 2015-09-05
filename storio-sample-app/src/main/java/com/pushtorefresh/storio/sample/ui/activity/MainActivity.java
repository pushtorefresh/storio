package com.pushtorefresh.storio.sample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.ui.Toasts;
import com.pushtorefresh.storio.sample.ui.activity.db.TweetsSampleActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.main_db_sample)
    void openDbSample() {
        startActivity(new Intent(this, TweetsSampleActivity.class));
    }

    @OnClick(R.id.main_content_resolver_sample)
    void openContentResolverSample() {
        Toasts.safeShowShortToast(this, "Not implemented yet :(");
    }
}
