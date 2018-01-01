### StorIO — modern replacement for SQLiteDatabase and ContentResolver APIs

#### Hello dear reader, hope you are Android Developer.

Everybody knows that APIs of [SQLiteDatabase](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html) and [ContentResolver](http://developer.android.com/reference/android/content/ContentResolver.html) suck, almost everyone uses some abstractions over them: ORM, DAO or custom solutions.

Over years of our (I mean each Pushtorefresh’s developer, because as company we are just starting) experience in Android Development we used all of these approaches:

* ORMs often become bottleneck (performance & flexibility) in critical period of application development.
* Own solution (usually it’s some kind of DAO) requires huge amount of time for testing, development and support, you can and should spend this time on more major things for the application.
* DAOs can fit your needs, but different libraries has their own pros and cons (we’ve worked with and reviewed 10+ libraries for DBs, [like Square when they decided to create SqlBrite](https://corner.squareup.com/2015/02/sqlbrite-reactive-sqlite-for-android.html)).

#### Let’s take a look at main things that offers StorIO:

1. API for Humans: fluent builders for Queries and other things, readable and obvious constructions, Object-Mapping, Immutability and Thread-Safety.
2. Simplified pack of Operations: instead of standard CRUD (Create-Read-Update-Delete or Insert-Select-Update-Delete) we suggest only three Operations: Put, Get and Delete, and it’s important to say that you can define behavior of each Operation per data type.
3. Optional Object Mapping without Reflection, but if you need to work with [Cursors](http://developer.android.com/reference/android/database/Cursor.html) and [ContentValues](http://developer.android.com/reference/android/content/ContentValues.html) — StorIO allows it as well.
4. Execution model similar to [Retrofit’s](https://github.com/square/retrofit) one: every Operation can be executed as blocking call or as rx.Observable, we can also add callback-oriented execution model if enough amount of users will ask for it.
5. True [Rx](https://github.com/ReactiveX/RxJava) support: Observable from Get Operation will be updated if tables/Uris will be changed, with StorIO + [RxJava](https://github.com/ReactiveX/RxJava) you can replace [Loaders](http://developer.android.com/guide/components/loaders.html), because you know, [Loaders API](http://developer.android.com/guide/components/loaders.html) is really ugly, bad and non obvious.

#### Why StorIO:

* Open Source -> less bugs
* Documentation, Sample app and Design tests -> less bugs
* `StorIO` has unit and integration tests -> less bugs
* Simple concept of just three main Operations: `Put`, `Get`, `Delete` -> less bugs
* Almost everything is immutable and thread-safe -> less bugs
* Builders for everything make code much, much more readable and obvious -> less bugs
* Less bugs -> less bugs

#### Why we made StorIO:

We tired of passing 5-7 params to the query() method, where half of them are nulls and the other half are String[] arrays with one element and call to String.valueOf(), it’s just ridiculous and unreadable -> with StorIO you can create Queries via builders and store them separately and even reuse them.

We tired of Object-Mapping with different constraints in ORMs and DAOs -> with StorIO we don’t limit you at all, you just need to declare resolvers for each Operations: Put, Get, Delete per type, of course we provide some default implementations, but you can easily write your own resolvers and use AutoValue/AutoParcel/etc entities.

We tired of checking for value existence to decide what to do insert/update -> with StorIO we have one Operation — Put, but we don’t hide insert/update semantic, more over as said before you can define your own behavior of this Operation and for example store one object in multiple tables and so on.

We tired of ORMs, because ORM will always be ORM, it can generate crazy, slow SQL or just limit you when you won’t be ready for that -> StorIO is not ORM, it’s some kind of DAO.

We tired of poor/missing Rx support in most of ORMs and DAOs, because Rx support is not only ability to get result as Observable, but also ability to receive updates after changes automatically — this is what Reactive is by its nature. In StorIO, rx.Observable from Get Operation will automatically receive updates of tables/Uris.

#### What about [SqlBrite](https://github.com/square/sqlbrite):

Yep, there is [SqlBrite from Square](https://corner.squareup.com/2015/02/sqlbrite-reactive-sqlite-for-android.html) but it’s low-level foundation, good Rx + SQLiteDatabase and ContentResolver integration, but it does not have Object Mapping out of the box, it provides same poor methods with a lot of nulls as SQLiteDabase (BriteDatabase) and just query method for ContentResolver (BriteContentResolver) -> in StorIO we tried to solve huge pack of problems and give us and you a tool that we all will be happy to use everyday, for example Immutability and Thread-Safety can save you from such huge amount of bugs, race conditions and so on, Object Mapping makes life easier, BTW we understand that sometimes you need to work with Cursor (when you have thousand of items in list and so on) so you can just reuse GetResolver of your entity and avoid violating DRY principle by repeating parsing code, @NonNull and @Nullable annotations can save you hours of debugging, Builders make your code readable.

StorIO is our vision, “merge” of good concepts, ideas from different libraries (even [Retrofit](https://github.com/square/retrofit)) and our experience in Software Development, we really think and hope that it can replace standard SQLiteDatabase and ContentResolver APIs for many Android Developers.

Here is the repository: https://github.com/pushtorefresh/storio, we would be glad to hear your feedback, and of course, contributions are welcomed!
