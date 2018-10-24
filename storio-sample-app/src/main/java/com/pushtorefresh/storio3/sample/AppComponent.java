package com.pushtorefresh.storio3.sample;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.sample.db.DbModule;
import com.pushtorefresh.storio3.sample.many_to_many_sample.ManyToManyActivity;
import com.pushtorefresh.storio3.sample.provider.ContentResolverModule;
import com.pushtorefresh.storio3.sample.provider.SampleContentProvider;
//import com.pushtorefresh.storio3.sample.sqldelight.SqlDelightActivity;
import com.pushtorefresh.storio3.sample.ui.fragment.TweetsContentResolverFragment;
import com.pushtorefresh.storio3.sample.ui.fragment.TweetsSQLiteFragment;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DbModule.class,
                ContentResolverModule.class
        }
)
public interface AppComponent {

    void inject(@NonNull TweetsSQLiteFragment fragment);

    void inject(@NonNull TweetsContentResolverFragment fragment);

    void inject(@NonNull ManyToManyActivity manyToManyActivity);

//    void inject(@NonNull SqlDelightActivity sqlDelightActivity);
//
    void inject(@NonNull SampleContentProvider sampleContentProvider);

    @NonNull
    StorIOSQLite storIOSQLite();
}
