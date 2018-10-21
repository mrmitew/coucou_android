package com.github.mrmitew.coucou

class IllegalServiceTypeException(type: String)
    : RuntimeException("The following is not a valid Bonjour type: $type")

class DiscoveryFailedException(driverName: String, cause: Exception?)
    : RuntimeException("Service Discovery Driver '$driverName' failed with an unrecoverable error" +
        if (cause != null) ": ${cause.message}" else "", cause)

class BroadcastFailedException(driverName: String, cause: Exception?)
    : RuntimeException("Service Broadcast Driver '$driverName' failed with an unrecoverable error" +
        if (cause != null) ": ${cause.message}" else "", cause)
