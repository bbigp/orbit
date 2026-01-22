package cn.coolbet.orbit.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Qualifiers {
    val NetworkApp = named("app")
    val NetworkMiniflux = named("miniflux")
}

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single(Qualifiers.NetworkApp) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    single(Qualifiers.NetworkApp) {
        Retrofit.Builder()
            .baseUrl("https://coolbet.cn")
            .client(get(Qualifiers.NetworkApp))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

//@Module
//@InstallIn(SingletonComponent::class) // ⭐️ 安装在应用级别
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
//        return HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//
//    @Provides
//    @Singleton
//    @App
//    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    @App
//    fun provideRetrofit(@App okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://coolbet.cn")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//}

//@Qualifier
//@Retention(AnnotationRetention.RUNTIME)
//annotation class App
//
//@Qualifier
//@Retention(AnnotationRetention.RUNTIME)
//annotation class Miniflux