package com.github.mrmitew.coucou.driver

import com.github.mrmitew.coucou.engine.BroadcastEngine
import com.github.mrmitew.coucou.engine.DiscoveryEngine

interface Driver {
    val name: String
    fun createDiscovery(type: String): DiscoveryEngine
    fun createBroadcast(): BroadcastEngine
}