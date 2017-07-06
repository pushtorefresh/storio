package com.pushtorefresh.storio.sample;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.DbModule;
import com.pushtorefresh.storio.sample.many_to_many_sample.ManyToManyActivity;
import com.pushtorefresh.storio.sample.provider.SampleContentProvider;
import com.pushtorefresh.storio.sample.ui.fragment.TweetsFragment;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

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

    void inject(@NonNull SampleContentProvider sampleContentProvider);

    @NonNull
    StorIOSQLite storIOSQLite();
}
