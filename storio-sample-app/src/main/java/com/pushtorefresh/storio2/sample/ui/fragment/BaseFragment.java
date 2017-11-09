package com.pushtorefresh.storio2.sample.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.pushtorefresh.storio2.sample.SampleApp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    @NonNull
    private final CompositeDisposable compositeDisposableForOnStop = new CompositeDisposable();

    protected void disposeOnStop(@NonNull Disposable disposable) {
        compositeDisposableForOnStop.add(disposable);
    }

    @Override
    public void onStop() {
        compositeDisposableForOnStop.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SampleApp.get(getActivity()).refWatcher().watch(this);
    }
}
