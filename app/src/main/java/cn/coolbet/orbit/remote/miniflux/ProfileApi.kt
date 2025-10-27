package cn.coolbet.orbit.remote.miniflux

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {
    @GET("{path}")
    suspend fun icon(@Path("path") path: String): IconFileResponse
}

data class IconFileResponse(
    val id: Long,
    @SerializedName("mime_type")
    val mimeType: String,
    val data: String
)