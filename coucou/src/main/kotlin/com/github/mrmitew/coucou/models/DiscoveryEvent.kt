package com.github.mrmitew.coucou.models

sealed class DiscoveryEvent {
    data class Failure(val cause: Exception) : DiscoveryEvent()
    data class ServiceResolved(val service: DiscoveryService) : DiscoveryEvent()
    data class ServiceLost(val service: DiscoveryService) : DiscoveryEvent()
}