[![Release](https://jitpack.io/v/kidal5/KotlinLogger2.svg)](https://jitpack.io/#kidal5/KotlinLogger2)

## KotlinLogger2

Smart android logger written in kotlin language.

Mostly used in Prague based android develpoment company - [Quanti](https://www.quanti.cz/) for everything.

## Features
* Very easy to use
* No more TAGs
* Easy to extend using your own logger
* Lot of optional parameters
* Lightweight
* Sample [app](github/sampleApp.png) is ready to build 

## Code Example

Usage is simple

1) Add all needed logers to your mainActivity

```kotlin
Log.addLogger(BaseAndroidLog()); //forwards all log to android logcat
Log.addLogger(FileLoggerAsync(applicationContext)); 
```

2) (Optionally) Enable unchecked crash handling
```kotlin
Log.useUncheckedErrorHandler()
```

3) Then log as you would normally do - just using another dependency

```kotlin
Log.v("This");
Log.d("is");
Log.i("sample");
Log.i("text");
Log.i("that");
Log.i("will");
Log.i("be");
Log.w("logged.");
Log.e("wi", Exception()); //throwable
```

4) (Optionally) Use of SendLogDialogFragment

```kotlin
SendLogDialogFragment.newInstance("your@email.com").show(supportFragmentManager, "TAG")
```
![dialog](github/dialog.png "dialog")

## Installation

Project si hosted on [Jitpack](https://jitpack.io) so the basic installation is found [here](https://jitpack.io/#kidal5/KotlinLogger2).

## Future development
* add Crashlytics connection
* send your requests

## License

Copyright 2018 Quanti s.r.o

Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
