# StuDo

![Release](https://img.shields.io/badge/Release-v0.1.1-informational.svg?style=flat)
![Android 6.0+](https://img.shields.io/badge/Android-6.0%2B-informational.svg)

Android app for [StuDo](https://studo.rtuitlab.ru) –– the service for event organization and promotion inside RTU university

## APK download
* [Download on GitHub](https://github.com/RTUITLab/StuDo-Android/releases)

## Building
* Clone repository:
    * `git clone --recursive https://github.com/RTUITLab/StuDo-Android.git`
* The project can be built with Android Studio 3.6.3+

## How to start
By default, app use `https://studo.rtuitlab.ru/api/` as API URL. If you want to use your own API URL you need to provide it in the `local.properties` file like so:
```groovy
api.url=YourURLHere
```

## Translations
* English
* Russian

## StuDo is built with latest development tools and design patterns
* Written with [Kotlin](https://kotlinlang.org/)
* [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for asynchronous operations
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture):
    * [Viewmodels](https://developer.android.com/topic/libraries/architecture/viewmodel)
    * [Livedata](https://developer.android.com/topic/libraries/architecture/livedata)
    * [Databinding](https://developer.android.com/topic/libraries/data-binding)
* [Navigation component](https://developer.android.com/guide/navigation)
* [Dependency injection](https://developer.android.com/training/dependency-injection)

## Third party libraries
* [Retrofit](https://github.com/square/retrofit)
* [Koin](https://github.com/InsertKoinIO/koin)
* [RxMarkdown](https://github.com/yydcdut/RxMarkdown)
* [JWTDecode.Android](https://github.com/auth0/JWTDecode.Android)
* [Progress Button Android](https://github.com/razir/ProgressButton)