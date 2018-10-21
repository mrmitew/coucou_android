package com.github.mrmitew.coucou.engine.impl.nsd

internal class NsdDiscoveryException(code: Int) : RuntimeException("NsdManager Discovery error (code=$code)")
internal class NsdBroadcastException(code: Int) : RuntimeException("NsdManager Broadcast error (code=$code)")
