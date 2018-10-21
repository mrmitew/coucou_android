package com.github.mrmitew.coucou.platform

import java.net.InetAddress

interface Platform {
    fun getWifiAddress(): InetAddress
    fun createConnection(): PlatformConnection
}