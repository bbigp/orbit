package cn.coolbet.orbit.dao

import android.annotation.SuppressLint
import android.content.Context
import cn.coolbet.orbit.model.domain.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.annotation.Signed
import javax.inject.Inject

@Signed
class UserMapper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
){
    companion object {
        val USER_KEY = "current_user_data"
        val PREFS_NAME = "user_prefs"
    }

    val sharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE,
    )

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().apply {
            putString(USER_KEY, userJson)
            commit()
        }
    }

    @SuppressLint("CheckResult")
    fun userProfile(): User {
        val userJson = sharedPreferences.getString(USER_KEY, "")
        if (userJson == "") return User.EMPTY
        return gson.fromJson(userJson, User::class.java)
    }

    fun userSetting(autoRead: Boolean = false): User {
        val user = userProfile()
        val newUser = user.copy(autoRead = autoRead)
        saveUser(newUser)
        return newUser
    }


    fun logout() {
        sharedPreferences.edit().apply{
            remove(USER_KEY)
            commit()
        }
    }

}