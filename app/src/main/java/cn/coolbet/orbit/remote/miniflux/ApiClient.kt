package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.di.Miniflux
import cn.coolbet.orbit.di.SessionComponent
import cn.coolbet.orbit.di.SessionScope
import cn.coolbet.orbit.manager.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://feedo.coolbet.cn/"
const val XAuthToken = "lOEQiLk-6QtDmiIz9_AsoBmZrdeKBarjZyjTLyo4600="

@Module
@InstallIn(SessionComponent::class)
object MinifluxClient {

    @Provides
    @SessionScope
    fun provideMiniFeedApi(@Miniflux retrofit: Retrofit): MiniFeedApi { // Hilt 自动注入 Retrofit
        return retrofit.create(MiniFeedApi::class.java)
    }

    @Provides
    @SessionScope
    fun provideMiniFolderApi(@Miniflux retrofit: Retrofit): MiniFolderApi {
        return retrofit.create(MiniFolderApi::class.java)
    }

    @Provides
    @SessionScope
    fun provideIconFileApi(@Miniflux retrofit: Retrofit): MinIconFileApi {
        return retrofit.create(MinIconFileApi::class.java)
    }

    @Provides
    @SessionScope
    @Miniflux
    fun createRetrofit(@Miniflux okHttpClient: OkHttpClient, preferenceManager: PreferenceManager): Retrofit {
        val baseUrl = preferenceManager.getBaseURL()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
            .build()
    }

    @Provides
    @SessionScope
    @Miniflux
    fun createOkHttpClient(preferenceManager: PreferenceManager, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val newRequest = originalRequest.newBuilder()
                .header("X-Auth-Token", preferenceManager.getAuthToken())
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            // 设置超时
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }
}

