package com.github.mrmitew.coucou.internal

import com.github.mrmitew.coucou.models.DiscoveryEvent
import kotlinx.coroutines.experimental.channels.ChannelIterator

/**
 * Performs the given action on each element of this [ChannelIterator].
 */
internal suspend fun ChannelIterator<DiscoveryEvent>.forEach(
        action: suspend (DiscoveryEvent) -> Unit) = run {
    while (hasNext()) {
        action(next())
    }
}