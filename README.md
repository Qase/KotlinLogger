[![](https://jitpack.io/v/kidal5/KotlinLogger.svg)](https://jitpack.io/#kidal5/KotlinLogger)

## KotlinLogger

Basic android logger written in kotlin language using reactive style of programming.

Mostly used in Prague based android develpoment company - [Quanti](https://www.quanti.cz/) for everything.

## Code Example

Usage is simple

1) To your app class add all logers that zou want to log on
```java
Log.addLogger(new FileLogger(this));
Log.addLogger(new BaseAndroidLog());
```

2) Then log as you would normally do - just using another dependency

```java
Log.v("Text");
Log.i("Text");
Log.w("Text");
Log.d("Text");
Log.e("Text");
Log.e("Text", new Exception());
```

## Installation

Project si hosted on [Jitpack](https://jitpack.io) so the basic installation is found [here](https://jitpack.io/#kidal5/KotlinLogger/v1.9).

## License

Copyright 2016 Quanti s.r.o

Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
