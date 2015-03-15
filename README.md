### `StorIO — modern API for SQLiteDatabase and ContentProvider` 

######Overview:
* Powerful set of operations: `Put`, `Get`, `Delete`
* Convinient builders. Forget about 6-7 `null` in queries
* Every operation over `StorIO` can be executed as blocking call or as `Observable`
* `RxJava` as first class citizen, but it's not required dependency! 
* `Query` can observe changes in `StorIO` and receive updates automatically, thx to `RxJava`
* If you don't want to work with `Cursor` and `ContentValue` you don't have to
* `StorIO` is mockable for testing

