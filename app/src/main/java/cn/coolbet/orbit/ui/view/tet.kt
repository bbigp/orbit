package cn.coolbet.orbit.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


@Composable
fun AppLifecycleTracker(syncAction: () -> Unit) {

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = AppForegroundBackgroundObserver(syncAction)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                println("✨ 任务启动了!")
            }
        }
    }
}

class AppForegroundBackgroundObserver(
    private val onAppComesToForeground: () -> Unit
): DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        onAppComesToForeground()
    }

    override fun onStop(owner: LifecycleOwner) {

    }
}