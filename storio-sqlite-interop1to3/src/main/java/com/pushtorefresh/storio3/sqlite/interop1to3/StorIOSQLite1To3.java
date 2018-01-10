package com.pushtorefresh.storio3.sqlite.interop1to3;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.functions.Consumer;
import rx.functions.Action1;

public class StorIOSQLite1To3 {

    private final Set<com.pushtorefresh.storio.sqlite.Changes> forwardedChanges1 = Collections.newSetFromMap(
            new ConcurrentHashMap<com.pushtorefresh.storio.sqlite.Changes, Boolean>()
    );

    private final Set<com.pushtorefresh.storio3.sqlite.Changes> forwardedChanges3 = Collections.newSetFromMap(
            new ConcurrentHashMap<com.pushtorefresh.storio3.sqlite.Changes, Boolean>()
    );

    public void forwardNotifications(
            @NonNull final com.pushtorefresh.storio.sqlite.StorIOSQLite sqlite1,
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite sqlite3
    ) {
        com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable(
                "forwardNotifications() requires rxJava2"
        );
        com.pushtorefresh.storio3.internal.Environment.throwExceptionIfRxJava2IsNotAvailable(
                "forwardNotifications() requires rxJava2"
        );

        sqlite1.observeChanges()
                .subscribe(new Action1<com.pushtorefresh.storio.sqlite.Changes>() {
            @Override
            public void call(@NonNull com.pushtorefresh.storio.sqlite.Changes changes1) {
                if (!forwardedChanges1.remove(changes1)) {  // Check to prevent cyclic forwarding.
                    final com.pushtorefresh.storio3.sqlite.Changes changes3ToForward
                            = convertToV3(changes1);
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
                            final com.pushtorefresh.storio.sqlite.Changes changes1ToForward
                                    = convertToV1(changes3);
                            forwardedChanges1.add(changes1ToForward);
                            sqlite1.lowLevel().notifyAboutChanges(changes1ToForward);
                        }
                    }
                });
    }

    @NonNull
    private com.pushtorefresh.storio.sqlite.Changes convertToV1(
            @NonNull com.pushtorefresh.storio3.sqlite.Changes changes3
    ) {
        return com.pushtorefresh.storio.sqlite.Changes.newInstance(
                changes3.affectedTables(),
                changes3.affectedTags()
        );
    }

    @NonNull
    private com.pushtorefresh.storio3.sqlite.Changes convertToV3(
            @NonNull com.pushtorefresh.storio.sqlite.Changes changes1
    ) {
        return com.pushtorefresh.storio3.sqlite.Changes.newInstance(
                changes1.affectedTables(),
                changes1.affectedTags()
        );
    }
}
