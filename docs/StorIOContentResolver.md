### StorIOContentResolver — API for Content Resolver

Important notice: All StorIO APIs looks same, if you know how to work with StorIOSQLite -> you know how to work with StorIOContentResolver!

#### 0. Create an instance of StorIOContentResolver

```java
StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
  .contentResolver(yourContentResolver)
  .addTypeMapping(SomeType.class, typeMapping) // required for object mapping
  .build();
```

It's a good practice to use one instance of `StorIOContentResolver` per application, but it's not required.

#### 1. Get Operation
###### Get list of objects with blocking call:

```java
final List<Tweet> tweets = storIOContentResolver
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(Query.builder()
    .uri(someUri)
    .where("author = ?")
    .whereArgs("@artem_zin")
    .build())
  .prepare()
  .executeAsBlocking();
```

###### Get `Cursor` via blocking call:

```java
final Cursor tweetsCursor = storIOContentResolver
  .get()
  .cursor()
  .withQuery(Query.builder()
    .uri(someUri)
    .where("author = ?")
    .whereArgs("@artem_zin")
    .build())
  .prepare()
  .executeAsBlocking();
```

Things become much more interesting with `RxJava`!

##### What if you want to observe changes in `StorIOContentResolver`?

###### First-case: Receive updates to `Observable` on each change of Uri from `Query`

```java
storIOContentResolver
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(Query.builder()
    .uri(tweetsUri)
    .where("author = ?")
    .whereArgs("@artem_zin")
    .build())
  .prepare()
  .asRxObservable() // Get Result as rx.Observable and subscribe to further updates of Uri from Query!
  .observeOn(mainThread()) // All Rx operations work on Schedulers.io()
  .subscribe(tweets -> { // Please don't forget to unsubscribe
      // will be called with first result and then after each change of Uri from Query
      adapter.setData(tweets);
    }
  );
// don't forget to manage Subscription and unsubscribe in lifecycle methods to prevent memory leaks
```

###### Second case: Handle changes manually

```java
storIOContentResolver
  .observeChangesOfUri(someUri)
  .subscribe(changes -> { // Just subscribe or apply Rx Operators such as Debounce, Filter, etc
    // do what you want!
  });
```

###### Get result via Rx only once and ignore further changes

```java
storIOContentResolver
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(Query.builder()
          .uri(tweetsUri)
          .build())
  .prepare()
  .asRxObservable()
  .take(1) // To get result only once and ignore further changes of this Uri
  .observeOn(mainThread())
  .subscribe(tweets -> {
      // Display data
    }
  );
```

###### Customize behavior of `Get` Operation with `GetResolver`

```java
GetResolver<Type> getResolver = new DefaultGetResolver()<Type> {
  @Override @NonNull public SomeType mapFromCursor(@NonNull Cursor cursor) {
    return new SomeType(); // parse Cursor here
  }
};

storIOContentResolver
  .get()
  .listOfObjects(Tweet.class)
  .withQuery(someQuery)
  .withGetResolver(getResolver) // here we set custom GetResolver for Get Operation
  .prepare()
  .executeAsBlocking();
```

Several things about `Get` Operation:
* There is `DefaultGetResolver` — Default implementation of `GetResolver` which simply redirects query to `StorIOContentResolver`, in 99% of cases `DefaultGetResolver` will be enough
* As you can see, results of `Get` Operation computed even if you'll apply `RxJava` operators such as `Debounce`, if you want to avoid unneeded computations, please combine `StorIOContentResolver.observeChangesOfUri()` with `Get` Operation manually.
* In next versions of `StorIO` we are going to add `Lazy<T>` to allow you skip unneeded computations

#### 2. Put Operation

###### Put object of some type
```java
Tweet tweet = getSomeTweet();

storIOContentResolver
  .put()
  .object(tweet)
  .prepare()
  .executeAsBlocking(); // or asRxObservable()
```

###### Put multiple objects of some type
```java
List<Tweet> tweets = getSomeTweets();

storIOContentResolver
  .put()
  .objects(tweets)
  .prepare()
  .executeAsBlocking(); // or asRxObservable()
```

###### Put `ContentValues`
```java
ContentValues contentValues = getSomeContentValues(); 

storIOContentResolver
  .put()
  .contentValues(contentValues)
  .withPutResolver(putResolver) // requires PutResolver<ContentValues>
  .prepare()
  .executeAsBlocking(); // or asRxObservable()
```

`Put` Operation requires `PutResolver` which defines the behavior of `Put` Operation (insert or update).

```java
PutResolver<SomeType> putResolver = new DefaultPutResolver<SomeType>() {
  @Override @NonNull public InsertQuery mapToInsertQuery(@NonNull SomeType object) {
    return InsertQuery.builder()
      .uri("content://some_uri")
      .build();
  }
  
  @Override @NonNull public UpdateQuery mapToUpdateQuery(@NonNull SomeType object) {
    return UpdateQuery.builder()
      .uri("content://some_uri")
      .where("some_column = ?")
      .whereArgs(object.someColumn())
      .build();
  }
  
  @Override @NonNull public ContentValues mapToContentValues(@NonNull SomeType object) {
    final ContentValues contentValues = new ContentValues();
    // fill with fields from object
    return contentValues;
  }
};
```

Several things about `Put` Operation:
* `Put` Operation requires `PutResolver`
* Result of `Put` Operation can be useful if you want to know what happened: insert (and insertedId) or update (and number of updated rows)

#### 3. Delete Operation

###### Delete object
```java
Tweet tweet = getSomeTweet();

storIOContentResolver
  .delete()
  .object(tweet)
  .prepare()
  .executeAsBlocking(); // or asRxObservable()
``` 

###### Delete multiple objects
```java
List<Tweet> tweets = getSomeTweets();

storIOContentResolver
  .delete()
  .objects(tweets)
  .prepare()
  .executeAsBlocking(); // or asRxObservable()
```

Delete Resolver

```java
DeleteResolver<SomeType> deleteResolver = new DefaultDeleteResolver<SomeType>() {
  @Override @NonNull public DeleteQuery mapToDeleteQuery(@NonNull SomeType object) {
    return DeleteQuery.builder()
      .uri("content://some_uri")
      .where("some_column = ?")
      .whereArgs(object.someColumn())
      .build();
  }
};
```

Several things about `Delete` Operation:
* Result of `Delete` Operation can be useful if you want to know what happened

#### How object mapping works?
##### You can set default type mappings when you build instance of `StorIOContentResolver`

```java
StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder()
  .contentResolver(yourContentResolver)
  .addTypeMapping(Tweet.class, ContentResolverTypeMapping.<Tweet>builder()
    .putResolver(new TweetPutResolver()) // object that knows how to perform Put Operation (insert or update)
    .getResolver(new TweetGetResolver()) // object that knows how to perform Get Operation
    .deleteResolver(new TweetDeleteResolver())  // object that knows how to perform Delete Operation
    .build())
  .addTypeMapping(...)
  // other options
  .build(); // This instance of StorIOContentResolver will know how to work with Tweet objects
```

You can override Operation Resolver per each individual Operation.


Few tips about Operation Resolvers:

* If your entities are immutable or they have builders or they use AutoValue/AutoParcel -> write your own Operation Resolvers
* If you want to write your own Operation Resolver -> take a look at Default Operation resolvers, they can fit your needs
* Via custom Operation Resolvers you can implement any Operation as you want -> store one object in multiple ContentProviders (why would you want to do that?), use custom sql things and so on

API of `StorIOSQLite` is same.
