package cn.coolbet.orbit.remote

import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.remote.miniflux.MiniFolderApi
import cn.coolbet.orbit.remote.miniflux.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class FolderApi @Inject constructor(
    private val apiProvider: Provider<MiniFolderApi>
) {
    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        apiProvider.get().getFolders().map { it.to() }
    }

}
