package cn.coolbet.orbit.remote

import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.remote.miniflux.MiniFolderApi
import cn.coolbet.orbit.remote.miniflux.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderApi(
    private val miniFolderApi: MiniFolderApi
) {
    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        miniFolderApi.getFolders().map { it.to() }
    }

}
