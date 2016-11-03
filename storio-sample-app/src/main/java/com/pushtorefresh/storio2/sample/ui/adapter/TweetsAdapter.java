package com.pushtorefresh.storio2.sample.ui.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio2.sample.R;
import com.pushtorefresh.storio2.sample.db.entities.Tweet;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.Collections.emptyList;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    @NonNull
    private final LayoutInflater layoutInflater;

    @NonNull
    private List<Tweet> tweets = emptyList();

    @Nullable
    private final OnUpdateTweetListener listener;

    public TweetsAdapter(@NonNull LayoutInflater layoutInflater, @Nullable OnUpdateTweetListener listener) {
        this.layoutInflater = layoutInflater;
        this.listener = listener;
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
        return new ViewHolder(itemView, listener);
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

        @Nullable
        private final OnUpdateTweetListener listener;

        @NonNull
        Long id;

        @Bind(R.id.list_item_tweet_author)
        TextView authorTextView;

        @Bind(R.id.list_item_tweet_content)
        TextView contentTextView;

        public ViewHolder(@NonNull View itemView, @Nullable OnUpdateTweetListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
        }

        @OnClick(R.id.button_update)
        void updateTweet () {
            if(listener != null) {
                listener.onUpdateTweet(id);
            }
        }
    }

    // Helps reflect to button update pressing
    public interface OnUpdateTweetListener {
        void onUpdateTweet(@NonNull Long tweetId);
    }
}
