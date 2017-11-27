package com.pushtorefresh.storio3.sample.ui.activity;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pushtorefresh.storio3.sample.R;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends ActionBarActivity {

    @Nullable
    private Toolbar toolbar;

    @NonNull
    private final CompositeDisposable compositeDisposableForOnStop = new CompositeDisposable();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void disposeOnStop(@NonNull Disposable disposable) {
        compositeDisposableForOnStop.add(disposable);
    }

    @Override
    public void onStop() {
        compositeDisposableForOnStop.clear();
        super.onStop();
    }

    @Nullable
    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected boolean homeAsBackButton() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && homeAsBackButton()) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
