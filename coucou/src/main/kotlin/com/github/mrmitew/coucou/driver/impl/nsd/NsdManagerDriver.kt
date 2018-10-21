package com.github.mrmitew.coucou.driver.impl.nsd

import android.content.Context
import android.net.nsd.NsdManager
import com.github.mrmitew.coucou.driver.Driver
import com.github.mrmitew.coucou.driver.impl.internal.getNsdManager
import com.github.mrmitew.coucou.engine.BroadcastEngine
import com.github.mrmitew.coucou.engine.DiscoveryEngine
import com.github.mrmitew.coucou.engine.impl.nsd.NsdManagerBroadcastEngine
import com.github.mrmitew.coucou.engine.impl.nsd.NsdManagerDiscoveryEngine

/**
 * Driver implementation using Android's [NsdManager].
 */
class NsdManagerDriver constructor(private val context: Context) : Driver {
    companion object {
        const val DRIVER_NAME = "nsdmanager"
    }

    override val name: String get() = DRIVER_NAME

    override fun createDiscovery(type: String): DiscoveryEngine =
            NsdManagerDiscoveryEngine(context.getNsdManager(), type)

    override fun createBroadcast(): BroadcastEngine =
            NsdManagerBroadcastEngine(context.getNsdManager())
}