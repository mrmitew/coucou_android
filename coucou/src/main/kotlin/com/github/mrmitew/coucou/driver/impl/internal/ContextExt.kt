package com.github.mrmitew.coucou.driver.impl.internal

import android.content.Context
import android.net.nsd.NsdManager

internal fun Context.getNsdManager() = this.getSystemService(Context.NSD_SERVICE) as NsdManager