package cn.coolbet.orbit.di

import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
import dagger.Component
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

@Scope // 告诉 Dagger/Hilt 这是一个自定义作用域
@Retention(AnnotationRetention.RUNTIME)
annotation class SessionScope


@SessionScope
@Component(dependencies = [SingletonComponent::class]) // SessionComponent 依赖于 SingletonComponent
interface SessionComponent {

    // SessionManager (@Singleton 级) 需要通过这个方法获取 @SessionScope 实例
    fun minIconFileApi(): MinIconFileApi

    @Component.Factory
    interface Factory {
        fun create(): SessionComponent
    }

//    @EntryPoint
//    @InstallIn(SessionComponent::class)
//    interface SessionEntryPoint {
//        fun minIconFileApi(): MinIconFileApi
//    }
}