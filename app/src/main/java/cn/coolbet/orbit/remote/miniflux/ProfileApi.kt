package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.User
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.Date

interface ProfileApi {
    @GET("{path}")
    suspend fun icon(@Path("path") path: String): IconFileResponse

    @GET
    suspend fun me(@Url url: String, @Header("X-Auth-Token") apiKey: String): MeResponse
}

data class IconFileResponse(
    val id: Long,
    @SerializedName("mime_type")
    val mimeType: String,
    val data: String
)

data class MeResponse(
    val id: Long,
    val username: String = "",
)

fun MeResponse.to(baseURL: String, apiKey: String): User {
    return User(id = id, username = username, baseURL = baseURL, token = apiKey)
}
