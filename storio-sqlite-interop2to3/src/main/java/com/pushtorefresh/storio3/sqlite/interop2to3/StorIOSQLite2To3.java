package com.pushtorefresh.storio3.sqlite.interop2to3;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.functions.Consumer;
import rx.functions.Action1;

public class StorIOSQLite2To3 {

    private final Set<com.pushtorefresh.storio2.sqlite.Changes> forwardedChanges2 = Collections.newSetFromMap(
            new ConcurrentHashMap<com.pushtorefresh.storio2.sqlite.Changes, Boolean>()
    );

    private final Set<com.pushtorefresh.storio3.sqlite.Changes> forwardedChanges3 = Collections.newSetFromMap(
            new ConcurrentHashMap<com.pushtorefresh.storio3.sqlite.Changes, Boolean>()
    );

    public void forwardNotifications(
            @NonNull final com.pushtorefresh.storio2.sqlite.StorIOSQLite sqlite2,
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite sqlite3
    ) {
        sqlite2.observeChanges()
                .subscribe(new Action1<com.pushtorefresh.storio2.sqlite.Changes>() {
            @Override
            public void call(@NonNull com.pushtorefresh.storio2.sqlite.Changes changes2) {
                if (!forwardedChanges2.remove(changes2)) {  // Check to prevent cyclic forwarding.
                    final com.pushtorefresh.storio3.sqlite.Changes changes3ToForward
                            = convertToV3(changes2);
                    forwardedChanges3.add(changes3ToForward);
                    sqlite3.lowLevel().notifyAboutChanges(changes3ToForward);
                }
            }
        });

        sqlite3.observeChanges(BackpressureStrategy.BUFFER)
                .subscribe(new Consumer<com.pushtorefresh.storio3.sqlite.Changes>() {
                    @Override
                    public void accept(com.pushtorefresh.storio3.sqlite.Changes changes3) throws Exception {
                        if (!forwardedChanges3.remove(changes3)) {  // Check to prevent cyclic forwarding.
                            final com.pushtorefresh.storio2.sqlite.Changes changes2ToForward
                                    = convertToV2(changes3);
                            forwardedChanges2.add(changes2ToForward);
                            sqlite2.lowLevel().notifyAboutChanges(changes2ToForward);
                        }
                    }
                });
    }

    @NonNull
    private com.pushtorefresh.storio2.sqlite.Changes convertToV2(
            @NonNull com.pushtorefresh.storio3.sqlite.Changes changes3
    ) {
        return com.pushtorefresh.storio2.sqlite.Changes.newInstance(
                changes3.affectedTables(),
                changes3.affectedTags()
        );
    }

    @NonNull
    private com.pushtorefresh.storio3.sqlite.Changes convertToV3(
            @NonNull com.pushtorefresh.storio2.sqlite.Changes changes2
    ) {
        return com.pushtorefresh.storio3.sqlite.Changes.newInstance(
                changes2.affectedTables(),
                changes2.affectedTags()
        );
    }
}
