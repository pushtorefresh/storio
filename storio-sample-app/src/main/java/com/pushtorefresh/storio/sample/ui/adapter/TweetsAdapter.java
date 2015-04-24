package com.pushtorefresh.storio.sample.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.db.entity.Tweet;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> tweets;

    public void setTweets(@Nullable List<Tweet> tweets) {
        this.tweets = tweets;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets == null ? 0 : tweets.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tweet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        holder.authorTextView.setText("@" + tweet.author());
        holder.contentTextView.setText(tweet.content());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.list_item_tweet_author)
        TextView authorTextView;

        @InjectView(R.id.list_item_tweet_content)
        TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
