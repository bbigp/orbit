package cn.coolbet.orbit.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import cn.coolbet.orbit.model.domain.User
import com.google.gson.Gson

class Preference(
    context: Context,
    private val gson: Gson,
){

    val USER_KEY = "current_user_data"

//    private val sharedPrefs = context.getSharedPreferences(
//        "app_prefs", Context.MODE_PRIVATE,
//    )

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "user_prefs", Context.MODE_PRIVATE,
    )

    @SuppressLint("CheckResult")
    fun userProfile(): User {
        val userJson = sharedPreferences.getString(USER_KEY, "")
        if (userJson == "") return User.EMPTY
        return gson.fromJson(userJson, User::class.java)
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().apply {
            putString(USER_KEY, userJson)
            commit()
        }
    }

    fun userSetting(): User {
        val user = userProfile()
        val newUser = user.copy()
        saveUser(newUser)
        return newUser
    }

    fun deleteUser() {
        sharedPreferences.edit().apply{
            remove(USER_KEY)
            commit()
        }
    }
}