package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.model.domain.Entry

data class AddFeedPreview(
    val title: String,
    val url: String,
    val iconUrl: String,
    val entries: List<Entry> = emptyList(),
)

data class AddFeedResultUnit(
    val previews: List<AddFeedPreview> = emptyList(),
    val error: String? = null,
)

@Stable
class AddFeedState {
    var isFetchingPreview by mutableStateOf(false)

    fun setLoading(v: Boolean) {
        isFetchingPreview = v
    }
}


//业务数据：放一个 state（例如 StateFlow），由 Model 管理
//页面状态：放一个 state（loading/错误/空态等），可在 Model 或 UI
//纯交互数据：放 remember/rememberSaveable
