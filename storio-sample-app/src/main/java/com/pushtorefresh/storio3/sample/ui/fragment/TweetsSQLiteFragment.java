package com.pushtorefresh.storio3.sample.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sample.R;
import com.pushtorefresh.storio3.sample.SampleApp;
import com.pushtorefresh.storio3.sample.db.entities.Tweet;
import com.pushtorefresh.storio3.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio3.sample.sample_code.Relations;
import com.pushtorefresh.storio3.sample.ui.UiStateController;
import com.pushtorefresh.storio3.sample.ui.adapter.TweetsAdapter;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.pushtorefresh.storio3.sample.db.DBQueries.QUERY_ALL;
import static com.pushtorefresh.storio3.sample.ui.Toasts.safeShowShortToast;
import static io.reactivex.BackpressureStrategy.LATEST;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TweetsSQLiteFragment extends BaseFragment implements TweetsAdapter.OnUpdateTweetListener {

    // In this sample app we use dependency injection (DI) to keep the code clean
    // Just remember that it's already configured instance of StorIOSQLite from DbModule
    @Inject
    StorIOSQLite storIOSQLite;

    UiStateController uiStateController;

    @Bind(R.id.tweets_recycler_view)
    RecyclerView recyclerView;

    TweetsAdapter tweetsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentActivity activity = getActivity();
        SampleApp.get(activity).appComponent().inject(this);
        tweetsAdapter = new TweetsAdapter(LayoutInflater.from(activity), this);
        new Relations(storIOSQLite).getTweetWithUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tweetsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        uiStateController = new UiStateController.Builder()
                .withLoadingUi(view.findViewById(R.id.tweets_loading_ui))
                .withErrorUi(view.findViewById(R.id.tweets_error_ui))
                .withEmptyUi(view.findViewById(R.id.tweets_empty_ui))
                .withContentUi(recyclerView)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadData();
    }

    void reloadData() {
        uiStateController.setUiStateLoading();

        final Disposable disposable = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(QUERY_ALL)
                .prepare()
                .asRxFlowable(LATEST) // it will be subscribed to changes in tweets table!
                .delay(1, SECONDS) // for better User Experience :) Actually, StorIO is so fast that we need to delay emissions (it's a joke, or not)
                .observeOn(mainThread())
                .subscribe(new Consumer<List<Tweet>>() {
                    @Override
                    public void accept(List<Tweet> tweets) {
                        // Remember: subscriber will automatically receive updates
                        // Of tables from Query (tweets table in our case)
                        // This makes your code really Reactive and nice!

                        // We guarantee, that list of objects will never be null (also we use @NonNull/@Nullable)
                        // So you just need to check if it's empty or not
                        if (tweets.isEmpty()) {
                            uiStateController.setUiStateEmpty();
                            tweetsAdapter.setTweets(Collections.<Tweet>emptyList());
                        } else {
                            uiStateController.setUiStateContent();
                            tweetsAdapter.setTweets(tweets);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        // In cases when you are not sure that query will be successful
                        // You can prevent crash of the application via error handler
                        Timber.e(throwable, "reloadData()");
                        uiStateController.setUiStateError();
                        tweetsAdapter.setTweets(Collections.<Tweet>emptyList());
                    }
                });
        // Preventing memory leak (other rx operations: Put, Delete emit result once so memory leak won't live long)
        // Because io.reactivex.Flowable from Get Operation is endless (it watches for changes of tables from query)
        // You can easily create memory leak (in this case you'll leak the Fragment and all it's fields)
        // So please, PLEASE manage your subscriptions
        // We suggest same mechanism via storing all disposables that you want to dispose
        // In something like CompositeSubscription and dispose them in appropriate moment of component lifecycle
        disposeOnStop(disposable);
    }

    @OnClick(R.id.tweets_empty_ui_add_tweets_button)
    void addTweets() {
        final List<Tweet> tweets = new ArrayList<Tweet>();

        tweets.add(Tweet.newTweet("artem_zin", "Checkout StorIO — modern API for SQLiteDatabase & ContentResolver"));
        tweets.add(Tweet.newTweet("HackerNews", "It's revolution! Dolphins can write news on HackerNews with our new app!"));
        tweets.add(Tweet.newTweet("AndroidDevReddit", "Awesome library — StorIO"));
        tweets.add(Tweet.newTweet("Facebook", "Facebook community in Twitter is more popular than Facebook community in Facebook and Instagram!"));
        tweets.add(Tweet.newTweet("Google", "Android be together not the same: AOSP, AOSP + Google Apps, Samsung Android"));
        tweets.add(Tweet.newTweet("Reddit", "Now we can send funny gifs directly into your brain via Oculus Rift app!"));
        tweets.add(Tweet.newTweet("ElonMusk", "Tesla Model S OTA update with Android Auto 5.2, fixes for memory leaks"));
        tweets.add(Tweet.newTweet("AndroidWeekly", "Special issue #1: StorIO — forget about SQLiteDatabase, ContentResolver APIs, ORMs suck!"));
        tweets.add(Tweet.newTweet("Apple", "Yosemite update: fixes for Wifi issues, yosemite-wifi-patch#142"));

        // Looks/reads nice, isn't it?
        storIOSQLite
                .put()
                .objects(tweets)
                .prepare()
                .asRxCompletable()
                .observeOn(mainThread()) // The default scheduler is Schedulers.io(), all rx operations in StorIO already subscribed on this scheduler, you just need to set observeOn()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        // After successful Put Operation our subscriber in reloadData() will receive update!
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        safeShowShortToast(getActivity(), R.string.tweets_add_error_toast);
                    }
                });
    }

    /**
     * This method from {@link com.pushtorefresh.storio3.sample.ui.adapter.TweetsAdapter.OnUpdateTweetListener}
     * interface.
     * It updates specific tweet by adding '+' to the end of tweet author
     * every time when is called.
     * It has chain of 3 steps in ReactiveX-way:
     * 1. getting tweet via its id
     * 2. mapping with changing author
     * 3. putting result back to database
     */
    @Override
    public void onUpdateTweet(@NonNull final Long tweetId) {
        // 1.
        storIOSQLite
                .get()
                .object(Tweet.class)
                .withQuery(Query.builder()
                        .table(TweetsTable.TABLE)
                        .where(TweetsTable.COLUMN_ID + " = ?")
                        .whereArgs(tweetId)
                        .build())
                .prepare()
                .asRxSingle()
                // 2.
                .map(new Function<Optional<Tweet>, Optional<Tweet>>() {
                    @Override
                    @NonNull
                    public Optional<Tweet> apply(@NonNull Optional<Tweet> tweet) {
                        // We can get empty optional in parameter so we check it
                        return tweet.isPresent()
                                ? Optional.of(Tweet.newTweet(tweetId, tweet.get().author() + "+", tweet.get().content()))
                                : tweet;
                    }
                })
                // 3.
                .flatMap(new Function<Optional<Tweet>, Single<?>>() {
                    @Override
                    @NonNull
                    public Single<?> apply(@NonNull Optional<Tweet> tweet) {
                        return storIOSQLite
                                .put()
                                .object(tweet.get())
                                .prepare()
                                .asRxSingle();
                    }
                })
                // Let Subscriber run in Main Thread e.g. for Toast
                .observeOn(mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        // Just for curiosity )
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
