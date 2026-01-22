package cn.coolbet.orbit.ui.view.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.remote.miniflux.MiniLoginApi
import cn.coolbet.orbit.remote.miniflux.to
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class LoginScreenModel(
    retrofit: Retrofit,
    private val session: Session,
): ScreenModel {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    private val loginApi = retrofit.create(MiniLoginApi::class.java)

    fun changeState(baseURL: String? = null, authToken: String? = null) {
        _state.update { it ->
            it.copy(
                baseURL = baseURL ?: it.baseURL,
                authToken = authToken ?: it.authToken,
            )
        }
    }

    fun login() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = _state.value
            try {
                delay(2000)
                var baseURL = currentState.baseURL
                baseURL = if (baseURL.endsWith("/")) baseURL else "$baseURL/"
                val authToken = currentState.authToken
                val user = loginApi.me(baseURL + "v1/me", authToken)
                    .to(baseURL, authToken)
                session.startSession(user)
                NavigatorBus.replaceAll(Route.Home)
            } catch (e: Exception) {

            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class LoginState (
    val baseURL: String = "https://feedo.coolbet.cn/",
    val authToken: String = "lOEQiLk-6QtDmiIz9_AsoBmZrdeKBarjZyjTLyo4600=",
    val isLoading: Boolean = false,
)