StorIO Change Log
==========

## Version 1.11.0

_2016_10_10_

* Basic sample app which depends only on storio-sqlite and storio-annotations. Many thanks to [@skrzyneckik](https://github.com/skrzyneckik)
* RxJava 1.2.1. Thanks to [@yshrsmz](https://github.com/yshrsmz)
* Make generated map methods public
* Placeholders generator allow zero count
* Remove toast exceptions swallowing in Sample

**Changes:**

* [PR 686](https://github.com/pushtorefresh/storio/pull/686) Basic sample app which depends only on storio-sqlite and storio-annotations
* [PR 692](https://github.com/pushtorefresh/storio/pull/692) RxJava 1.2.1
* [PR 674](https://github.com/pushtorefresh/storio/pull/674) Make generated map methods public
* [PR 676](https://github.com/pushtorefresh/storio/pull/676) Placeholders generator allow zero count
* [PR 687](https://github.com/pushtorefresh/storio/pull/687) Remove toast exceptions swallowing in Sample

## Version 1.10.0

_2016_07_26_

* Find type mapping among interfaces recursively. Pluggable typemapping!
* Default scheduler for StorIOSQLite
* Default scheduler for StorIOContentResolver
* `ignoreNull` property for annotation processing
* Generated get resolver supports nulls for boxed types

**Changes:**

* [PR 601](https://github.com/pushtorefresh/storio/pull/601) Find type mapping among interfaces recursively
* [PR 660](https://github.com/pushtorefresh/storio/pull/660) Default scheduler for StorIOSQLite
* [PR 661](https://github.com/pushtorefresh/storio/pull/661) Default scheduler for StorIOContentResolver
* [PR 642](https://github.com/pushtorefresh/storio/pull/642) `ignoreNull` property for annotation processing
* [PR 643](https://github.com/pushtorefresh/storio/pull/643) Generated get resolver supports nulls for boxed types

## Version 1.9.1

_2016_07_7_

* Gradle 2.1.2
* RxJava 1.1.6
* Apt plugin 1.8
* Add link to [CodeGenUnderStorIO](https://github.com/shivan42/CodeGenUnderStorIO)
* Backpressure fix in OnSubscribeExecuteAsBlocking
* Dagger 2.4 in sample app

**Changes:**

* [PR 668](https://github.com/pushtorefresh/storio/pull/668) RxJava 1.1.6
* [PR 667](https://github.com/pushtorefresh/storio/pull/667) Backpressure fix in OnSubscribeExecuteAsBlocking
* [PR 665](https://github.com/pushtorefresh/storio/pull/665) Gradle 2.1.2
* [PR 659](https://github.com/pushtorefresh/storio/pull/659) Apt plugin 1.8
* [PR 657](https://github.com/pushtorefresh/storio/pull/657) Dagger 2.4 in sample app
* [PR 655](https://github.com/pushtorefresh/storio/pull/655) Add link to [CodeGenUnderStorIO](https://github.com/shivan42/CodeGenUnderStorIO)

## Version 1.9.0

_2016_05_19_

* `asRxCompletable()`! Thanks to [@geralt-encore](https://github.com/geralt-encore)
* Gradle Wrapper 2.12
* RxJava 1.1.3
* Integration with Codecov.io
* `StorIOSQLite.LowLevel` instead of `StorIOSQLite.Internal`(deprecated). Feel free to use it!

**Changes:**

* [PR 651](https://github.com/pushtorefresh/storio/pull/651) `RawQuery` arguments are objects instead of strings
* [PR 650](https://github.com/pushtorefresh/storio/pull/650) RxJava 1.1.3
* [PR 632](https://github.com/pushtorefresh/storio/pull/632) Gradle Wrapper 2.12
* [PR 629](https://github.com/pushtorefresh/storio/pull/629) **`asRxCompletable` for `StorIOSQLite`**
* [PR 633](https://github.com/pushtorefresh/storio/pull/633) **`asRxCompletable` for `StorIOContentResolver`**
* [PR 630](https://github.com/pushtorefresh/storio/pull/630) Integration CI with Codecov.io
* [PR 599](https://github.com/pushtorefresh/storio/pull/599) `StorIOSQLite.LowLevel` instead of `StorIOSQLite.Internal` for `StorIOSQLite`
* [PR 608](https://github.com/pushtorefresh/storio/pull/608) `StorIOSQLite.LowLevel` instead of `StorIOSQLite.Internal` for `StorIOContentResolver`

## Version 1.8.0

_2016_01_19_

* `asRxSingle()`, yep, `rx.Single` support! Many thanks to [@geralt-encore](https://github.com/geralt-encore)
* `asRxObservable()` instead of `createObservable()` (deprecated)

**Changes:**

* [PR 596](https://github.com/pushtorefresh/storio/pull/596) Test asRxObservable() instead of createObservable() which is now deprecated
* [PR 594](https://github.com/pushtorefresh/storio/pull/594) Gradle Wrapper 2.10
* [PR 593](https://github.com/pushtorefresh/storio/pull/593) Enable emails from Travis to react on problems with master branch
* [PR 592](https://github.com/pushtorefresh/storio/pull/592) Add query to exceptions (significantly helps inspect crashes)
* [PR 588](https://github.com/pushtorefresh/storio/pull/588) Try to find interface of class when apply mapper
* [PR 586](https://github.com/pushtorefresh/storio/pull/586) Remove "final" from most of the classes (will help with mocking)
* [PR 585](https://github.com/pushtorefresh/storio/pull/585) Base `executeAsBlocking()` result is nullable
* [PR 584](https://github.com/pushtorefresh/storio/pull/584) **Add `asRxObservable()`, deprecate createObservable()**
* [PR 573](https://github.com/pushtorefresh/storio/pull/573) Support for rx.Single

## Version 1.7.0

_2015_12_30_

* Option to get one object for `StorIOSQLite` and `StorIOContentResolver`
* Handle backpressure for `Get` operation via RxJava (**requires RxJava 1.1.0**)
* `SQLiteTypeMapping` and `ContentResolverTypeMapping` generation
* Annotation processor for `StorIOContentResolver` 
* Option to set different uri's for `insert`, `update` and `delete` (`StorIOContentResolver`)
* `PutResult` and `DeleteResult` now allow `0` updated tables
* Jacoco is alive!
* Android Gradle Plugin 1.5.0
* Gradle wrapper 2.9
* RxJava 1.1.0
* SupportLibs 23.1.0
* **Thanks to [@geralt-encore](https://github.com/geralt-encore) and [@zayass](https://github.com/zayass)!**

**Changes:**

* [PR 574](https://github.com/pushtorefresh/storio/pull/574) `SQLiteTypeMapping` and `ContentResolverTypeMapping` generation
* [PR 575](https://github.com/pushtorefresh/storio/pull/575) Use force to reanimate Jacoco!
* [PR 569](https://github.com/pushtorefresh/storio/pull/569) Option to set different uri's for insert, update and delete
* [PR 572](https://github.com/pushtorefresh/storio/pull/572) Handle backpressure for `Get` operation via RxJava, RxJava 1.1.0
* [PR 561](https://github.com/pushtorefresh/storio/pull/561) Switch to Android Gradle Plugin 1.5.0
* [PR 563](https://github.com/pushtorefresh/storio/pull/563) `PreparedGetObject` blocking for `StorIOSQLite`
* [PR 568](https://github.com/pushtorefresh/storio/pull/568) `PreparedGetObject` as observable for `StorIOSQLite`
* [PR 565](https://github.com/pushtorefresh/storio/pull/565) `PreparedGetObject` blocking for `StorIOContentResolver`
* [PR 570](https://github.com/pushtorefresh/storio/pull/570) `PreparedGetObject` as observable for `StorIOContentResolver`
* [PR 560](https://github.com/pushtorefresh/storio/pull/560) `PutResult` and `DeleteResult` allow `0` updated tables
* [PR 562](https://github.com/pushtorefresh/storio/pull/562) Switch to Gradle wrapper 2.9
* [PR 558](https://github.com/pushtorefresh/storio/pull/558) Add module with common annotations processing logic
* [PR 548](https://github.com/pushtorefresh/storio/pull/548) Add annotation processor for `StorIOContentResolver`
* [PR 553](https://github.com/pushtorefresh/storio/pull/553) Switch to supportLibs 23.1.0

## Version 1.6.1

_2015_11_7_

* `StorIOContentReslver` fix for observing changes of Uris on Android API < 16

**Changes:**

* [PR 550](https://github.com/pushtorefresh/storio/pull/550) StorIOContentReslver fix for observing changes of Uris on Android API < 16

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
