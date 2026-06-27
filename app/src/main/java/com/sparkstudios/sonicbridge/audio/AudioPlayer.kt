package com.sparkstudios.sonicbridge.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import androidx.annotation.RequiresApi

class AudioPlayer {

    companion object {

        const val SAMPLE_RATE = 48000

        const val CHANNEL_MASK =
            AudioFormat.CHANNEL_OUT_STEREO

        const val ENCODING =
            AudioFormat.ENCODING_PCM_16BIT

    }

    private val bufferSize =
        AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_MASK,
            ENCODING
        ) * 2

    private val audioTrack =
        AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),

            AudioFormat.Builder()
                .setEncoding(ENCODING)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_MASK)
                .build(),

            bufferSize,

            AudioTrack.MODE_STREAM,

            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

    fun start() {

        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) {

            audioTrack.play()

        }

    }

    fun playAudio(data: ByteArray) {

        audioTrack.write(
            data,
            0,
            data.size,
            AudioTrack.WRITE_BLOCKING
        )

    }

    fun pause() {

        if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {

            audioTrack.pause()

        }

    }

    fun stop() {

        try {

            audioTrack.stop()

        } catch (_: Exception) {
        }

    }

    fun release() {

        stop()

        audioTrack.flush()

        audioTrack.release()

    }

}