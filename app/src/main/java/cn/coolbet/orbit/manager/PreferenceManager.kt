package cn.coolbet.orbit.manager

import android.content.Context
import cn.coolbet.orbit.dao.UserMapper.Companion.PREFS_NAME
import cn.coolbet.orbit.dao.UserMapper.Companion.USER_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
){

    private val sharedPrefs = context.getSharedPreferences(
        "app_prefs", Context.MODE_PRIVATE,
    )


    fun getBaseUrl(): String {
        return sharedPrefs.getString("BASE_URL_KEY", "") ?: ""
    }

    fun setBaseUrl(url: String) {
    }

    fun clearSessionData() {
    }
}