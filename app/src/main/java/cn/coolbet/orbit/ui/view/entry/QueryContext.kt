package cn.coolbet.orbit.ui.view.entry

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QueryContext(
    val page: String
): Parcelable {
    companion object {
        val normal = QueryContext(page = "normal")
        val search = QueryContext(page = "search")
    }

}

