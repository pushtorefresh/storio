#### StorIO — modern API for SQLiteDatabase and ContentProvider

#####Overview:
* Powerful set of operations: `Put`, `Get`, `Delete`
* Convinient builders. Forget about 6-7 `null` in queries
* Immutability of Queries, Operations and thread-safety
* No reflection, no annotations, `StorIO` is not ORM
* Every operation over `StorIO` can be executed as blocking call or as `Observable`
* `RxJava` as first class citizen, but it's not required dependency!
* `Observable` from `Get` operation can observe changes in `StorIO` and receive updates automatically
* `StorIO` can replace `Loaders`
* If you don't want to work with `Cursor` and `ContentValue` you don't have to
* You can customize behavior of every operation via `Resolvers`: `GetResolver`, `PutResolver`, `DeleteResolver`
* `StorIO` is mockable for testing


###StorIODb — an API for Database

####Get operation

######Get `Cursor` with blocking call:

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

######Get list of objects with blocking call:

```java
// it's a good practice to store MapFunc as public static final field in object class
final MapFunc<Cursor, Tweet> mapFunc = new MapFunc<Cursor, Tweet>() {
  @Override public Tweet map(Cursor cursor) {
    // no need to move cursor and close it, StorIO will handle it for you
    return new Tweet(); // parse values from cursor 
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

Things getting much more insteresing with `RxJava`!

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
  .subscribeOn(Schedulers.io()) // moving Get operation to other thread
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

######Customize behavior of `Get` operation with `GetResolver`

```java
GetResolver getResolver = new GetResolver() {
  // resolve Get for RawQuery
  @Override @NonNull public Cursor performGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery) {
    Cursor cursor = ...; // get result as you want, or add some additional behavior 
    return cursor;
  }
  
  // resolve Get for Query
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
  .withGetResolver(getResolver) // here we set custom GetResolver for Get operation
  .prepare()
  .executeAsBlocking();
```

Several things about `Get` operation:
* There is `DefaultGetResolver` which simply redirects query to `StorIODb`, `Get` operation will use `DefaultGetResolver` if you won't pass your `GetResolver`, in 99% of cases `DefaultGetResolver` will be enough
* As you can see, results of `Get` operation computed even if you'll apply `RxJava` operators such as `Debounce`, if you want to avoid unneeded computatations, please combine `StorIODb.observeChangesInTable()` with `Get` operation manually.
* In `StorIO 1.1.0` we are going to add `Lazy<T>` to allow you skip unneeded computations
* If you want to `Put` multiple items into `StorIODb`, better to do this in transaction to avoid multiple calls to the listeners (see docs about `Put` operation)

####Put operation
`Put` operation requires `PutResolver` which defines the behavior of `Put` operation (insert or update).

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
    // optional callback were you can change object after insert
    
    if (putResult.wasInserted()) {
      tweet.setId(putResult.getInsertedId());
    }
  }
};
```

######Put `ContentValues`
```java
ContentValues contentValues = getSomeContentValues(); 

storIODb
  .put()
  .contentValues(contentValues) // or Iterable<ContentValues>
  .withPutResolver(putResolver)
  .prepare()
  .executeAsBlocking(); // or createObservable()
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

Several things about `Put` operation:
* `Put` operation requires `PutResolver`, `StorIO` requires it to avoid reflection
* `Put` operation can be executed in transaction and by default it will use transaction, you can customize this via `useTransactionIfPossible()` or `dontUseTransaction()`
* `Put` operation in transaction will produce only one notification to table observers

For more examples, please check our [`Design Tests`](storio/src/test/java/com/pushtorefresh/storio/db/unit_test/design).

####Architecture:
`StorIODb` and `StorIOContentProvider` — are abstractions with default implementations: `StorIOSQLiteDb` and `StorIOContentProviderImpl`. 

It means, that you can have your own implementation of `StorIODb` and `StorIOContentProvider` with custom behavior, such as memory caching, verbose logging and so on.

One of the main goals of `StorIO` — clean API which will be easy to use and understand, that's why `StorIODb` and `StorIOContentProvider` have just several methods, but sometimes you need to go under the hood and we allow you to do it: `StorIODb.Internal` and `StorIOContentProvider.Internal` encapsulates low-level methods, you can use them if you need to, but try to avoid it.

