package com.pushtorefresh.storio.basic_sample;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private @Nullable List<Tweet> tweets;

    public void setTweets(@Nullable List<Tweet> tweets) {
        this.tweets = tweets;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets == null ? 0 : tweets.size();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tweet, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        holder.authorTextView.setText("@" + tweet.author());
        holder.contentTextView.setText(tweet.content());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private @NonNull final TextView authorTextView;

        private @NonNull final TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.list_item_tweet_author);
            contentTextView = (TextView) itemView.findViewById(R.id.list_item_tweet_content);
        }
    }
}
