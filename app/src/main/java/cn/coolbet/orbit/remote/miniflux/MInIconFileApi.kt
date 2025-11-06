package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.User
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface MinIconFileApi {
    @GET("{path}")
    suspend fun icon(@Path("path") path: String): IconFileResponse
}

data class IconFileResponse(
    val id: Long,
    @SerializedName("mime_type")
    val mimeType: String,
    val data: String
) {
    companion object {
        val EMPTY = IconFileResponse(id = 0, mimeType = "", data = "")
    }
}

data class MeResponse(
    val id: Long,
    val username: String = "",
)

fun MeResponse.to(baseURL: String, authToken: String): User {
    return User(id = id, username = username, baseURL = baseURL, authToken = authToken)
}
