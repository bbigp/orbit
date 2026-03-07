package cn.coolbet.orbit.ui.view.listdetail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDetailConfig(
    val showSearch: Boolean = true,
    val enableSwipe: Boolean = true,
) : Parcelable
