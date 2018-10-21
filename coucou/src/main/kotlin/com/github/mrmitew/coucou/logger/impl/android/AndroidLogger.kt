package com.github.mrmitew.coucou.logger.impl.android

import com.github.mrmitew.coucou.logger.Logger

class AndroidLogger : Logger {
    override fun v(tag: String, msg: String) = println("[$tag] $msg")
    override fun v(tag: String, msg: String, tr: Throwable) = println("[$tag] $msg; ${tr.message}")
    override fun d(tag: String, msg: String) = println("[$tag] $msg")
    override fun d(tag: String, msg: String, tr: Throwable) = println("[$tag] $msg ; ${tr.message}")
    override fun i(tag: String, msg: String) = println("[$tag] $msg")
    override fun i(tag: String, msg: String, tr: Throwable) = println("[$tag] $msg; ${tr.message}")
    override fun w(tag: String, msg: String) = println("[$tag] $msg")
    override fun w(tag: String, msg: String, tr: Throwable) = println("[$tag] $msg; ${tr.message}")
    override fun w(tag: String, tr: Throwable) = println("[$tag] ${tr.message}")
    override fun e(tag: String, msg: String) = println("[$tag] $msg")
    override fun e(tag: String, msg: String, tr: Throwable) = println("[$tag] $msg; ${tr.message}")
}