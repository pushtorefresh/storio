StorIO Change Log
==========

## Version 1.6.0

_2015_10_19_

* Convert any `Query` back to its `Builder` via `toBuilder()`!
* Observe all changes in `StorIOSQLite` via `observeChanges()`!
* Retrieve `ContentResolver` from `StorIOContentResolver` via `StorIOContentResolver.internal().contentResolver()`

**Changes:**

* [PR 544](https://github.com/pushtorefresh/storio/pull/544) Add getter for underlying ContentResolver to the StorIOContentResolver
* [PR 543](https://github.com/pushtorefresh/storio/pull/543) Add API for observing all changes in StorIOSQLite
* [PR 539](https://github.com/pushtorefresh/storio/pull/539) Add toBuilder() for queries
* [PR 538](https://github.com/pushtorefresh/storio/pull/538) Switch back to Android Gradle Plugin 1.3.1

## Version 1.5.0

_2015_10_01_

* `get().numberOfResults()` for both SQLite and ContentResolver!
* `@CheckResult` annotation for better IDE experience!
* `insertWithOnConflict()` for StorIOSQLite.
* We've added example of composite entity! 

**Changes:**

* [PR 534](https://github.com/pushtorefresh/storio/pull/534) Add StorIOContentResolver get().numberOfResults()!
* [PR 533](https://github.com/pushtorefresh/storio/pull/533) Add StorIOSQLite get().numberOfResults()! 
* [PR 531](https://github.com/pushtorefresh/storio/pull/531) Add @CheckResult annotation, makes life in the Android Studio Better!
* [PR 530](https://github.com/pushtorefresh/storio/pull/530) Add insertWithOnConflict() for StorIOSQLite!
* [PR 520](https://github.com/pushtorefresh/storio/pull/520) Example of UserWithTweets entity with custom Put/Get/Delete resolvers

## Version 1.4.0

_2015_09_15_

*  `Query.limit()` now accepts integers! Better API for everybody! Thanks @vokilam for the suggestion!
*  Little fix for the sample app. Thanks @cpeppas!

**Changes:**

* [PR 517](https://github.com/pushtorefresh/storio/pull/517) Limit method accept integer args
* [PR 514](https://github.com/pushtorefresh/storio/pull/514) adding somebytes column that was missing from CREATE TABLE TweetsTable

## Version 1.3.1

_2015_09_10_

*  Add info about all types of fields supported by StorIO Annotation Processor!
*  Updated build tools and dependencies! (Gradle Plugin 1.4.0-beta1, sdk 23, RxJava 1.0.14, RxAndroid 1.0.1, Support Libs 23.0.1, Private Constructor Checker 1.1.0, Dagger 2.0.1, ButterKnife 7.0.1)
*  **Fix SQLiteDatabase.execSQL() without args!**

**Changes:**

* [PR 503](https://github.com/pushtorefresh/storio/pull/503) Annotation processor supported types
* [PR 504](https://github.com/pushtorefresh/storio/pull/504) New build tools and dependencies
* [PR 510](https://github.com/pushtorefresh/storio/pull/510) Raw query without arguments fix

## Version 1.3.0

_2015_08_29_

*  **StorIOSQLite Annotation Processor now supports blobs `byte[]`!**
*  We've added example of relations implementation (R from ORM) to the Sample App!

**Changes:**

* [PR 498](https://github.com/pushtorefresh/storio/pull/498) Add support for `byte[]` into StorIOSQLite annotation processor
* [PR 494](https://github.com/pushtorefresh/storio/pull/494) Relations example!


## Version 1.2.1

_2015_08_17_

*  **`StorIOSQLite`: Remove unnecessary synchronization, prevent possible deadlocks, faster & better!**
*  **Use AssertJ for test!**

**Changes:**

* [PR 491](https://github.com/pushtorefresh/storio/pull/491) Remove unnecessary synchronization, prevent possible deadlocks, faster & better
* [PR 490](https://github.com/pushtorefresh/storio/pull/490) Use AssertJ for test


## Version 1.2.0

_2015_08_7_

*  **Add `Queries` class with common utils for queries**, now you can generate placeholders!

**Changes:**

* [PR 485](https://github.com/pushtorefresh/storio/pull/485) Add public Queries utils with function for generating placeholders


## Version 1.1.2

_2015_08_5_

*  **Fix for possible deadlock because of internal SQLiteDatabase synchronization.** See issue #[481](https://github.com/pushtorefresh/storio/issues/481).
*  **Thanks to [@tadas-subonis](https://github.com/tadas-subonis)!**

**Changes:**

* [PR 482](https://github.com/pushtorefresh/storio/pull/482) Fix possible deadlock caused by internal synchronization in SQLiteDatabase


## Version 1.1.1

_2015_08_4_

*  **Fix for nested transactions in StorIOSQLite.**
* Switch to PrivateConstructorChecker!
* Ignore debug buildType for library projects — faster CI.
*  **Thanks to [@tadas-subonis](https://github.com/tadas-subonis)!**


**Changes:**

* [PR 479](https://github.com/pushtorefresh/storio/pull/479) Fix ConcurrentModificationException in DefaultStorIOSQLite in case of nested transactions
* [PR 477](https://github.com/pushtorefresh/storio/pull/477) Switch to PrivateConstructorChecker!
* [PR 473](https://github.com/pushtorefresh/storio/pull/473) Ignore debug buildType for library projects

## Version 1.1.0

_2015_07_27_

*  **Common StorIOException for all operations** See [448](https://github.com/pushtorefresh/storio/issues/448).
*  **StorIOContentResolver will throw StorIOException if ContentResolver.query() returns null**
*  **80% code coverage!**
* RxJava 1.0.13
* Robolectric 3.0.0


**Changes:**

* [PR 451](https://github.com/pushtorefresh/storio/pull/451) Throw exception if contentResolver.query() returns null
* [PR 458](https://github.com/pushtorefresh/storio/pull/458) Remove Query.CompleteBuilder.whereArgs(list), it was error in API, sorry guys
* [PR 460](https://github.com/pushtorefresh/storio/pull/460) 80% code coverage for StorIO-Test-Common
* [PR 461](https://github.com/pushtorefresh/storio/pull/461) 80% code coverage for StorIO-Common
* [PR 462](https://github.com/pushtorefresh/storio/pull/462) 80% code coverage for StorIO-Content-Resolver
* [PR 465](https://github.com/pushtorefresh/storio/pull/465) 80% code coverage for StorIO-SQLite
* [PR 466](https://github.com/pushtorefresh/storio/pull/466) Switch to Robolectric 3.0
* [PR 467](https://github.com/pushtorefresh/storio/pull/467) Switch to RxJava v1.0.13
* [PR 468](https://github.com/pushtorefresh/storio/pull/468) Revert "Remove Query.CompleteBuilder.whereArgs(list), it was error in…


## Version 1.0.1

_2015-07-21_

*  **PutResult.newUpdateResult() now can be created with 0 updated rows.** See [453](https://github.com/pushtorefresh/storio/issues/453).
*  **JavaPoet 1.2.**
*  **Better tests!**
*  **Better Sample App!**

**Changes:**

* [PR 440](https://github.com/pushtorefresh/storio/pull/440) Fix content resolver tests flakiness.
* [PR 442](https://github.com/pushtorefresh/storio/pull/442) Pack of improvements for the Sample App.
* [PR 444](https://github.com/pushtorefresh/storio/pull/444) Switch to JavaPoet v1.2.
* [PR 454](https://github.com/pushtorefresh/storio/pull/454) Allow PutResult.newUpdateResult() with 0 rows updated.


## Version 1.0.0

_2015-06-01_

**Initial release.**
