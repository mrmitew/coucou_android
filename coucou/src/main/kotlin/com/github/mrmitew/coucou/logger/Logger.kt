package com.github.mrmitew.coucou.logger

interface Logger {
    fun v(tag: String, msg: String)
    fun v(tag: String, msg: String, tr: Throwable)
    fun d(tag: String, msg: String)
    fun d(tag: String, msg: String, tr: Throwable)
    fun i(tag: String, msg: String)
    fun i(tag: String, msg: String, tr: Throwable)
    fun w(tag: String, msg: String)
    fun w(tag: String, msg: String, tr: Throwable)
    fun w(tag: String, tr: Throwable)
    fun e(tag: String, msg: String)
    fun e(tag: String, msg: String, tr: Throwable)
}