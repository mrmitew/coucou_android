[![](https://jitpack.io/v/mrmitew/coucou_android.svg)](https://jitpack.io/#mrmitew/coucou_android)

# Coucou (Android)
A highly modular network service discovery and broadcast library that utilizes Kotlin's coroutines. For iOS, go to [coucou_ios](https://github.com/mrmitew/coucou_ios) repo.

## Coucou API
```kotlin
suspend fun startDiscovery(type: String, action: suspend (DiscoveryEvent) -> Unit)
suspend fun startBroadcast(config: BroadcastConfig): Disposable?
```

## How to use

### Creation
```kotlin
val coucou = Coucou.create {
    platform { AndroidPlatform(applicationContext) }
    driver { NsdManagerDriver(applicationContext) }
    logger { AndroidLogger() }
}
```
### Network Service Discovery
```kotlin
try {
    coucou.startDiscovery(type = "_http._tcp.") { event ->
        when(event) {
            is DiscoveryEvent.Failure -> {
                // TODO: something with event.cause
            }
            is DiscoveryEvent.ServiceResolved -> {
                // TODO: something with event.service
            }
            is DiscoveryEvent.ServiceLost -> {
                // TODO: something with event.service
            }
        }
    }
} catch (e: Exception) {
    // TODO
}
```

To cancel discovery, just call the `cancel()` on the coroutine's job. You can obtain it from the coroutine builder or from the coroutine's context.

### Network Service Broadcast
```kotlin
val disposable = coucou.startBroadcast(config = BroadcastConfig(type = "_http._tcp.", name = "Coucou", port = 8080)) 
// .... whenever the broadcast isn't needed, dispose
disposable?.dispose()
```

* Note: the discovery and broadcast have to be executed in suspending functions or within coroutines. Please see the sample app for a better example.

## Installation

### Gradle

Modify your root build.gradle to look like so:
```groovy
allprojects {
    repositories {
        // other repos here
        maven { url 'https://jitpack.io' }
    }
}
```

Then add the libray dependency to your app's build.gradle
```groovy
implementation 'com.github.mrmitew:coucou_android:<ENTER_COUCOU_VERSION_HERE>'
```

Done.

## To do
* Write unit tests 
* Complete sample
* More documentation
* Improve the README

# Credits
Inspiration for this library and software architecture was taken from [RxBonjour](https://github.com/mannodermaus/RxBonjour) by Marcel Schnelle.
