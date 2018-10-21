package com.github.mrmitew.coucou.platform.impl.android

import android.content.Context
import com.github.mrmitew.coucou.platform.Platform
import com.github.mrmitew.coucou.platform.PlatformConnection
import com.github.mrmitew.coucou.platform.impl.android.internal.byteIpAddress
import com.github.mrmitew.coucou.platform.impl.android.internal.getWifiManager
import java.net.InetAddress

class AndroidPlatform(private val context: Context) : Platform {
    override fun createConnection(): PlatformConnection = AndroidConnection(context)

    override fun getWifiAddress(): InetAddress =
            InetAddress.getByAddress(getConnectionInfo().byteIpAddress())

    private fun getWifiManager() = context.getWifiManager()
    private fun getConnectionInfo() = getWifiManager().connectionInfo
}

