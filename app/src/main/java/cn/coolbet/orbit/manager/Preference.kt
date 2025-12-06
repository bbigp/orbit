package cn.coolbet.orbit.manager

import android.annotation.SuppressLint
import android.content.Context
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.model.domain.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preference @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
){

    val USER_KEY = "current_user_data"

//    private val sharedPrefs = context.getSharedPreferences(
//        "app_prefs", Context.MODE_PRIVATE,
//    )

    val sharedPreferences = context.getSharedPreferences(
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

    fun userSetting(unreadMark: UnreadMark? = null, autoRead: Boolean? = null,
                    openContent: OpenContentWith? = null, rootFolderId: Long? = null,
                    autoReaderView: Boolean? = null,
    ): User {
        val user = userProfile()
        val newUser = user.copy(
            unreadMark = unreadMark ?: user.unreadMark,
            autoRead = autoRead ?: user.autoRead,
            openContent = openContent ?: user.openContent,
            rootFolder = rootFolderId ?: user.rootFolder,
            autoReaderView = autoReaderView ?: user.autoReaderView,
        )
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