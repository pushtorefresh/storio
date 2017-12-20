StorIO Change Log
==========

## Version 3.0.0

_2017_12_20_

* RxJava2 support :tada::tada::tada:
* Add `asRxMaybe`.
* `executeSQL()` now can be executed via `asRxCompletable()`.
* Add interceptors for ContentResolver.
* Add ContentResolver sample.
* Android gradle plugin 3.0.1 and support libraries 27.0.2.
* Mockito 2.13.0 and Mockito-Kotlin 1.5.0.
* Add gradle versions plugin.
* Table generation with few primary keys.
* Do not publish jar for android modules.

**Migration notes:**
* `asRxObservable` -> `asRxFlowable` (see [backpressure 2.0](https://github.com/ReactiveX/RxJava/wiki/Backpressure-(2.0))).
* Get object `asRxFlowable()` and `asRxSingle` return `Optional` of object because RxJava2 [no longer accepts nulls](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#nulls).
* You can use `asRxMaybe` to retrieve value without wrapping.
* `PreparedOperation` takes 3 parameters: `Result` - type of operation result; `WrappedResult` - `Optional` in cases when result may be null, result itself otherwise; `Data` - some operation description that can be used inside interceptor.
* You can call `DefaultStorIOContentResolver.Builder#addInterceptor(Interceptor)` to log/debug/modify result of any operation (like it was implemented before in `DefaultStorIOSQLite`).
 
**Changes:**

* [PR 844](https://github.com/pushtorefresh/storio/pull/844): Override Travis install step to avoid unnecessary `./gradlew assemble`.
* [PR 845](https://github.com/pushtorefresh/storio/pull/845): RxJava2 base support.
* [PR 848](https://github.com/pushtorefresh/storio/pull/848): Optional for SQLite.
* [PR 849](https://github.com/pushtorefresh/storio/pull/849): Optional for ContentResolver.
* [PR 850](https://github.com/pushtorefresh/storio/pull/850): Add ContentResolver sample.
* [PR 854](https://github.com/pushtorefresh/storio/pull/854): Table generation with few primary keys.
* [PR 856](https://github.com/pushtorefresh/storio/pull/856): Rewrite optional usage to allow Maybe implementation.
* [PR 857](https://github.com/pushtorefresh/storio/pull/857): Support io.reactivex.Maybe.
* [PR 858](https://github.com/pushtorefresh/storio/pull/858): Rename package to storio3.
* [PR 861](https://github.com/pushtorefresh/storio/pull/861): Fix maven url, update version.
* [PR 862](https://github.com/pushtorefresh/storio/pull/862): Android gradle plugin 3.0.1.
* [PR 864](https://github.com/pushtorefresh/storio/pull/864): Add interceptors for ContentResolver.
* [PR 865](https://github.com/pushtorefresh/storio/pull/865): Add gradle versions plugin.
* [PR 866](https://github.com/pushtorefresh/storio/pull/866): Mockito 2.13.0 and Mockito-Kotlin 1.5.0.
* [PR 867](https://github.com/pushtorefresh/storio/pull/867): Kotlin 1.2.0.
* [PR 870](https://github.com/pushtorefresh/storio/pull/870): Do not publish jar for android modules.

## Version 2.1.0

_2017_10_29_

* Table generation by annotation processor, thanks to @pbochenski and @geralt-encore!
* Remove exhaustive else from GetResolverGenerator utils.
* Some improvements in sample-projects, thanks to @ValeriusGC (it took us almost a year to merge…)
* Automated release and CI tweaks.

**Changes:**

* [PR 840](https://github.com/pushtorefresh/storio/pull/840): Table generation by annotation processor, thanks to @pbochenski and @geralt-encore!
* [PR 835](https://github.com/pushtorefresh/storio/pull/835): Remove exhaustive else from GetResolverGenerator utils.
* [PR 711](https://github.com/pushtorefresh/storio/pull/711): Improvements in sample-projects thanks @ValeriusGC.
* [PR 839](https://github.com/pushtorefresh/storio/pull/839): Fix readme links.
* [PR 841](https://github.com/pushtorefresh/storio/pull/841): Configure all signing params for automated release.
* [PR 842](https://github.com/pushtorefresh/storio/pull/842): Download Linux Android SDK on Travis instead of macOS.
* [PR 843](https://github.com/pushtorefresh/storio/pull/843): Minimize deploy logs, close nexus repo after upload.

## Version 2.0.3

_2017_10_16_

* No API/implementation changes, fine-tuning automatic release process.

**Changes:**

* [PR 836](https://github.com/pushtorefresh/storio/pull/836): Do clean release build to exclude Jacoco from jar.
* [PR 834](https://github.com/pushtorefresh/storio/pull/834): Use environment variable to detect publishing state.
* [PR 833](https://github.com/pushtorefresh/storio/pull/833): Disable debug builds for library modules.

## Version 2.0.2

_2017_10_16_

* No API/implementation changes, we're fine-tuning automated release process.

## Version 2.0.1

_2017_10_16_

* Add automatic deploy hooks to Travis config.
* Gradle 4.2.1.

**Changes:**

* [PR 830](https://github.com/pushtorefresh/storio/pull/830): Add automatic deploy hooks to Travis config.
* [PR 825](https://github.com/pushtorefresh/storio/pull/825): Gradle 4.2.1.
* [PR 823](https://github.com/pushtorefresh/storio/pull/823): Update readme according to Kotlin integration changes.
* [PR 829](https://github.com/pushtorefresh/storio/pull/829): Fix test parallelWritesWithoutTransaction.
* [PR 824](https://github.com/pushtorefresh/storio/pull/824): Fix Travis log overflow.

## Version 2.0.0

_2017_09_12_

* **Interceptors API!** :tada::tada: Many thanks to [@rsinukov](https://github.com/rsinukov)
* Logging via interceptors (just add `LoggingInterceptor`).
* Remove deprecated `createObservable` and `internal`. You should use `asRxObservable` and `lowLevel` instead.
* `mapFromCursor` receives `StorIOSqlite`/`StorIOContentResolver` as parameter.
* Remove `Query.CompleteBuilder.whereArgs(list)`. Please use vararg overload instead.
* Add ability to use vals instead of vars in classes for resolver generation.
* Fix message in case creator parameters do not match columns.
* Fix case with different classes having fields with the same names.
* Add SQLDelight interaction example.
* Gradle 3.5.
* Checkstyle 7.7.
* Kotlin 1.1.2.
* Gradle plugin 2.3.3.
* Support library 25.3.1.
* Compile testing tool 0.11.

**Changes:**

* [PR 542](https://github.com/pushtorefresh/storio/pull/542): Interceptors API and `LoggingInterceptor`.
* [PR 812](https://github.com/pushtorefresh/storio/pull/812): Remove deprecated `createObservable` and `internal`.
* [PR 817](https://github.com/pushtorefresh/storio/pull/817): Add `storIOSqlite` parameter to get resolver.
* [PR 818](https://github.com/pushtorefresh/storio/pull/818): Add `storIOContentResolver` parameter to get resolver.
* [PR 819](https://github.com/pushtorefresh/storio/pull/819): Remove `Query.CompleteBuilder.whereArgs(list)`.
* [PR 802](https://github.com/pushtorefresh/storio/pull/802): Add ability to use vals instead of vars in classes for resolver generation.
* [PR 797](https://github.com/pushtorefresh/storio/pull/797): Fix message in case creator parameters do not match columns.
* [PR 803](https://github.com/pushtorefresh/storio/pull/803): Gradle plugin 2.3.3. Fix case with different classes having fields with the same names.
* [PR 814](https://github.com/pushtorefresh/storio/pull/814): Add SQLDelight interaction example.
* [PR 790](https://github.com/pushtorefresh/storio/pull/790): Gradle 3.5, Checkstyle 7.7.
* [PR 792](https://github.com/pushtorefresh/storio/pull/792): Kotlin 1.1.2.
* [PR 794](https://github.com/pushtorefresh/storio/pull/794): Support library 25.3.1.
* [PR 816](https://github.com/pushtorefresh/storio/pull/816): Compile testing tool 0.11.

## Version 1.13.0

_2017_05_15_

* Support for Kotlin properties!
* Notification tags.
* Annotation processors in Kotlin.
* Robolectric 3.3.2.
* AssertJ 3.6.2.
* Fix markdown headers.

**Changes:**

* [PR 776](https://github.com/pushtorefresh/storio/pull/776): Support for Kotlin properties.
* [PR 768](https://github.com/pushtorefresh/storio/pull/768): Notification tags.
* [PR 775](https://github.com/pushtorefresh/storio/pull/775): Annotation processors in Kotlin.
* [PR 774](https://github.com/pushtorefresh/storio/pull/774): Robolectric 3.3.2 and AssertJ 3.6.2.
* [PR 772](https://github.com/pushtorefresh/storio/pull/772): Fix markdown headers.

## Version 1.12.3

_2017_02_19_

* Tests for StorIOSQLiteAnnotationsProcessor with [google compile testing](https://github.com/google/compile-testing). :tada::tada: Great work from [@geralt-encore](https://github.com/geralt-encore)!
* Tests for StorIOContentResolverAnnotationsProcessor with [google compile testing](https://github.com/google/compile-testing).
* JavaPoet 1.8. Fixes for [#763](https://github.com/pushtorefresh/storio/issues/763), thanks [@joelpet](https://github.com/joelpet) for reporting.
* Add to StorIO.LowLevel getter for underlying SQLiteOpenHelper.
* Fix for [#757](https://github.com/pushtorefresh/storio/issues/757) compilation error after applying column annotation on a private method.
* Mockito 2.7.7.

**Changes:**

* [PR 760](https://github.com/pushtorefresh/storio/pull/760): Compile testing for StorIOSQLiteAnnotationsProcessor.
* [PR 761](https://github.com/pushtorefresh/storio/pull/761): Compile testing for StorIOContentResolverAnnotationsProcessor.
* [PR 763](https://github.com/pushtorefresh/storio/pull/763): JavaPoet 1.8.
* [PR 706](https://github.com/pushtorefresh/storio/pull/706): Add to StorIO.LowLevel getter for underlying SQLiteOpenHelper.
* [PR 754](https://github.com/pushtorefresh/storio/pull/754): Fix compilation error after applying column annotation on a private method.
* [PR 762](https://github.com/pushtorefresh/storio/pull/762): Mockito 2.7.7.

## Version 1.12.2

_2017_01_22_

* Fixes for [#749](https://github.com/pushtorefresh/storio/issues/749), thanks to [@bluebery](https://github.com/bluebery) and [@michaelcarrano](https://github.com/michaelcarrano) for reporting.
* Fix typo in README. Thanks to [@mikeyxkcd](https://github.com/mikeyxkcd).
* Gradle 2.2.3.
* Build tools 25.0.2.
* Robolectric 3.1.4.

**Changes:**

* [PR 747](https://github.com/pushtorefresh/storio/pull/747): Updated gradle/plugin/tools versions.
* [PR 748](https://github.com/pushtorefresh/storio/pull/748): Update README.md dependency typo.
* [PR 750](https://github.com/pushtorefresh/storio/pull/750): Use annotationProcessor instead of apt.
* [PR 755](https://github.com/pushtorefresh/storio/pull/755): Update readme with annotationProcessor instead of apt.
* [PR 754](https://github.com/pushtorefresh/storio/pull/754): Fixes for resolver generators.

## Version 1.12.1

_2016_12_29_

* Fixes for AutoValue and Kotlin support in `StorIOSQLiteProcessor` and `StorIOContentResolverProcessor`, thanks to reporters and [@geralt-encore](https://github.com/geralt-encore) and [@hotchemi](https://github.com/hotchemi) for fixes!

**Changes:**

* [PR 743](https://github.com/pushtorefresh/storio/pull/743): Update readme with kapt2 for Kotlin support.
* [PR 742](https://github.com/pushtorefresh/storio/pull/742): Add missing annotation to Kotlin's example in README.
* [PR 740](https://github.com/pushtorefresh/storio/pull/740): Mapping parameters by name for Kotlin and AutoValue support.
* [PR 739](https://github.com/pushtorefresh/storio/pull/739): Fix AutoValue integration.

## Version 1.12.0

_2016_12_7_

* Support for **AutoValue** and **Kotlin** in StorIOSQLiteProcessor and StorIOContentResolverProcessor! :balloon::tada::fireworks: 100500 thanks to [@geralt-encore](https://github.com/geralt-encore)!
* DefaultStorIOSQLite now combines affected tables from pending changes. **After the end of transaction DefaultStorIOSQLite will send only one notification instead of multiple for every change**.
* Queries take generic args instead of objects.
* Add `RawQuery#affectsTables` and `RawQuery#observesTables` that take collection.

**Changes:**

* [PR 720](https://github.com/pushtorefresh/storio/pull/720) Support for AutoValue and Kotlin data classes in StorIOSQLiteProcessor.
* [PR 725](https://github.com/pushtorefresh/storio/pull/725) Support for AutoValue and Kotlin data classes in StorIOContentResolverProcessor.
* [PR 726](https://github.com/pushtorefresh/storio/pull/726) Update README with AutoValue and Kotlin examples.
* [PR 717](https://github.com/pushtorefresh/storio/pull/717) DefaultStorIOSQLite combines affected tables from pending changes.
* [PR 699](https://github.com/pushtorefresh/storio/pull/699) Queries take generic args instead of objects.
* [PR 698](https://github.com/pushtorefresh/storio/pull/698) Add RawQuery affectsTables and observesTables that take collection.

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
