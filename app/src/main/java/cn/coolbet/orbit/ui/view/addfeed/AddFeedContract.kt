package cn.coolbet.orbit.ui.view.addfeed

sealed class AddFeedEffect {
    data class Error(val message: String) : AddFeedEffect()
}
