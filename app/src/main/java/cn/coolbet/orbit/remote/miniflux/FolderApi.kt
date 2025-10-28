package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.Folder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FolderApi @Inject constructor(
    private val api: MiniFolderApi
) {
    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        api.getFolders().map { it.to() }
    }

}


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