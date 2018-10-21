package com.github.mrmitew.coucou.engine.impl.nsd

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.github.mrmitew.coucou.BroadcastFailedException
import com.github.mrmitew.coucou.driver.impl.nsd.NsdManagerDriver
import com.github.mrmitew.coucou.engine.BroadcastEngine
import com.github.mrmitew.coucou.models.BroadcastConfig
import com.github.mrmitew.coucou.models.toNsdModel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.net.InetAddress

class NsdManagerBroadcastEngine(private val nsdManager: NsdManager) : BroadcastEngine {
    private var listener: NsdManager.RegistrationListener? = null

    override suspend fun broadcast(address: InetAddress, config: BroadcastConfig) {
        suspendCancellableCoroutine<NsdServiceInfo> { cont ->
            listener = object : NsdManager.RegistrationListener {
                override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                    cont.resume(serviceInfo)
                }

                override fun onServiceUnregistered(service: NsdServiceInfo) {
                }

                override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, code: Int) {
                    cont.resumeWithException(
                        BroadcastFailedException(
                            NsdManagerDriver.DRIVER_NAME,
                            NsdBroadcastException(code)
                        )
                    )
                }

                override fun onUnregistrationFailed(p0: NsdServiceInfo?, code: Int) {
                    cont.resumeWithException(
                        BroadcastFailedException(
                            NsdManagerDriver.DRIVER_NAME,
                            NsdBroadcastException(code)
                        )
                    )
                }
            }

            val nsdServiceInfo = config.copy(address = config.address ?: address).toNsdModel()
            nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
        }
    }

    override fun dispose() {
        try {
            listener?.let {
                nsdManager.unregisterService(it)
            }
        } catch (_: IllegalArgumentException) {
        }
    }
}