package cn.coolbet.orbit.remote.miniflux

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface MiniLoginApi {
    @GET
    suspend fun me(@Url url: String, @Header("X-Auth-Token") authToken: String): MeResponse
}