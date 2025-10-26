package cn.coolbet.orbit.remote.miniflux

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface ProfileApi {
    @GET("v1/icons/{iconID}")
    suspend fun icon(): IconFileResponse
}

data class IconFileResponse(
    val id: Long,
    @SerializedName("mime_type")
    val mimeType: String,
    val data: String
)