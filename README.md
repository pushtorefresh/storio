#### StorIO — modern API for SQLiteDatabase and ContentProvider

######Overview:
* Powerful set of operations: `Put`, `Get`, `Delete`
* Convinient builders. Forget about 6-7 `null` in queries
* Every operation over `StorIO` can be executed as blocking call or as `Observable`
* `RxJava` as first class citizen, but it's not required dependency!
* Observable from `Get` operation can observe changes in `StorIO` and receive updates automatically
* If you don't want to work with `Cursor` and `ContentValue` you don't have to
* `StorIO` is mockable for testing


####StorIODb — an API for Database

How to get a `Cursor` with blocking call:

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

How to get result as list of some type with blocking call:

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

Get cursor as `Observable`
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

######First-case: You want to update result of Get operation automatically

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

###### Second case: You want to handle changes manually

```java
storIODb
  .observeChangesInTable("tweets")
  .subscribe(new Action1<Changes>() {
    // do what you want!
  });
```

Please notice several things:
* If you want to `Put` multiple items into `StorIODb`, better to do this in transaction to avoid multiple calls to the listeners (see docs about `Put` operation)
* As you can see, results of `Get` operation computed even if you'll apply `RxJava` operators such as `Debounce`, if you want to avoid unneeded computatations, please combine `StorIODb.observeChangesInTable()` with `Get` operation manually.

For more examples, please check our [`Design Tests`](storio/src/test/java/com/pushtorefresh/storio/db/unit_test/design).

####Architecture:
`StorIODb` and `StorIOContentProvider` — are abstractions with default implementations: `StorIOSQLiteDb` and `StorIOContentProviderImpl`. 

It means, that you can have your own implementation of `StorIODb` and `StorIOContentProvider` with custom behavior, such as memory caching, verbose logging and so on.

One of the main goals of `StorIO` — clean API which will be easy to use and understand, that's why `StorIODb` and `StorIOContentProvider` have just several methods, but sometimes you need to go under the hood and we allow you to do it: `StorIODb.Internal` and `StorIOContentProvider.Internal` encapsulates low-level methods, you can use them if you need to, but try to avoid it.

