package com.pushtorefresh.storio2.sample;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sample.db.DbModule;
import com.pushtorefresh.storio2.sample.many_to_many_sample.ManyToManyActivity;
import com.pushtorefresh.storio2.sample.ui.adapter.TweetsAdapter;
import com.pushtorefresh.storio2.sample.provider.SampleContentProvider;
import com.pushtorefresh.storio2.sample.sqldelight.SqlDelightActivity;
import com.pushtorefresh.storio2.sample.ui.fragment.TweetsFragment;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DbModule.class
        }
)
public interface AppComponent {

    void inject(@NonNull TweetsFragment fragment);

    void inject(@NonNull ManyToManyActivity manyToManyActivity);

    void inject(@NonNull TweetsAdapter adapter);

    void inject(@NonNull SqlDelightActivity sqlDelightActivity);

    void inject(@NonNull SampleContentProvider sampleContentProvider);

    @NonNull
    StorIOSQLite storIOSQLite();
}
