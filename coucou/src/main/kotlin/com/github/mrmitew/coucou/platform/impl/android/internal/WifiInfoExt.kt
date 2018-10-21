package com.github.mrmitew.coucou.platform.impl.android.internal

import android.net.wifi.WifiInfo

internal fun WifiInfo.byteIpAddress() = byteArrayOf(
        (ipAddress and 0xff).toByte(),
        (ipAddress shr 8 and 0xff).toByte(),
        (ipAddress shr 16 and 0xff).toByte(),
        (ipAddress shr 24 and 0xff).toByte())