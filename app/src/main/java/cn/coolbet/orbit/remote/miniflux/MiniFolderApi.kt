package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.Folder
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET


interface MiniFolderApi {
    @GET("v1/categories")
    suspend fun getFolders() : List<CategoryResponse>
}

fun CategoryResponse.to(): Folder {
    return Folder(id = this.id, title = this.title, userId = this.userId, hideGlobally = this.hideGlobally)
}

data class CategoryResponse(
    val id: Long,
    val title: String,

    @SerializedName("user_id")
    val userId: Long,

    @SerializedName("hide_globally")
    val hideGlobally: Boolean
)