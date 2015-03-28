package com.pushtorefresh.storio.sample.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutCollectionResult;
import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.SampleApp;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.ui.DividerItemDecoration;
import com.pushtorefresh.storio.sample.ui.ToastHelper;
import com.pushtorefresh.storio.sample.ui.UiStateController;
import com.pushtorefresh.storio.sample.ui.adapter.TweetsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TweetsFragment extends BaseFragment {

    @Inject
    StorIOSQLiteDb storIOSQLiteDb;

    UiStateController uiStateController;

    @InjectView(R.id.tweets_recycler_view)
    RecyclerView recyclerView;

    TweetsAdapter tweetsAdapter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApp.get(getActivity()).getDependencyGraph().inject(this);
        tweetsAdapter = new TweetsAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweets, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tweetsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        uiStateController = new UiStateController.Builder()
                .withLoadingUi(view.findViewById(R.id.tweets_loading_ui))
                .withErrorUi(view.findViewById(R.id.tweets_error_ui))
                .withEmptyUi(view.findViewById(R.id.tweets_empty_ui))
                .withContentUi(recyclerView)
                .build();
    }

    @Override public void onStart() {
        super.onStart();
        reloadData();
    }

    void reloadData() {
        uiStateController.setUiStateLoading();

        final Subscription subscription = storIOSQLiteDb
                .get()
                .listOfObjects(Tweet.class)
                .withMapFunc(Tweet.MAP_FROM_CURSOR)
                .withQuery(Tweet.GET_ALL_QUERY)
                .prepare()
                .createObservableStream() // it will be subscribed to changes in tweets table!
                .delay(1, TimeUnit.SECONDS) // for better User Experience
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Tweet>>() {
                    @Override public void onError(Throwable e) {
                        uiStateController.setUiStateError();
                        tweetsAdapter.setTweets(null);
                    }

                    @Override public void onNext(List<Tweet> tweets) {
                        if (tweets.isEmpty()) {
                            uiStateController.setUiStateEmpty();
                            tweetsAdapter.setTweets(null);
                        } else {
                            uiStateController.setUiStateContent();
                            tweetsAdapter.setTweets(tweets);
                        }
                    }

                    @Override public void onCompleted() {
                        // no impl
                    }
                });

        unsubscribeOnStop(subscription); // preventing memory leak
    }

    @OnClick(R.id.tweets_empty_ui_add_tweets_button)
    void addTweets() {
        final List<Tweet> tweets = new ArrayList<>();

        tweets.add(Tweet.newTweet("artem_zin", "Checkout StorIO — modern API for SQLiteDatabase & ContentProvider"));
        tweets.add(Tweet.newTweet("HackerNews", "It's revolution! Dolphins can write news on HackerNews with our new app!"));
        tweets.add(Tweet.newTweet("AndroidDevReddit", "Awesome library — StorIO"));
        tweets.add(Tweet.newTweet("Facebook", "Facebook community in Twitter is more popular than Facebook community in Facebook and Instagram!"));
        tweets.add(Tweet.newTweet("Google", "Android be together not the same: AOSP, AOSP + Google Apps, Samsung Android"));
        tweets.add(Tweet.newTweet("Reddit", "Now we can send funny gifs directly into your brain via Oculus Rift app!"));
        tweets.add(Tweet.newTweet("ElonMusk", "Tesla Model S OTA update with Android Auto 5.2, fixes for memory leaks"));
        tweets.add(Tweet.newTweet("AndroidWeekly", "Special issue #1: StorIO — forget about SQLiteDatabase, ContentProvider APIs, ORMs sucks!"));
        tweets.add(Tweet.newTweet("Apple", "Yosemite update: fixes for Wifi issues, yosemite-wifi-patch#142"));

        storIOSQLiteDb
                .put()
                .objects(tweets)
                .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Tweet.PUT_RESOLVER)
                .prepare()
                .createObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PutCollectionResult<Tweet>>() {
                    @Override public void onError(Throwable e) {
                        ToastHelper.safeShowShortToast(getActivity(), R.string.tweets_add_error_toast);
                    }

                    @Override
                    public void onNext(PutCollectionResult<Tweet> tweetPutCollectionResult) {
                        // handled via reactive stream! see reloadData()
                    }

                    @Override public void onCompleted() {
                        // no impl
                    }
                });
    }
}
