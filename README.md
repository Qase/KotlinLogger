[![Release](https://jitpack.io/v/Qase/KotlinLogger.svg)](https://jitpack.io/#Qase/KotlinLogger)
[![codebeat badge](https://codebeat.co/badges/d7306f5e-9328-45a0-bbed-1abab3e8b5b2)](https://codebeat.co/projects/github-com-qase-kotlinlogger-master)

## KotlinLogger

Smart android logger written in kotlin language.

Mostly used in Prague based android develpoment company - [Quanti](https://www.quanti.cz/) for everything.

## Features
* Usable in every JVM language including Java/Kotlin/Scala ...
* Very easy to use
* No more TAGs
* Easy to extend using your own logger
* Lot of optional parameters
* Lightweight
* Sample [app](github/sampleApp.png) is ready to build 

## Code Example

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

2) Add all needed logers to your mainActivity

```kotlin
Log.addLogger(BaseAndroidLog()); //forwards all log to android logcat
Log.addLogger(FileLoggerAsync(applicationContext)); 
Log.addLogger(CrashlyticsLogger()) //defualt logging level is warn
```

For usage of FileLoggerAsync is necesarry this permission in manifest file
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```


3) (Optionally) Enable unchecked crash handling
```kotlin
Log.useUncheckedErrorHandler()
```

4) Then log as you would normally do - just using another dependency
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

5) (Optionally) Use of SendLogDialogFragment

```kotlin
SendLogDialogFragment.newInstance("your@email.com").show(supportFragmentManager, "TAG")
```

<img src="github/dialog.png" width="250">


## Installation

Click [HERE](https://jitpack.io/#Qase/KotlinLogger).

## Future development
* send your requests

## License

Copyright 2018 Quanti s.r.o

Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
