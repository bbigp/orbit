package cn.coolbet.orbit.view.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.dao.UserMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginScreenModel @Inject constructor(
//    private val profileApi: ProfileApi,
    private val userMapper: UserMapper,
): ScreenModel {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun changeState(serverAddress: String? = null, apiKey: String? = null) {
        _state.update { it ->
            it.copy(
                serverAddress = serverAddress ?: it.serverAddress,
                apiKey = apiKey ?: it.apiKey,
            )
        }
    }

    fun login() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = _state.value
            try {
                delay(2000)
//                val resp = profileApi.me(currentState.serverAddress + "v1/me", currentState.apiKey)
//                userMapper.saveUser(resp.to(currentState.serverAddress, currentState.apiKey))
            } catch (e: Exception) {

            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class LoginState (
    val serverAddress: String = "https://feedo.coolbet.cn/",
    val apiKey: String = "lOEQiLk-6QtDmiIz9_AsoBmZrdeKBarjZyjTLyo4600=",
    val isLoading: Boolean = false,
)