package cn.coolbet.orbit.common

import android.content.Context

fun Context.readAssetText(fileName: String): String {
    return runCatching {
        assets.open(fileName).use { it.bufferedReader().readText() }
    }.getOrDefault("")
}