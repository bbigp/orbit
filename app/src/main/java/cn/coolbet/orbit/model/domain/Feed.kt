package cn.coolbet.orbit.model.domain

import cn.coolbet.orbit.model.OrderPublishedAt

data class Feed(
    val id: Long,
    val userId: Long = 0,
    val feedURL: String = "",
    override val siteURL: String = "",
    override val title: String = "",
    val errorCount: Int = 0,
    val errorMsg: String = "",
    val folderId: Long = 0,
    val desc: String = "",

    override val hideGlobally: Boolean = false,
    override val onlyShowUnread: Boolean = false,
    override val order: String = OrderPublishedAt,
    val iconURL: String = "",

    val folder: Folder = Folder.EMPTY,
) : Meta {

    // 在 Kotlin 中可以作为 companion object 的常量或 object 实例
    companion object {
        val EMPTY = Feed(id = 0L)
    }


    // @override String get metaId => "e$id";
    override val metaId: String
        get() = "e$id"

    // @override String get url => feedUrl;
    override val url: String
        get() = feedURL

    // List<String> get statuses => onlyShowUnread ? [EntryStatus.unread.name] : [EntryStatus.unread.name, EntryStatus.read.name];
    val statuses: List<String>
        get() = if (onlyShowUnread) {
            listOf(EntryStatus.UNREAD.valaue)
        } else {
            listOf(EntryStatus.UNREAD.valaue, EntryStatus.READ.valaue)
        }

    /**
     * 对应 Dart 中的 contextMenus 方法，返回一个 List<ContextMenuEntry>
     * 注意：在 Kotlin/Compose 中，通常不传递 BuildContext。
     * 而是使用 Lambda 或在 Composable 中处理导航。
     */
//    fun contextMenus(): List<ContextMenuEntry> {
//        // Get.toNamed 替换为 Kotlin 中相应的导航调用
//        // 假设 Get.toNamed 在 Kotlin 中可以访问或替换为 Compose 导航
//        val onEditClick = {
//            // 假设 Get.toNamed 对应于 Router.navigate 或类似操作
//            // Get.toNamed(RouteConfig.addFeed, arguments = this)
//            println("Navigating to ${RouteConfig.ADD_FEED} for feed ID $id")
//        }
//
//        val onUnsubscribeClick = {
//            // 在 Kotlin 中处理取消订阅的逻辑，可能弹出 Compose Dialog
//            println("Attempting to unsubscribe feed ID $id")
//        }
//
//        return listOf(
//            ContextMenu(
//                label = "编辑",
//                icon = SvgIcons.EDIT, // 假设 SvgIcons.edit 对应 SvgIcons.EDIT
//                onTap = onEditClick
//            ),
//            ContextMenu.Divider, // 假设 ContextMenuDivider 对应 ContextMenu.Divider
//            ContextMenu(
//                label = "取消订阅",
//                icon = SvgIcons.REDUCE_O, // 假设 SvgIcons.reduce_o 对应 SvgIcons.REDUCE_O
//                type = ContextMenuType.DANGER, // 假设 ContextMenuType.danger 对应 ContextMenuType.DANGER
//                onTap = onUnsubscribeClick
//            ),
//        )
//    }
}



