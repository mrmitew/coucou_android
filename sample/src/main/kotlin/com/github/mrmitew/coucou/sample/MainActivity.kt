package com.github.mrmitew.coucou.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.mrmitew.coucou.Coucou
import com.github.mrmitew.coucou.driver.impl.nsd.NsdManagerDriver
import com.github.mrmitew.coucou.logger.impl.android.AndroidLogger
import com.github.mrmitew.coucou.models.DiscoveryEvent
import com.github.mrmitew.coucou.platform.impl.android.AndroidPlatform
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

    private val logger = AndroidLogger()

    private val coucou: Coucou by lazy {
        Coucou.create {
            platform { AndroidPlatform(applicationContext) }
            driver { NsdManagerDriver(applicationContext) }
            logger { logger }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logger.d(TAG, "Starting discovery")

        launch {
            coucou.startDiscovery(type = "_http._tcp.") {
                logger.d(TAG, it.asMap().toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun DiscoveryEvent.description(): String = when(this) {
        is DiscoveryEvent.Failure -> "failure"
        is DiscoveryEvent.ServiceResolved -> "resolved"
        is DiscoveryEvent.ServiceLost -> "lost"
    }

    private fun DiscoveryEvent.asMap(): Map<String, Any> = when(this) {
        is DiscoveryEvent.Failure -> mapOf(
            Pair("event", description()),
            Pair("cause", cause))
        is DiscoveryEvent.ServiceResolved -> mapOf(
            Pair("event", description()),
            Pair("service", service.toString()))
        is DiscoveryEvent.ServiceLost -> mapOf(
            Pair("event", description()),
            Pair("service", service.toString()))
    }
}
