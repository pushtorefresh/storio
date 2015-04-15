###StorIOSQLite — API for SQLite Database

####0. Create an instance of StorIOSQLite

```java
StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
  .sqliteOpenHelper(yourSqliteOpenHelper) // or .db(db)
  .build();
```

It's a good practice to use one instance of `StorIOSQLite` per database.

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

final List<Tweet> tweets = storIOSQLite
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .withMapFunc(mapFunc)
  .prepare()
  .executeAsBlocking();
```

######Get `Cursor` via blocking call:

```java
final Cursor tweetsCursor = storIOSQLite
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
storIOSQLite
  .get()
  .cursor()
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .createObservable()
  .subscribeOn(Schedulers.io()) // Move Get Operation to background thread
  .observeOn(AndroidSchedulers.mainThread()) // Observe result on Main thread
  .subscribe(new Action1<Cursor>() {
    @Override public void call(Cursor cursor) {
      // display the data from cursor
      // will be called once
    }
  });
```

#####What if you want to observe changes in `StorIOSQLite`?

######First-case: Receive updates to `Observable` on each change in tables from `Query` 

```java
storIOSQLite
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(Tweet.ALL_TWEETS_QUERY)
  .withMapFunc(Tweet.MAP_FROM_CURSOR)
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
storIOSQLite
  .observeChangesInTable("tweets")
  .subscribe(new Action1<Changes>() { // or apply RxJava Operators
    // do what you want!
  });
```

######Get result with RawQuery with joins and other SQL things

```java
storIOSQLite
  .get()
  .listOfObjects(TweetAndUser.class)
  .withQuery(new RawQuery.Builder()
    .query("SELECT * FROM tweets JOIN users ON tweets.user_name = users.name WHERE tweets.user_name = ?")
    .args("artem_zin")
    .build())
  .withMapFunc(TweetAndUser.MAP_FROM_CURSOR)
  .prepare()
  .createObservableStream();
```

######Customize behavior of `Get` Operation with `GetResolver`

```java
GetResolver getResolver = new GetResolver() {
  // Performs Get for RawQuery
  @Override @NonNull public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
    Cursor cursor = ...; // get result as you want, or add some additional behavior 
    return cursor;
  }
  
  // Performs Get for Query
  @Override @NonNull public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
    Cursor cursor = ...; // get result as you want, or add some additional behavior 
    return cursor;
  }
};

storIOSQLite
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(Tweet.ALL_TWEETS_QUERY)
  .withMapFunc(Tweet.MAP_FROM_CURSOR)
  .withGetResolver(getResolver) // here we set custom GetResolver for Get Operation
  .prepare()
  .executeAsBlocking();
```

Several things about `Get` Operation:
* There is `DefaultGetResolver` which simply redirects query to `StorIOSQLite`, `Get` Operation will use `DefaultGetResolver` if you won't pass your `GetResolver`, in 99% of cases `DefaultGetResolver` will be enough
* As you can see, results of `Get` Operation computed even if you'll apply `RxJava` operators such as `Debounce`, if you want to avoid unneeded computations, please combine `StorIOSQLite.observeChangesInTable()` with `Get` Operation manually.
* In `StorIO 1.1.0` we are going to add `Lazy<T>` to allow you skip unneeded computations
* If you want to `Put` multiple items into `StorIOSQLite`, better to do this in transaction to avoid multiple calls to the listeners (see docs about `Put` Operation)

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

storIOSQLite
  .put()
  .object(tweet)
  .withPutResolver(Tweet.PUT_RESOLVER)
  .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

######Put multiple objects of some type
```java
List<Tweet> tweets = getSomeTweets();

storIOSQLite
  .put()
  .objects(tweets)
  .withPutResolver(Tweet.PUT_RESOLVER)
  .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

######Put `ContentValues`
```java
ContentValues contentValues = getSomeContentValues(); 

storIOSQLite
  .put()
  .contentValues(contentValues)
  .withPutResolver(putResolver)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

Several things about `Put` Operation:
* `Put` Operation requires `PutResolver`, `StorIO` requires it to avoid reflection
* `Put` Operation can be executed in transaction and by default it will use transaction, you can customize this via `useTransaction(true)` or `useTransaction(false)`
* `Put` Operation in transaction will produce only one notification to `StorIOSQLite` observers
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

storIOSQLite
  .delete()
  .object(tweet)
  .withMapFunc(mapToDeleteQuery)
  .prepare()
  .executeAsBlocking(); // or createObservable()
``` 

######Delete multiple objects
```java
List<Tweet> tweets = getSomeTweets();

storIOSQLite
  .delete()
  .objects(tweets)
  .withMapFunc(mapToDeleteQuery)
  .prepare()
  .executeAsBlocking(); // or createObservable()
```

Several things about `Delete` Operation:
* `Delete` Operation of multiple items can be performed in transaction, by default it will use transaction if possible
* Same rules as for `Put` Operation about notifications for `StorIOSQLite` observers: transaction -> one notification, without transaction - multiple notifications
* Result of `Delete` Operation can be useful if you want to know what happened

####4. ExecSql Operation
Sometimes you need to execute raw sql, `StorIOSQLite` allows you to do it

```java
storIOSQLite
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
* Notice that you can set list of tables that will be affected by `RawQuery` and `StorIOSQLite` will notify tables Observers
