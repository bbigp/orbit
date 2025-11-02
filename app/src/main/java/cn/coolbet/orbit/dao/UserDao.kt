package cn.coolbet.orbit.dao

import android.content.Context
import cn.coolbet.orbit.model.domain.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.annotation.Signed
import javax.inject.Inject

@Signed
class UserDao @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
){

    val sharedPreferences = context.getSharedPreferences(
        "user_prefs", Context.MODE_PRIVATE,
    )
    private val USER_KEY = "current_user_data"


    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().apply {
            putString(USER_KEY, userJson)
            commit()
        }
    }



}