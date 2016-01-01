# Code Coverage Report generation

# For Android Modules

Here are some of the andriod modules:
* storio-common
* storio-content-resolver
* storio-sample-app
* storio-sqlite
* storio-test-common
* storio-test-without-rxjava

To generate the code coverage report for android modules, execute the following command:

> Windows: ci.sh
> Linux/Unix/ OSX: ./ci.sh

This will generate code coverage report in each of the modules. In order to view the same, open the following file in your browser.
> ROOT/MODULE_NAME/build/reports/jacoco

Please note that the above folder is created under each of the modules. For example:
* pushtorefresh-storio/storio-common/build/reports/jacoco/testReleaseUnitTestCoverage/html/index.html
* pushtorefresh-storio/storio-content-resolver/build/reports/jacoco/testReleaseUnitTestCoverage/html/index.html

# For Non-Android Modules

Here are some of the non-andriod modules:
* storio-common-annotations-processor
* storio-content-resolver-annotations
* storio-content-resolver-annotations-processor
* storio-sqlite-annotations
* storio-sqlite-annotations-processor

To generate the coverage report for non-android modules, execute the following commands at the module's root directory:

> Windows: gradle jacocoTestReport
> Linux/Unix/ OSX: ./gradle jacocoTestReport

This will generate code coverage report in each of the modules. In order to view the same, open the following file in your browser.
> ROOT/MODULE_NAME/build/reports/jacoco

Please note that the above folder is created under each of the modules. For example:
* pushtorefresh-storio/storio-common-annotations-processor/build/reports/jacoco/test/index.html
* pushtorefresh-storio/storio-content-resolver-annotations/build/reports/jacoco/testReleaseUnitTestCoverage/html/index.html