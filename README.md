# Bridge

[![](https://jitpack.io/v/7Koston/bridge.svg)](https://jitpack.io/#7Koston/bridge)

A library for avoiding TransactionTooLargeException during state saving and restoration.

## Origin

https://github.com/livefront/bridge

## AndroidX friendly

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```gradle
dependencies {
    // FOR JAVA 7+
    implementation 'com.github.7Koston:bridge:1.3.0'
    // FOR JAVA 8+
    implementation 'com.github.7Koston:bridge:1.3.2'
}
```