package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.MemoryStore
import cn.coolbet.orbit.di.SessionComponent
import cn.coolbet.orbit.di.SessionEntryPoint
import cn.coolbet.orbit.model.domain.User
import cn.coolbet.orbit.remote.SessionAwareIconApi
import dagger.hilt.EntryPoints
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionComponentBuilder: SessionComponent.Builder,
    private val preferenceManager: PreferenceManager,
    private val awareIconApi: SessionAwareIconApi,
    private val store: MemoryStore,
){
    private var _sessionComponent: SessionComponent? = null
    val sessionComponent: SessionComponent? get() = _sessionComponent

    // 💥 检查当前是否处于登录状态（是否有 URL）
    fun isSessionActive(): Boolean {
        return preferenceManager.userProfile().isNotEmpty && _sessionComponent != null
    }


    /**
     * 【App 启动时 / 登录成功后】调用
     * 确保会话组件已创建，使用持久化的 URL。
     */
    fun startSession(user: User? = null) {
        user?.let { preferenceManager.saveUser(it) }

        // 如果持久化的 URL 存在，并且组件尚未创建，则创建它
        if (preferenceManager.userProfile().isNotEmpty && _sessionComponent == null) {
            _sessionComponent = sessionComponentBuilder.build()
            awareIconApi.set(EntryPoints.get(
                sessionComponent!!,
                SessionEntryPoint::class.java
            ).minIconFileApi())
            store.loadInitialData()
            Log.d("SessionManager", "会话启动，Retrofit 使用持久化 URL。")
        }
    }

    /**
     * 【退出登录时】调用
     */
    fun endSession() {
        // 1. 清除持久化数据
        preferenceManager.clearSessionData()
        awareIconApi.clear()

        // 2. 销毁 Retrofit 实例和所有会话级依赖
        _sessionComponent = null
        Log.d("SessionManager", "会话已销毁，旧 URL 已清除。")
    }

}