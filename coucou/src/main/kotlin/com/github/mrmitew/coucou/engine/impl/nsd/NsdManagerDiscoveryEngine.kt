package com.github.mrmitew.coucou.engine.impl.nsd

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.github.mrmitew.coucou.DiscoveryFailedException
import com.github.mrmitew.coucou.driver.impl.nsd.NsdManagerDriver
import com.github.mrmitew.coucou.engine.DiscoveryEngine
import com.github.mrmitew.coucou.models.DiscoveryEvent
import com.github.mrmitew.coucou.models.DiscoveryService
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.net.InetAddress
import java.util.concurrent.CancellationException
import kotlin.coroutines.experimental.CoroutineContext

class NsdManagerDiscoveryEngine(private val nsdManager: NsdManager,
                                         private val type: String) : DiscoveryEngine {

    private var backlog: NsdResolveBacklog? = null
    private var listener: NsdDiscoveryListener? = null

    override suspend fun discover(platformAddress: InetAddress):
            ReceiveChannel<DiscoveryEvent> {
        val channel = Channel<DiscoveryEvent>(capacity = Channel.CONFLATED)
        val resolveBacklog = NsdResolveBacklog(nsdManager, channel)
        val discoveryListener = NsdDiscoveryListener(channel, resolveBacklog)

        backlog = resolveBacklog
        listener = discoveryListener

        nsdManager.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

        return channel
    }

    override fun dispose() {
        try {
            listener?.let { nsdManager.stopServiceDiscovery(it) }
        } catch (_: Exception) {
        } finally {
            backlog?.close()
            listener = null
            backlog = null
        }
    }

    private class NsdDiscoveryListener(val channel: SendChannel<DiscoveryEvent>,
                                       val backlog: NsdResolveBacklog)
        : NsdManager.DiscoveryListener {
        override fun onServiceFound(service: NsdServiceInfo) {
            // At this point we don't have a lot of information about the service
            // We'll add it to the backlog for resolving
            backlog.queue(service)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            channel.offer(DiscoveryEvent.ServiceLost(service.toDiscoveryService()))
        }

        override fun onStartDiscoveryFailed(type: String?, code: Int) {
            channel.close(
                DiscoveryFailedException(
                    NsdManagerDriver.DRIVER_NAME,
                    NsdDiscoveryException(code)
                )
            )
        }

        override fun onStopDiscoveryFailed(type: String?, code: Int) {
            channel.close(
                DiscoveryFailedException(
                    NsdManagerDriver.DRIVER_NAME,
                    NsdDiscoveryException(code)
                )
            )
        }

        override fun onDiscoveryStarted(p0: String?) {
            // TODO: Propagate via the [channel]
        }

        override fun onDiscoveryStopped(p0: String?) {
            // TODO: Propagate via the [channel]
        }
    }
}

/**
 *  Linear Processor of found NsdServiceInfo objects.
 *  Necessary because of NsdManager's "one resolve at a time" limitation
 */
private class NsdResolveBacklog(
        private val nsdManager: NsdManager,
        private val sendChannel: SendChannel<DiscoveryEvent>) : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val backlogActor =
            actor<NsdServiceInfo>(capacity = Channel.UNLIMITED) {
                channel.consumeEach {
                    try {
                        sendChannel.offer(DiscoveryEvent.ServiceResolved(nsdManager.resolveService(it)))
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            sendChannel.offer(DiscoveryEvent.Failure(e))
                        }
                    }
                }
            }

    private suspend fun NsdManager.resolveService(service: NsdServiceInfo) =
            suspendCancellableCoroutine<DiscoveryService> { cont ->
                resolveService(service, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(p0: NsdServiceInfo?, code: Int) {
                        cont.resumeWithException(
                            DiscoveryFailedException(
                                NsdManagerDriver.DRIVER_NAME,
                                NsdDiscoveryException(code)
                            )
                        )
                    }

                    override fun onServiceResolved(service: NsdServiceInfo) {
                        cont.resume(service.toDiscoveryService())
                    }
                })
            }

    /**
     *  Terminates the work of this backlog instance
     */
    internal fun close() {
        sendChannel.close()
        backlogActor.close()
    }

    /**
     * Adds the provided item to the backlog's queue for processing
     */
    internal fun queue(service: NsdServiceInfo) {
        backlogActor.offer(service)
    }
}