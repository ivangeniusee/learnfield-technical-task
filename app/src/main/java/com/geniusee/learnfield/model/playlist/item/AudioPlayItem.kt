package com.geniusee.learnfield.model.playlist.item

import androidx.annotation.RawRes

data class AudioPlayItem(
    @RawRes val resource: Int,
    val name: String
) : PlayItem