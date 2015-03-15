package com.pushtorefresh.storio.sample;

import com.pushtorefresh.storio.sample.db.DbModule;
import com.pushtorefresh.storio.sample.ui.fragment.TweetsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DbModule.class
        }
)
public interface DependencyGraph {
    void inject(TweetsFragment fragment);
}
