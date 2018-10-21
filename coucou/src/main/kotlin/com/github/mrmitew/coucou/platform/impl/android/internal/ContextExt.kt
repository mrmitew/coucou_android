package com.github.mrmitew.coucou.platform.impl.android.internal

import android.content.Context
import android.net.wifi.WifiManager

internal fun Context.getWifiManager(): WifiManager =
        this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
