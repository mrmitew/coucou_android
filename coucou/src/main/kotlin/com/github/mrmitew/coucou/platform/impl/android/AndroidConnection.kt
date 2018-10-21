package com.github.mrmitew.coucou.platform.impl.android

import android.content.Context
import android.net.wifi.WifiManager
import com.github.mrmitew.coucou.platform.PlatformConnection
import com.github.mrmitew.coucou.platform.impl.android.internal.getWifiManager

class AndroidConnection(context: Context) : PlatformConnection {
    companion object {
        private const val WIFI_MULTICAST_LOCK_TAG = "multicast_lock"
    }

    private var multicastLock: WifiManager.MulticastLock? = null

    init {
        context.getWifiManager()
                .createMulticastLock(WIFI_MULTICAST_LOCK_TAG).apply {
                    setReferenceCounted(true)
                    acquire()
                }
    }

    override fun dispose() {
        multicastLock?.release()
    }
}