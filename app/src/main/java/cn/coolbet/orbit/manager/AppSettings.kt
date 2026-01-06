package cn.coolbet.orbit.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.graphics.toColorInt

//val appSettings = remember { AppSettings(context) } // 使用 remember 确保不会在重组时重复创建实例
//var articleBgColor by remember { mutableStateOf(appSettings.articleBgColor) }


class AppSettings(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_settings", Context.MODE_PRIVATE
    )

    val articleBgColor = PreferenceItem(prefs, "article_bg_color", "#FFFFFFFF")
    val articleFontSize = PreferenceItem(prefs, "article_font_size", 15)
    val articleFontFamily = PreferenceItem(prefs, "article_font_family", "DM Sans")
//    var articleBgColor: String by SharedPrefsDelegate(prefs, "", "")

}

var PreferenceItem<String>.colorValue: Color
    get() = Color(value.toColorInt())
    set(color) {
        value = String.format("#%08X", (0xFFFFFFFFL and color.toArgb().toLong()))
    }

@Composable
fun PreferenceItem<String>.asColorState(): androidx.compose.runtime.State<Color> {
    val hexState = this.asState()
    return remember {
        derivedStateOf { Color(hexState.value.toColorInt()) }
    }
}

object Env {
    lateinit var settings: AppSettings

    fun init(context: Context) {
        settings = AppSettings(context)
    }
}

class PreferenceItem<T>(
    private val prefs: SharedPreferences,
    private val key: String,
    private val defaultValue: T,
) {

    private val _state = MutableStateFlow(readFromPrefs()) // 1. 初始化时自动读取旧值
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, string ->
        if (key == string) {
            _state.value = readFromPrefs()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    var value: T
        get() = _state.value
        set(value) {
            _state.value = value // 更新内存（UI会刷新）
            writeToPrefs(value) // 更新磁盘
        }

    @Composable
    fun asState() = _state.collectAsState()

    @Suppress("UNCHECKED_CAST")
    private fun readFromPrefs(): T = when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        else -> defaultValue
    }

    private fun writeToPrefs(value: T) = prefs.edit {
        when (value) {
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
        }
    }

}


class SharedPrefsDelegate<T>(
    private val prefs: SharedPreferences,
    private val key: String,
    private val defaultValue: T
) {

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: Any?): T {
        return when(defaultValue) {
            is String -> prefs.getString(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            else -> defaultValue
        }
    }

    operator fun setValue(thisRef: Any?, property: Any?, value: T) {
        prefs.edit {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
            }
        }
    }

}