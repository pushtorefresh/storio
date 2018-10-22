package com.pushtorefresh.storio3.sample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pushtorefresh.storio3.sample.R;
import com.pushtorefresh.storio3.sample.many_to_many_sample.ManyToManyActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_LONG;

//import com.pushtorefresh.storio3.sample.sqldelight.SqlDelightActivity;


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
        switch (item.getItemId()) {
            case R.id.many_to_many:
                startActivity(new Intent(this, ManyToManyActivity.class));
                return true;
            case R.id.sqldelight:
//                TODO: Enable SQLDelight example after resolving https://github.com/square/sqldelight/issues/1005
//                startActivity(new Intent(this, SqlDelightActivity.class));
                Toast.makeText(this, "Temorary disabled", LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.main_db_sample)
    void openDbSample() {
        startActivity(new Intent(this, TweetsSQLiteSampleActivity.class));
    }

    @OnClick(R.id.main_content_resolver_sample)
    void openContentResolverSample() {
        startActivity(new Intent(this, TweetsContentResolverSampleActivity.class));
    }
}
