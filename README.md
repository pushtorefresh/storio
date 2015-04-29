#### StorIO — modern API for SQLiteDatabase and ContentResolver

Currently in development.

#####Overview:
* Powerful & Simple set of Operations: `Put`, `Get`, `Delete`
* API for Humans: Type Safety, Immutability & Thread-Safety
* Convenient builders with compile-time guarantees for required params. Forget about 6-7 `null` in queries
* Typesafe Object Mapping, if you don't want to work with `Cursor` and `ContentValues` you don't have to
* No reflection, `StorIO` is not ORM
* Every Operation over `StorIO` can be executed as blocking call or as `rx.Observable`
* `RxJava` as first class citizen, but it's not required dependency!
* `rx.Observable` from `Get` Operation **can observe changes** in `StorIO` and receive updates automatically
* `StorIO` is replacements for `SQLiteDatabase` and `ContentResolver` APIs
* `StorIO` + `RxJava` is replacement for `Loaders` API
* We are working on `MockStorIO` (similar to [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)) for easy unit testing

----

#####Why StorIO?
* Open Source -> less bugs
* Documentation, Sample app and Design tests -> less bugs
* `StorIO` has unit and integration tests -> less bugs
* Simple concept of just three main Operations: `Put`, `Get`, `Delete` -> less bugs
* Almost everything is immutable and thread-safe -> less bugs
* Builders for everything make code much, much more readable and obvious -> less bugs
* Less bugs -> less bugs

####Documentation:

* [`StorIO SQLite`](docs/StorIOSQLite.md)
* [`StorIO ContentResolver`](docs/StorIOContentProvider.md)

Easy ways to learn how to use `StorIO` -> check out `Design Tests` and `Sample App`:

* [Design tests for StorIO SQLite](storio-sqlite/src/test/java/com/pushtorefresh/storio/sqlite/design)
* [Design tests for StorIO ContentResolver](storio-content-resolver/src/test/java/com/pushtorefresh/storio/contentresolver/design)
* [Sample App](storio-sample-app)

####Some examples

#####Get list of objects from SQLiteDatabase
```java
List<Tweet> tweets = storIOSQLite
  .get()
  .listOfObjects(Tweet.class) // Type safety
  .withQuery(new Query.Builder() // Query builder
    .table("tweets")
    .where("author = ?")
    .whereArgs("artem_zin") // Varargs Object..., no more new String[] {"I", "am", "tired", "of", "this", "shit"}
    .build()) // Query is immutable — you can save it and share without worries
  .prepare() // Operation builder
  .executeAsBlocking(); // Control flow is readable from top to bottom, just like with RxJava

```

#####Put something to SQLiteDatabase
```java
storIOSQLite
  .put() // Insert or Update
  .objects(Tweet.class, newTweets) // Type safety
  .prepare()
  .executeAsBlocking();
```

#####Delete something from SQLiteDatabase
```java
storIOSQLite
  .delete()
  .byQuery(new DeleteQuery.Builder()
    .table("tweets")
    .where("timestamp <= ?")
    .whereArgs(System.currentTimeMillis() - 86400) // No need to write String.valueOf()
    .build())
  .prepare()
  .executeAsBlocking();
```

####Reactive? Observable.just(true)!

#####Get something as rx.Observable
```java
storIOSQLite
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .createObservable()
  .subscribeOn(Schedulers.io()) // Execute Get Operation on Background Thread
  .observeOn(AndroidSchedulers.mainThread()) // Observe on Main Thread
  .subscribe(new Action1<List<Tweet>>() {
  	@Override public void call(List<Tweet> tweets) {
  	  adapter.setData(tweets); // display results
  	}
  });
```

#####Get something as rx.Observable and receive updates!
```java
storIOSQLite
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(new Query.Builder()
    .table("tweets")
    .build())
  .prepare()
  .createObservableStream() // Get Result as rx.Observable and subscribe to further updates of tables from Query!
  .subscribeOn(Schedulers.io())
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(new Action1<List<Tweet>>() { // don't forget to unsubscribe please
  	@Override public void call(List<Tweet> tweets) {
  	  // will be called with first result and then after each change of tables from Query
  	  // several changes in transaction -> one notification
  	  adapter.setData(tweets);
  	}
  });
```

#####Want to work with plain Cursor, no problems
```java
Cursor cursor = storIOSQLite
  .get()
  .cursor()
  .withQuery(new Query.Builder() // Or RawQuery
    .table("tweets")
    .where("who_cares = ?")
    .whereArgs("nobody")
    .build())
  .prepare()
  .executeAsBlocking();
```

####How object mapping works?
#####You can set default type mappings when you build instance of `StorIOSQLite` or `StorIOContentResolver`

