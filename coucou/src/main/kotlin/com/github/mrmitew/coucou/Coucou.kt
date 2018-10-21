package com.github.mrmitew.coucou

import com.github.mrmitew.coucou.Coucou.Builder
import com.github.mrmitew.coucou.driver.Driver
import com.github.mrmitew.coucou.engine.DiscoveryEngine
import com.github.mrmitew.coucou.internal.forEach
import com.github.mrmitew.coucou.logger.Logger
import com.github.mrmitew.coucou.models.BroadcastConfig
import com.github.mrmitew.coucou.models.DiscoveryEvent
import com.github.mrmitew.coucou.models.Disposable
import com.github.mrmitew.coucou.platform.Platform
import kotlinx.coroutines.experimental.CancellationException

/**
 * Coucou provides access to Network Service Discovery APIs using Kotlin's coroutines.
 * To use Coucou, obtain an instance through its [Builder], configure it by providing a [Driver] as well
 * as a [Platform] and finally build it. Optionally, you can also provide a [Logger] of your preference.
 */
class Coucou private constructor(
        private val platform: Platform,
        private val driver: Driver,
        private val logger: Logger?) {

    companion object {
        fun create(init: Builder.() -> Unit) = Builder(init).build()
    }

    /**
     * Configuration and creation of Coucou instances.
     */
    class Builder private constructor() {
        private var platform: Platform? = null
        private var driver: Driver? = null
        private var logger: Logger? = null

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        fun platform(init: Builder.() -> Platform) = also { this.platform = init() }
        fun driver(init: Builder.() -> Driver) = also { this.driver = init() }
        fun logger(init: Builder.() -> Logger) = also { this.logger = init() }

        fun build(): Coucou = Coucou(
            requireNotNull(platform) { "You need to provide a platform() to the builder" },
            requireNotNull(driver) { "You need to provide a driver() to the builder" },
            logger
        )
    }

    /**
     * Starts a network service discovery for the provided service type with the provided
     * [Driver].
     *
     * Cancellation of a discovery and disposing all used resources is done automatically
     * when the parent coroutine is cancelled or an exception has been thrown or emitted
     * by the [DiscoveryEngine].
     *
     * @throws [DiscoveryFailedException]
     * @param type Type of service to discover
     * @param action Callback that will be used whenever there is a new discovery event
     */
    suspend fun startDiscovery(type: String,
                               action: suspend (DiscoveryEvent) -> Unit) {

        val connection = platform.createConnection()
        val discovery = driver.createDiscovery(type)

        val disposingAction = {
            discovery.dispose()
            connection.dispose()
        }

        try {
            discovery.discover(platform.getWifiAddress())
                    .iterator()
                    .forEach {
                        if(it is DiscoveryEvent.Failure) disposingAction.invoke()
                        action(it)
                    }
        } catch (e: Exception) {
            logger?.e("Coucou", "Disposing discovery due to ${e.message}")
            disposingAction.invoke()
            if (e !is CancellationException) {
                throw e
            }
        }
    }

    /**
     * Starts a network service broadcast with a given configuration.
     *
     * @throws [BroadcastFailedException]
     * @param config Configuration of the service to advertise
     * @return Returns an optional [Disposable] that has to be disposed whenever the broadcast isn't needed.
     * If the disposable is null, then broadcast initialization has failed and there is nothing to dispose.
     */
    suspend fun startBroadcast(config: BroadcastConfig): Disposable? {
        val connection = platform.createConnection()
        val broadcastEngine = driver.createBroadcast()

        val address = config.address ?: platform.getWifiAddress()

        val disposable = object : Disposable {
            override fun dispose() {
                broadcastEngine.dispose()
                connection.dispose()
            }
        }

        @Suppress("LiftReturnOrAssignment")
        try {
            broadcastEngine.broadcast(address, config)
            return disposable
        } catch (e: Exception) {
            logger?.e("Coucou", "Disposing broadcast due to ${e.message}")
            disposable.dispose()

            if (e !is CancellationException) {
                throw e
            }

            return null
        }
    }
}