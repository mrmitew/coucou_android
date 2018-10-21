package com.github.mrmitew.coucou.models

import com.github.mrmitew.coucou.engine.impl.nsd.TxtRecords
import java.net.Inet4Address
import java.net.Inet6Address

data class DiscoveryService(
        val type: String,
        val name: String,
        val v4Host: Inet4Address?,
        val v6Host: Inet6Address?,
        val port: Int,
        val txtRecords: TxtRecords = emptyMap()) {
    val host = v4Host ?: v6Host
}