```java
StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
  .db(someSQLiteDatabase)
  .addTypeDefaults(Tweet.class, new SQLiteTypeDefaults.Builder<Tweet>()
    .putResolver(new TweetPutResolver()) // object that knows how to perform Put Operation (insert or update)
    .getResolver(new TweetGetResolver()) // object that knows how to perform Get Operation
    .deleteResolver(new TweetDeleteResolver())  // object that knows how to perform Delete Operation
    .build())
  .addTypeDefaults(...)
  // other options
  .build(); // This instance of StorIOSQLite will know how to work with Tweet objects
```

You can override Operation Resolver per each individual Operation, it can be useful for working with `SQL JOIN`.
Also, as you can see, there is no Reflection, and no performance reduction in compare to manual object mapping code.

To **save you from coding boilerplate classes** we created **Annotation Processor** which will generate `PutResolver`, `GetResolver` and `DeleteResolver` at compile time, you just need to use generated classes

```groovy
dependencies {
	// At the moment there is annotation processor only for StorIOSQLite 
	compile 'com.pushtorefresh.storio:storio-sqlite-annotation:1.0.0'

	// We recommend to use Android Gradle Apt plugin: https://bitbucket.org/hvisser/android-apt
	apt 'com.pushtorefresh.storio:storio-sqlite-annotation-processor:1.0.0'
}
```

```java
@StorIOSQLiteType(table = "tweets")
public class Tweet {
	
	// annotated fields should have package-level visibility
	@StorIOSQLiteColumn(name = "author")
	String author;

	@StorIOSQLiteColumn(name = "content")
	String content;

    // please leave default constuctor with package-level visibility
	Tweet() {}
}
```

Annotation Processor will generate three classes in same package as annotated class during compilation:

* `TweetStorIOSQLitePutResolver`
* `TweetStorIOSQLiteGetResolver`
* `TweetStorIOSQLiteDeleteResolver`

You just need to aply them:

```java
StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
  .db(someSQLiteDatabase)
  .addTypeDefaults(Tweet.class, new SQLiteTypeDefaults.Builder<Tweet>()
    .putResolver(new TweetStorIOSQLitePutResolver()) // object that knows how to perform Put Operation (insert or update)
    .getResolver(new TweetStorIOSQLiteGetResolver()) // object that knows how to perform Get Operation
    .deleteResolver(new TweetStorIOSQLiteDeleteResolver())  // object that knows how to perform Delete Operation
    .build())
  .addTypeDefaults(...)
  // other options
  .build(); // This instance of StorIOSQLite will know how to work with Tweet objects
```

Few tips about Operation Resolvers:

* If your entities are immutable or they have builders or they use AutoValue/AutoParcel -> write your own Operation Resolvers
* If you want to write your own Operation Resolver -> take a look at Default Operation resolvers, they can fit your needs
* Via custom Operation Resolvers you can implement any Operation as you want -> store one object in multiple tables, use custom sql things and so on

API of `StorIOContentResolver` is same.

----

####Documentation:

* [`StorIO SQLite`](docs/StorIOSQLite.md)
* [`StorIO ContentResolver`](docs/StorIOContentProvider.md)

Easy ways to learn how to use `StorIO` -> check out `Design Tests` and `Sample App`:

* [Design tests for StorIO SQLite](storio-sqlite/src/test/java/com/pushtorefresh/storio/sqlite/design)
* [Design tests for StorIO ContentResolver](storio-content-resolver/src/test/java/com/pushtorefresh/storio/contentresolver/design)
* [Sample App](storio-sample-app)

----

####Architecture:
`StorIOSQLite` and `StorIOContentResolver` — are abstractions with default implementations: `DefaultStorIOSQLite` and `DefaultStorIOContentResolver`.

It means, that you can have your own implementation of `StorIOSQLite` and `StorIOContentResolver` with custom behavior, such as memory caching, verbose logging and so on or mock implementation for unit testing (we are working on `MockStorIO`).

One of the main goals of `StorIO` — clean API for Humans which will be easy to use and understand, that's why `StorIOSQLite` and `StorIOContentResolver` have just several methods, but we understand that sometimes you need to go under the hood and `StorIO` allows you to do it: `StorIOSQLite.Internal` and `StorIOContentResolver.Internal` encapsulates low-level methods, you can use them if you need, but please try to avoid it.

####Queries

All `Query` objects are immutable, you can share them safely.

####Concept of Prepared Operations
You may notice that each Operation (Get, Put, Delete) should be prepared with `prepare()`. `StorIO` has an entity called `PreparedOperation<T>`, and you can use them to perform group execution of several Prepared Operations or provide `PreparedOperation<T>` as a return type of your API (for example in Model layer) and client will decide how to execute it: `executeAsBlocking()` or `createObservable()` or `createObservableStream()` (if possible). Also, Prepared Operations might be useful for ORMs based on `StorIO`.

You can customize behavior of every Operation via `Resolvers`: `GetResolver`, `PutResolver`, `DeleteResolver`.

----
**Made with love** in [Pushtorefresh.com](https://pushtorefresh.com) by [@artem_zin](https://twitter.com/artem_zin) and [@nikitin-da](https://github.com/nikitin-da)
