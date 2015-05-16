package com.pushtorefresh.storio.sample.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.pushtorefresh.storio.sample.SampleApp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {

    @NonNull
    private final CompositeSubscription compositeSubscriptionForOnStop = new CompositeSubscription();

    protected void unsubscribeOnStop(@NonNull Subscription subscription) {
        compositeSubscriptionForOnStop.add(subscription);
    }

    @Override
    public void onStop() {
        compositeSubscriptionForOnStop.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SampleApp.get(getActivity()).refWatcher().watch(this);
    }
}
