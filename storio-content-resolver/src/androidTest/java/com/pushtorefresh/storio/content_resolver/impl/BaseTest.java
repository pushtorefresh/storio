package com.pushtorefresh.storio.content_resolver.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.ProviderTestCase2;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResults;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResults;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Before;

import java.util.List;

public abstract class BaseTest extends ProviderTestCase2<TestContentProvider> {

    @NonNull
    protected StorIOContentResolver storIOContentResolver;

    public BaseTest() {
        super(TestContentProvider.class, TestContentProvider.AUTHORITY);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(getMockContentResolver())
                .addTypeMapping(User.class, ContentResolverTypeMapping.<User>builder()
                        .putResolver(UserMeta.PUT_RESOLVER)
                        .getResolver(UserMeta.GET_RESOLVER)
                        .deleteResolver(UserMeta.DELETE_RESOLVER)
                        .build())
                .addTypeMapping(Tweet.class, ContentResolverTypeMapping.<Tweet>builder()
                        .putResolver(TweetMeta.PUT_RESOLVER)
                        .getResolver(TweetMeta.GET_RESOLVER)
                        .deleteResolver(TweetMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // clearing before each test case
        storIOContentResolver
                .delete()
                .byQuery(UserMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        storIOContentResolver
                .delete()
                .byQuery(TweetMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @Nullable
    List<User> getAllUsers() {
        return storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    User putUser() {
        return putUser(TestFactory.newUser());
    }

    @NonNull
    User putUser(@NonNull final User user) {
        final PutResult putResult = storIOContentResolver
                .put()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertNotNull(putResult);
        assertTrue(putResult.wasInserted());

        return user;
    }

    @NonNull
    List<User> putUsers(final int size) {
        return putUsers(TestFactory.newUsers(size));
    }

    @NonNull
    List<User> putUsers(@NonNull final List<User> users) {
        final PutResults<User> putResults = storIOContentResolver
                .put()
                .objects(users)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResults.numberOfInserts());

        return users;
    }

    @NonNull
    DeleteResult deleteUser(@NonNull final User user) {
        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        return deleteResult;
    }

    @NonNull
    DeleteResults<User> deleteUsers(@NonNull final List<User> users) {
        final DeleteResults<User> deleteResults = storIOContentResolver
                .delete()
                .objects(users)
                .prepare()
                .executeAsBlocking();

        for (User user : users) {
            assertTrue(deleteResults.wasDeleted(user));
        }
        return deleteResults;
    }

    void oneUserInStorageCheck(@NonNull final User user) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(1, usersFromStorage.size());
        assertEquals(user, usersFromStorage.get(0));
    }

    void usersInStorageCheck(@NonNull final List<User> users) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(users.size(), usersFromStorage.size());
        assertEquals(users, usersFromStorage);
    }

    void noUsersInStorageCheck() {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertTrue(usersFromStorage.isEmpty());
    }
}
