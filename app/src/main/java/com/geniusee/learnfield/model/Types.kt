package com.geniusee.learnfield.model

typealias MusicalTick = Double
typealias SystemTime = Double
typealias MusicalTempo = Double

fun SystemTime.toMillis(): Long {
    return (this * 1000).toLong()
}