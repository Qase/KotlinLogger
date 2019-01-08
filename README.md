[![Release](https://jitpack.io/v/Qase/KotlinLogger.svg)](https://jitpack.io/#Qase/KotlinLogger)
[![Build Status](https://travis-ci.org/Qase/KotlinLogger.svg?branch=master)](https://travis-ci.org/Qase/KotlinLogger)
[![codebeat badge](https://codebeat.co/badges/d7306f5e-9328-45a0-bbed-1abab3e8b5b2)](https://codebeat.co/projects/github-com-qase-kotlinlogger-master)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maintainer: kidal5](https://img.shields.io/badge/Maintainer-kidal5-blue.svg)](mailto:vladislav.trnka@quanti.cz)
[![Qase: KotlinLogger](https://img.shields.io/badge/Qase-KotlinLogger-ff69b4.svg)](https://github.com/Qase/KotlinLogger)



## KotlinLogger

Smart android logger written in Kotlin language.

Mostly used in Prague based android develpoment company - [Quanti](https://www.quanti.cz/) for everything. Product is still being actively developed.

## Features
* Usable in every JVM language including Java/Kotlin/Scala ...
* Very easy to use
* No more TAGs, but you can still use them
* Easy to extend using your own logger
* Lot of optional parameters
* Lightweight
* Possibility to print system info
* Sample [app](github/sampleApp.png) is ready to build

## Installation

Click [HERE](https://jitpack.io/#Qase/KotlinLogger).

## Code example

Usage is simple

1) Make sure you have set your applicationId in gradle 
```
android{
  defaultConfig{
    applicationId = "your.cool.app"
    ...
  }
  ...
}
```

2) Add all needed logers to your mainActivity. Every logger is singleton and some of them needs to be initialized using init method.

```kotlin
Log.addLogger(AndroidLogger)           //forwards all log to android logcat
AndroidLogger.init(LoggerBundle())     //not necesarry

Log.addLogger(CrashlyticsLogger)       //default logging level is warn
CrashlyticsLogger.init(LoggerBundle()) //not necesarry

Log.addLogger(FileLogger); 
FileLogger.init(applicationContext)    //NECESARRY, need appContext for access to files

```

For usage of FileLogger is necesarry this permission in manifest file
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

3) Then log as you would normally do - just using another dependency
(or log sync using Log.xSync methods)

```kotlin
Log.v("This");
Log.d("is");
Log.i("sample");
Log.i("text");
Log.i("that");
Log.i("will");
Log.iSync("be");
Log.w("logged.");
Log.e("wi", Exception()); //throwable
```

4) Other possibilities 
```kotlin
//Enable unchecked crash handling
Log.useUncheckedErrorHandler()

//Delete all logs
FileLogger.deleteAllLogs()

//Print system logs
Log.logMetadata(appContext)

//Use of SendLogDialogFragment
SendLogDialogFragment.newInstance("your@email.com", deleteLogs = true).show(supportFragmentManager, "TAG")
```
<img src="github/dialog.png" width="250">


## License
[MIT](https://github.com/nishanths/license/blob/master/LICENSE)
