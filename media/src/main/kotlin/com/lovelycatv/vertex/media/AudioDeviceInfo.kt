package com.lovelycatv.vertex.media

import javax.sound.sampled.Mixer

data class AudioDeviceInfo(
    val name: String,
    val description: String,
    val vendor: String,
    val version: String,
    val mixerInfo: Mixer.Info
) {
    override fun toString(): String = name
}