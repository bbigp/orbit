package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.di.SessionComponent
import cn.coolbet.orbit.remote.SessionAwareIconApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val factory: SessionComponent.Factory,
    private val preferenceManager: PreferenceManager,
    private val awareIconApi: SessionAwareIconApi,
){
    private var _sessionComponent: SessionComponent? = null
    val sessionComponent: SessionComponent? get() = _sessionComponent

    // 💥 检查当前是否处于登录状态（是否有 URL）
    fun isSessionActive(): Boolean {
        return preferenceManager.getBaseUrl().isNotEmpty() && _sessionComponent != null
    }


    /**
     * 【App 启动时 / 登录成功后】调用
     * 确保会话组件已创建，使用持久化的 URL。
     */
    fun startSession(forceUrl: String? = null) {
        // 如果提供了 URL (登录时)，先持久化
        forceUrl?.let { preferenceManager.setBaseUrl(it) }

        // 如果持久化的 URL 存在，并且组件尚未创建，则创建它
        if (preferenceManager.getBaseUrl().isNotEmpty() && _sessionComponent == null) {
            _sessionComponent = factory.create()
            awareIconApi.set(this.sessionComponent)
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