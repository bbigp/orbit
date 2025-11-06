package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.di.SessionScope
import cn.coolbet.orbit.manager.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://feedo.coolbet.cn/"
const val XAuthToken = "lOEQiLk-6QtDmiIz9_AsoBmZrdeKBarjZyjTLyo4600="

@Module
@InstallIn(SingletonComponent::class)
object MinifluxClient {

    @Provides
    @SessionScope
    fun provideMiniFeedApi(retrofit: Retrofit): MiniFeedApi { // Hilt 自动注入 Retrofit
        return retrofit.create(MiniFeedApi::class.java)
    }

    @Provides
    @SessionScope
    fun provideMiniFolderApi(retrofit: Retrofit): MiniFolderApi {
        return retrofit.create(MiniFolderApi::class.java)
    }

    @Provides
    @SessionScope
    fun provideIconFileApi(retrofit: Retrofit): MinIconFileApi {
        return retrofit.create(MinIconFileApi::class.java)
    }

    @Provides
    @SessionScope
    fun createRetrofit(okHttpClient: OkHttpClient, preferenceManager: PreferenceManager): Retrofit {
        val baseUrl = preferenceManager.getBaseUrl()
        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
            .build()
    }

    @Provides
    @SessionScope
    fun createOkHttpClient(preferenceManager: PreferenceManager): OkHttpClient {
        // 日志拦截器，用于在 Logcat 中查看请求和响应细节
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 设置为 BODY 级别查看请求体和响应体
        }

        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val newRequest = originalRequest.newBuilder()
//                .header("X-Auth-Token", XAuthToken)
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

