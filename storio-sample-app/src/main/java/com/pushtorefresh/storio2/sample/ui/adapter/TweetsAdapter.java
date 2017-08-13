package com.pushtorefresh.storio2.sample.ui.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
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

import static java.util.Collections.emptyList;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    @NonNull
    private final LayoutInflater layoutInflater;

    @NonNull
    private List<Tweet> tweets = emptyList();

    public TweetsAdapter(@NonNull LayoutInflater layoutInflater) {
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
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        holder.authorTextView.setText("@" + tweet.author());
        holder.contentTextView.setText(tweet.content());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_tweet_author)
        TextView authorTextView;

        @Bind(R.id.list_item_tweet_content)
        TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
