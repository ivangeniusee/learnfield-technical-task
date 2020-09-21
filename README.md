# Notes

Environments and tools
-------

* Android Studio 4.0+
* Kotlin
* Development Target: SDK 21+


Technical notes
-----------

### Using Koin
In iOS app there for dependency injection `Assebler` was used. In Android environment there are lot of depency injection libraries and one of our favorites is Koin wich is very powerful and lighweight and will do the same job as it was done in iOS app by `Assembler`.

### Using mp3
In iOS app audio files where in `aifc` format which is not supported by Android natively (https://developer.android.com/guide/topics/media/media-formats). So do not overcomplicate technical challendge we just converted it to mp3.

### Unneded dependecies
In iOS app `AudioSystemController` had `AudioEngineService` as depencecy which was responsibel for setting up audio input/output. In android there was no need of that because most of that work is done by Android when setting up a player. The other case when it would needed is when we want to play sound in background for a long period of time (like player apps doing it).

### Exoplayer
Android has it's own defaut `MediaPlayer`, but it has some restrictions (https://developer.android.com/guide/topics/media/exoplayer).
