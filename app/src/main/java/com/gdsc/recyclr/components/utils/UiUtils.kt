package com.gdsc.recyclr.components.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

object UiUtils {
    fun print(e: Exception) = Log.e("APP TAG", e.stackTraceToString())

    fun showMessage(
        context: Context,
        message: String?
    ) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
