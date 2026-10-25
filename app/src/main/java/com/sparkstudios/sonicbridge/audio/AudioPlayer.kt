package com.sparkstudios.sonicbridge.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

class AudioPlayer {
    companion object {
        const val SAMPLE_RATE = 48000
        const val CHANNEL_MASK = AudioFormat.CHANNEL_OUT_STEREO
        const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }

    private val bufferSize = AudioTrack.getMinBufferSize(
        SAMPLE_RATE, CHANNEL_MASK, ENCODING
    )

    private val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(ENCODING)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_MASK)
                .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
        .build()

    fun start() {
        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) audioTrack.play()
    }

    @Synchronized
    fun playAudio(data: ByteArray) {
        var offset = 0
        while (offset < data.size) {
            val written = audioTrack.write(
                data,
                offset,
                data.size - offset,
                AudioTrack.WRITE_BLOCKING
            )
            if (written <= 0) return
            offset += written
        }
    }

    fun pause() {
        if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) audioTrack.pause()
    }

    @Synchronized
    fun stop() {
        try {
            audioTrack.stop()
            audioTrack.flush()
        } catch (_: Exception) {
        }
    }

    fun release() {
        stop()
        audioTrack.release()
    }
}
