package cn.coolbet.orbit.ui.view.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.home.HomeScreen

object LoginScreen: Screen {
    private fun readResolve(): Any = LoginScreen

    @Composable
    override fun Content() {
        val model = getScreenModel<LoginScreenModel>()
        val state by model.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            model.events.collect { it ->
                when(it) {
                    is LoginEvent.NavigateToHome -> {
                        navigator.replaceAll(HomeScreen)
                    }
                    is LoginEvent.ShowError -> {
                        println(it.message)
                    }
                }
            }
        }

        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("By continuing, you agree to our", maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R13B25)
                    Text("Terms of Use and Privacy Policy", maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R13B25)
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 200.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        "Get Started",
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        style = AppTypography.M28,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Enter Server Address and API Key",
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = AppTypography.R15B50,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ObTextField(
                        hint = "Server Address",
                        sizes = ObTextFieldDefaults.large,
                        value = state.baseURL,
                        onValueChange = { newValue ->
                            model.changeState(baseURL = newValue)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ObTextField(
                        hint = "API Key",
                        sizes = ObTextFieldDefaults.large,
                        value = state.authToken,
                        onValueChange = { newValue ->
                            model.changeState(authToken = newValue)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ObAsyncTextButton(
                        "Continue",
                        isLoading = state.isLoading,
                        sizes = OButtonDefaults.large,
                        onClick = { model.login() },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Login() {
}