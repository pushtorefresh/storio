#### StorIO — modern API for SQLiteDatabase and ContentProvider

#####Overview:
* Powerful set of Operations: `Put`, `Get`, `Delete`
* Convenient builders. Forget about 6-7 `null` in queries
* No reflection, no annotations, `StorIO` is not ORM
* Every Operation over `StorIO` can be executed as blocking call or as `rx.Observable`
* `RxJava` as first class citizen, but it's not required dependency!
* `Observable` from `Get` Operation can observe changes in `StorIO` and receive updates automatically
* If you don't want to work with `Cursor` and `ContentValue` you don't have to
* `StorIO` can replace `Loaders`
* `StorIO` is mockable for testing

###StorIODb — API for Database

####0. Create an instance of StorIODb

```java
StorIODb storIODb = new StorIOSQLiteDbImpl.Builder()
  .sqliteOpenHelper(yourSqliteOpenHelper) // or .db(db)
  .build();
```

It's a good practice to use one instance of `StorIODb` per database.

####1. Get Operation
######Get list of objects with blocking call:

```java
// it's a good practice to store MapFunc as public static final field somewhere
final MapFunc<Cursor, Tweet> mapFunc = new MapFunc<Cursor, Tweet>() {
  @Override public Tweet map(Cursor cursor) {
    // no need to move cursor and close it, StorIO will handle it for you
    return new Tweet(); // fill with values from cursor 
  }
};

final List<Tweet> tweets = storIODb
  .get()
  .listOfObjects(Tweet.class)
  .withMapFunc(mapFunc)
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .executeAsBlocking();
```

######Get `Cursor` via blocking call:

```java
final Cursor tweetsCursor = storIODb
  .get()
  .cursor()
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .executeAsBlocking();
```

Things become much more interesting with `RxJava`!

######Get cursor as `Observable`
```java
storIODb
  .get()
  .cursor()
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .createObservable()
  .subscribeOn(Schedulers.io()) // moving Get Operation to other thread
  .observeOn(AndroidSchedulers.mainThread()) // observing result on Main thread
  .subscribe(new Action1<Cursor>() {
    @Override public void call(Cursor cursor) {
      // display the data from cursor
      // will be called once
    }
  });
```

#####What if you want to observe changes in `StorIODb`? 

######First-case: Receive updates to `Observable` on each change in tables from `Query` 

```java
storIODb
  .get()
  .listOfObjects(Tweet.class)
  .withMapFunc(Tweet.MAP_FROM_CURSOR)
  .withQuery(Tweet.ALL_TWEETS_QUERY)
  .prepare()
  .createObservableStream() // here is the magic! It will be subscribed to changes in tables from Query
  .subscribeOn(Schedulers.io())
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(new Action1<List<Tweet>>() {
    @Override public void call(List<Tweet> tweets) {
      // display the data
      // magic: this will be called on each update in "tweets" table
    }
  });
  
  // don't forget to manage Subscription and unsubscribe in lifecycle methods to prevent memory leaks
```

######Second case: Handle changes manually

```java
storIODb
  .observeChangesInTable("tweets")
  .subscribe(new Action1<Changes>() { // or apply RxJava Operators
    // do what you want!
  });
```

######Get result with RawQuery with joins and other SQL things

```java
storIODb
  .get()
  .listOfObjects(TweetAndUser.class)
  .withMapFunc(TweetAndUser.MAP_FROM_CURSOR)
  .withQuery(new RawQuery.Builder()
    .query("SELECT * FROM tweets JOIN users ON tweets.user_name = users.name WHERE tweets.user_name = ?")
    .args("artem_zin")
    .build())
  .prepare()
  .createObservableStream();
```

######Customize behavior of `Get` Operation with `GetResolver`

```java
GetResolver getResolver = new GetResolver() {
  // Performs Get for RawQuery
  @Override @NonNull public Cursor performGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery) {
    Cursor cursor = ...; // get result as you want, or add some additional behavior 
    return cursor;
  }
  
  // Performs Get for Query
  @Override @NonNull public Cursor performGet(@NonNull StorIODb storIODb, @NonNull Query query) {
    Cursor cursor = ...; // get result as you want, or add some additional behavior 
    return cursor;
  }
};

storIODb
  .get()
  .listOfObjects(Tweet.class)
  .withMapFunc(Tweet.MAP_FROM_CURSOR)
  .withQuery(Tweet.ALL_TWEETS_QUERY)
  .withGetResolver(getResolver) // here we set custom GetResolver for Get Operation
  .prepare()
  .executeAsBlocking();
```

Several things about `Get` Operation:
* There is `DefaultGetResolver` which simply redirects query to `StorIODb`, `Get` Operation will use `DefaultGetResolver` if you won't pass your `GetResolver`, in 99% of cases `DefaultGetResolver` will be enough
* As you can see, results of `Get` Operation computed even if you'll apply `RxJava` operators such as `Debounce`, if you want to avoid unneeded computatations, please combine `StorIODb.observeChangesInTable()` with `Get` Operation manually.
* In `StorIO 1.1.0` we are going to add `Lazy<T>` to allow you skip unneeded computations
* If you want to `Put` multiple items into `StorIODb`, better to do this in transaction to avoid multiple calls to the listeners (see docs about `Put` Operation)

####2. Put Operation
`Put` Operation requires `PutResolver` which defines the behavior of `Put` Operation (insert or update).

