StorIO Change Log
==========

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
