//package cn.coolbet.orbit.di
//
//import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
//import dagger.Component
//import dagger.hilt.DefineComponent
//import dagger.hilt.EntryPoint
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Scope
//
//@Scope // 告诉 Dagger/Hilt 这是一个自定义作用域
//@Retention(AnnotationRetention.RUNTIME)
//annotation class SessionScope
//
//
//@SessionScope
//@DefineComponent(parent = SingletonComponent::class)
//interface SessionComponent {
//
//    @DefineComponent.Builder
//    interface Builder {
//        fun build(): SessionComponent
//    }
//
//}
//
//@EntryPoint
//@InstallIn(SessionComponent::class)
//interface SessionEntryPoint {
//
//    // 现在这个方法可以暴露你的 @SessionScope 实例了
//    fun minIconFileApi(): MinIconFileApi
//
//}