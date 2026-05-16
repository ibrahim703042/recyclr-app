package com.gdsc.recyclr.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

/** Résout l’[ComponentActivity] sans cast brutal (évite les ClassCastException). */
fun Context.findComponentActivity(): ComponentActivity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    return ctx as? ComponentActivity
}
