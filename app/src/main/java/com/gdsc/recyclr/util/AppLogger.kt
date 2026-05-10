package com.gdsc.recyclr.util

import android.util.Log

/**
 * Logs centralisés : préférer à des crashes silencieux pour dépendances ou optionnels manquants.
 */
object AppLogger {
    private const val TAG = "Recyclr"

    fun d(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.d(TAG, message, throwable) else Log.d(TAG, message)
    }

    fun i(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.i(TAG, message, throwable) else Log.i(TAG, message)
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.w(TAG, message, throwable) else Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e(TAG, message, throwable) else Log.e(TAG, message)
    }

    inline fun runCatchingLog(tagHint: String, block: () -> Unit) {
        runCatching { block() }.onFailure { e("[$tagHint] — non bloquant", it) }
    }
}
