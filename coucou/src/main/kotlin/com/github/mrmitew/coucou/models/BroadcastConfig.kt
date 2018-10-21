package com.github.mrmitew.coucou.models

import android.net.nsd.NsdServiceInfo
import com.github.mrmitew.coucou.engine.impl.nsd.TxtRecords
import com.github.mrmitew.coucou.engine.impl.nsd.setTxtRecords
import java.net.InetAddress

data class BroadcastConfig(val type: String,
                           val name: String,
                           val address: InetAddress? = null,
                           val port: Int,
                           val txtRecords: TxtRecords? = emptyMap())

internal fun BroadcastConfig.toNsdModel(): NsdServiceInfo =
        NsdServiceInfo().apply {
            val model = this@toNsdModel
            serviceType = model.type
            serviceName = model.name
            host = model.address
            port = model.port
            setTxtRecords(model.txtRecords)
        }