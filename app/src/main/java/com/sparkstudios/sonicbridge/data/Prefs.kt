package com.sparkstudios.sonicbridge.data

import android.content.Context

object Prefs {

    private const val PREF_NAME = "sonic_bridge"

    private const val KEY_TV_IP = "tv_ip"

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveTvIp(
        context: Context,
        ip: String
    ) {

        prefs(context)
            .edit()
            .putString(KEY_TV_IP, ip)
            .apply()

    }

    fun getTvIp(
        context: Context
    ): String {

        return prefs(context)
            .getString(KEY_TV_IP, "")
            ?: ""

    }

    fun clearTvIp(
        context: Context
    ) {

        prefs(context)
            .edit()
            .remove(KEY_TV_IP)
            .apply()

    }

}