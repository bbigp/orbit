//package cn.coolbet.orbit.remote
//
//import cn.coolbet.orbit.remote.miniflux.IconFileResponse
//import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.concurrent.Volatile
//
//@Singleton
//class SessionAwareIconApi @Inject constructor() {
//    @Volatile private var actualApi: MinIconFileApi? = null
//
//    fun set(api: MinIconFileApi) {
//        this.actualApi = api
//    }
//
//    fun clear() {
//        this.actualApi = null
//    }
//
//    suspend fun icon(path: String): IconFileResponse {
//        return actualApi?.icon(path) ?: IconFileResponse.EMPTY
//    }
//
//}