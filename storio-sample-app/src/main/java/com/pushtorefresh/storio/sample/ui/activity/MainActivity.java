package com.pushtorefresh.storio.sample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.many_to_many_sample.ManyToManyActivity;
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.many_to_many) {
            startActivity(new Intent(this, ManyToManyActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
