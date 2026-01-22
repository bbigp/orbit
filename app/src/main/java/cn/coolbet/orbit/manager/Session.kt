package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Session(
    private val preference: Preference,
    private val store: CacheStore,
) {

    private val _state = MutableStateFlow(User.EMPTY)
    val state: StateFlow<User> = _state.asStateFlow()

    val user: User get() = _state.value
    val authToken: String get() = _state.value.authToken
    val baseURL: String get() = _state.value.baseURL

    fun refreshSession(){
        _state.value = preference.userProfile()
    }

    fun startSession(user: User? = null) {
        if (user == null) {
            _state.value = preference.userProfile()
        } else {
            user.let {
                preference.saveUser(it)
                _state.value = it
            }
        }
        if (_state.value.isEmpty) {
            return
        }
        Log.d("SessionManager", "会话启动，Retrofit 使用持久化 URL。")
        store.loadInitialData(_state.value.id)
    }

    fun endSession() {
        // 1. 清除持久化数据
        preference.deleteUser()

        store.clearCache()
        Log.d("SessionManager", "会话已销毁，旧 URL 已清除。")
    }

}