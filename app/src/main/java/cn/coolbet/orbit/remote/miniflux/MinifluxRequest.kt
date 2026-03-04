package cn.coolbet.orbit.remote.miniflux

import org.json.JSONObject
import retrofit2.HttpException

suspend inline fun <T> minifluxRequest(crossinline block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        throw IllegalStateException(e.toMinifluxErrorMessage(), e)
    }
}

fun HttpException.toMinifluxErrorMessage(): String {
    val body = response()?.errorBody()?.string()
    if (!body.isNullOrBlank()) {
        try {
            val message = JSONObject(body).optString("error_message")
            if (message.isNotBlank()) return message
        } catch (_: Exception) {
        }
    }
    return "HTTP ${code()}"
}