You have two ways of implementing `PutResolver`:

1) Easy: extend `DefaultPutResolver` and implement it correctly
`DefaultPutResolver` will search for field `_id` in `ContentValues` and will perform insert if there is no value or update if there `_id` is not null.

2) Implement `PutResolver` and perform put as you need.

In 99% of cases your tables have `_id` column as unique id and `DefaultPutResolver` will be enough

```java
public static final PutResolver<Tweet> PUT_RESOLVER = new DefaultPutResolver<>() {
  @Override @NonNull protected String getTable() {
    return "tweets";
  }
  
  @Override public void afterPut(@NonNull Tweet tweet, @NonNull PutResult putResult) {
    // callback were you can change object after insert
    
    if (putResult.wasInserted()) {
      tweet.setId(putResult.getInsertedId());
    }
  }
};
```
######Put object of some type
```java
Tweet tweet = getSomeTweet();

storIODb
  .put()
  .object(tweet)
  .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
  .withPutResolver(Tweet.PUT_RESOLVER)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

######Put multiple objects of some type
```java
List<Tweet> tweets = getSomeTweets();

storIODb
  .put()
  .objects(tweets)
  .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
  .withPutResolver(Tweet.PUT_RESOLVER)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

######Put `ContentValues`
```java
ContentValues contentValues = getSomeContentValues(); 

storIODb
  .put()
  .contentValues(contentValues)
  .withPutResolver(putResolver)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

Several things about `Put` Operation:
* `Put` Operation requires `PutResolver`, `StorIO` requires it to avoid reflection
* `Put` Operation can be executed in transaction and by default it will use transaction, you can customize this via `useTransactionIfPossible()` or `dontUseTransaction()`
* `Put` Operation in transaction will produce only one notification to `StorIODb` observers
* Result of `Put` Operation can be useful if you want to know what happened: insert (and insertedId) or update (and number of updated rows)

####3. Delete Operation
######Delete object
```java
// you can store it as static final field somewhere
final MapFunc<Tweet, DeleteQuery> mapToDeleteQuery = new MapFunc<Tweet, DeleteQuery>() {
  @Override public DeleteQuery map(Tweet tweet) {
    return new DeleteQuery.Builder()
      .table(Tweet.TABLE)
      .where(Tweet.COLUMN_ID)
      .whereArgs(String.valueOf(tweet.getId()))
      .build();
  }
};


Tweet tweet = getSomeTweet();

storIODb
  .delete()
  .object(tweet)
  .withMapFunc(mapToDeleteQuery)
  .prepare()
  .executeAsBlocking(); // or createObservable()
``` 

######Delete multiple objects
```java
List<Tweet> tweets = getSomeTweets();

storIODb
  .delete()
  .objects(tweets)
  .withMapFunc(mapToDeleteQuery)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

Several things about `Delete` Operation:
* `Delete` Operation of multiple items can be performed in transaction, by default it will use transaction if possible
* Same rules as for `Put` Operation about notifications for `StorIODb` observers: transaction -> one notification, without transaction - multiple notifications
* Result of `Delete` Operation can be useful if you want to know what happened

####4. ExecSql Operation
Sometimes you need to execute raw sql, `StorIODb` allows you to do it

```java
storIODb
  .execSql()
  .withQuery(new RawQuery.Builder()
    .query("ALTER TABLE ? ADD COLUMN ? INTEGER")
    .args("tweets", "number_of_retweets")
    .affectedTables("tweets") // optional: you can specify affected tables to notify Observers 
    .build())
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

Several things about `ExecSql`:
* Use it for non insert/update/query/delete operations
* Notice that you can set list of tables that will be affected by `RawQuery` and `StorIODb` will notify tables Observers

----
For more examples, please check our [`Design Tests`](storio/src/test/java/com/pushtorefresh/storio/db/unit_test/design).

####Architecture:
`StorIODb` and `StorIOContentProvider` — are abstractions with default implementations: `StorIOSQLiteDb` and `StorIOContentProviderImpl`. 

It means, that you can have your own implementation of `StorIODb` and `StorIOContentProvider` with custom behavior, such as memory caching, verbose logging and so on.

One of the main goals of `StorIO` — clean API which will be easy to use and understand, that's why `StorIODb` and `StorIOContentProvider` have just several methods, but sometimes you need to go under the hood and we allow you to do it: `StorIODb.Internal` and `StorIOContentProvider.Internal` encapsulates low-level methods, you can use them if you need to, but try to avoid it.

All `Query` objects are immutable, you can share them safely.

You may notice that each Operation (Get, Put, Delete) should be prepared with `prepare()`. `StorIO` has an entity called `PreparedOperation<T>`, and you can use them to perform group execution of several Prepared Operations or provide `PreparedOperation<T>` as a return type of your API (for example in Model layer) and client will decide how to execute it: `executeAsBlocking()` or `createObservable()` or `createObservableStream()` (if possible). Also, Prepared Operations might be useful for ORMs based on `StorIO`.

You can customize behavior of every Operation via `Resolvers`: `GetResolver`, `PutResolver`, `DeleteResolver`.

----
**Made with love** in [Pushtorefresh.com](https://pushtorefresh.com) by [@artem_zin](https://twitter.com/artem_zin) and [@nikitin-da](https://github.com/nikitin-da)
