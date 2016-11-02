package com.pushtorefresh.storio2.sample.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio2.sample.R;
import com.pushtorefresh.storio2.sample.SampleApp;
import com.pushtorefresh.storio2.sample.db.entities.Tweet;
import com.pushtorefresh.storio2.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.Collections.emptyList;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    @Inject
    StorIOSQLite storIOSQLite;

    @NonNull
    private final LayoutInflater layoutInflater;

    @NonNull
    private List<Tweet> tweets = emptyList();

    public TweetsAdapter(@NonNull Context context, @NonNull LayoutInflater layoutInflater) {
        SampleApp.get(context).appComponent().inject(this);
        this.layoutInflater = layoutInflater;
    }

    public void setTweets(@NonNull List<Tweet> tweets) {
        this.tweets = tweets;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_tweet, parent, false);
        return new ViewHolder(itemView, storIOSQLite);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        holder.id = tweet.id();
        holder.authorTextView.setText("@" + tweet.author());
        holder.contentTextView.setText(tweet.content());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final StorIOSQLite storIOSQLite;

        Long id;

        @Bind(R.id.list_item_tweet_author)
        TextView authorTextView;

        @Bind(R.id.list_item_tweet_content)
        TextView contentTextView;

        public ViewHolder(@NonNull View itemView, @NonNull StorIOSQLite storIOSQLite) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * This method updates specific tweet by adding '+' to the end of tweet author
         * every time when button pressed.
         * It has chain of 3 steps in ReactiveX-way:
         * 1. getting tweet via its id
         * 2. mapping with changing author
         * 3. putting result back to database
         */
        @OnClick(R.id.button_update)
        void updateTweet () {

            // 1.
            storIOSQLite
                    .get()
                    .object(Tweet.class)
                    .withQuery(Query.builder()
                            .table(TweetsTable.TABLE)
                            .where(TweetsTable.COLUMN_ID + " = ?")
                            .whereArgs(id)
                            .build())
                    .prepare()
                    .asRxSingle()
                    // 2.
                    .map(tweet -> Tweet.newTweet(id, tweet.author() + "+", tweet.content()))
                    // 3.
                    .flatMap(tweet -> storIOSQLite
                            .put()
                            .object(tweet)
                            .prepare()
                            .asRxSingle())
                    .subscribe();
        }
    }
}
