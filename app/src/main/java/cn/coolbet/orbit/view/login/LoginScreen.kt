package cn.coolbet.orbit.view.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.ui.theme.AppTypography

object LoginScreen: Screen {
    private fun readResolve(): Any = LoginScreen

    @Composable
    override fun Content() {
    }
}

@Preview(showBackground = true)
@Composable
fun Login() {
    Scaffold { paddingValues ->
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
                    "Enter token and server address",
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = AppTypography.R15B50,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                //服务器地址
                Spacer(modifier = Modifier.height(16.dp))
                //token
                //16
                //button
            }
        }
    }
}