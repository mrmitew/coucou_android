package com.github.mrmitew.coucou.engine

import com.github.mrmitew.coucou.models.BroadcastConfig
import java.net.InetAddress

interface BroadcastEngine : Engine {
    suspend fun broadcast(address: InetAddress, config: BroadcastConfig)
}