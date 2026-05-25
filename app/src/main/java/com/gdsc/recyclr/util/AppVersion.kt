package com.gdsc.recyclr.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

data class AppVersionInfo(
    val versionName: String,
    val buildNumber: String,
)

fun readAppVersionInfo(context: Context): AppVersionInfo {
    val info = runCatching {
        val pm = context.packageManager
        val pkg = context.packageName
        if (Build.VERSION.SDK_INT >= 33) {
            pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(pkg, 0)
        }
    }.getOrNull()

    val versionName = info?.versionName.orEmpty().ifBlank { "—" }
    val buildNumber = info?.let {
        if (Build.VERSION.SDK_INT >= 28) it.longVersionCode else @Suppress("DEPRECATION") it.versionCode.toLong()
    }?.toString() ?: "—"

    return AppVersionInfo(versionName, buildNumber)
}
