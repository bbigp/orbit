package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.di.Qualifiers
import cn.coolbet.orbit.manager.Session
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val minifluxModule = module {
    single(Qualifiers.NetworkMiniflux) {
        val session: Session = get()
        val loggingInterceptor: HttpLoggingInterceptor = get()

        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val newBaseUrl = session.baseURL.toHttpUrl()
            val newUrlBuilder = originalRequest.url.newBuilder()
                .scheme(newBaseUrl.scheme)
                .host(newBaseUrl.host)
                .port(newBaseUrl.port)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(newUrlBuilder)
                .header("X-Auth-Token", session.authToken)
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(newRequest)
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            // 设置超时
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    single(Qualifiers.NetworkMiniflux) {
        Retrofit.Builder()
            .baseUrl("https://orbit.cn")
            .client(get(Qualifiers.NetworkMiniflux))
            .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
            .build()
    }

    single { get<Retrofit>(Qualifiers.NetworkMiniflux).create(MiniFeedApi::class.java) }
    single { get<Retrofit>(Qualifiers.NetworkMiniflux).create(MiniFolderApi::class.java) }
    single { get<Retrofit>(Qualifiers.NetworkMiniflux).create(MinIconFileApi::class.java) }
    single { get<Retrofit>(Qualifiers.NetworkMiniflux).create(MiniEntryApi::class.java) }
}

//object MinifluxClient {
//
//    @Provides
//    @Singleton
//    fun provideMiniFeedApi(@Miniflux retrofit: Retrofit): MiniFeedApi { // Hilt 自动注入 Retrofit
//        return retrofit.create(MiniFeedApi::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideMiniFolderApi(@Miniflux retrofit: Retrofit): MiniFolderApi {
//        return retrofit.create(MiniFolderApi::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideIconFileApi(@Miniflux retrofit: Retrofit): MinIconFileApi {
//        return retrofit.create(MinIconFileApi::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideEntryApi(@Miniflux retrofit: Retrofit): MiniEntryApi {
//        return retrofit.create(MiniEntryApi::class.java)
//    }
//
//    @Provides
//    @Singleton
//    @Miniflux
//    fun createRetrofit(@Miniflux okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://orbit.cn")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    @Miniflux
//    fun createOkHttpClient(session: Session, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
//        val headerInterceptor = Interceptor { chain ->
//            val originalRequest = chain.request()
//
//            val newBaseUrl = session.baseURL.toHttpUrl()
//            val newUrlBuilder = originalRequest.url.newBuilder()
//                .scheme(newBaseUrl.scheme)
//                .host(newBaseUrl.host)
//                .port(newBaseUrl.port)
//                .build()
//            val newRequest = originalRequest.newBuilder()
//                .url(newUrlBuilder)
//                .header("X-Auth-Token", session.authToken)
//                .header("Content-Type", "application/json")
//                .build()
//
//            chain.proceed(newRequest)
//        }
//
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(headerInterceptor)
//            // 设置超时
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .build()
//    }
//}
//
