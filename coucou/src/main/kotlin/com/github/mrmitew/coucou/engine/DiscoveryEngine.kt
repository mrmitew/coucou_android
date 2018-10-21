package com.github.mrmitew.coucou.engine

import com.github.mrmitew.coucou.models.DiscoveryEvent
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import java.net.InetAddress

interface DiscoveryEngine : Engine {
    suspend fun discover(platformAddress: InetAddress): ReceiveChannel<DiscoveryEvent>
}