package cn.coolbet.orbit.model.domain

import com.google.gson.annotations.SerializedName


data class User(
    val id: Long,
    val username: String = "",
    val authToken: String,
    val baseURL: String,
    val autoRead: Boolean = false,
    val unreadMark: UnreadMark = UnreadMark.NUMBER,
    val openContent: OpenContentWith = OpenContentWith.READER_VIEW,
    val rootFolder: Long = 1,
) {

    val isEmpty: Boolean get() = id == 0L
    val isNotEmpty: Boolean get() = id != 0L
    companion object {
        val EMPTY = User(id = 0, authToken = "", baseURL = "")
    }

}


enum class UnreadMark(val value: String) {
    @SerializedName("Number") NUMBER("Number"),
    @SerializedName("Dot") DOT("Dot"),
    @SerializedName("None") NONE("None");
    companion object {
        fun fromValue(value: String?): UnreadMark =
            UnreadMark.entries.find { it.value == value } ?: NUMBER
    }
}

enum class OpenContentWith(val value: String) {
    @SerializedName("ReaderView") READER_VIEW("内置阅读器"),
    @SerializedName("InAppBrowser") IN_APP_BROWSER("应用内浏览器"),
    @SerializedName("SystemBrowser") SYSTEM_BROWSER("系统浏览器");
    companion object {
        fun fromValue(value: String?): OpenContentWith =
            OpenContentWith.entries.find { it.value == value } ?: READER_VIEW
    }
}
//val customGson = GsonBuilder()
//    // 注册适配器，让 Gson 知道如何处理 UnreadMark 类型
//    .registerTypeAdapter(UnreadMark::class.java, UnreadMarkAdapter())
//    .create()
//class UnreadMarkAdapter : JsonSerializer<UnreadMark>, JsonDeserializer<UnreadMark> {
//
//    // 序列化：将 UnreadMark 转换为 JSON
//    override fun serialize(
//        src: UnreadMark?,
//        typeOfSrc: Type?,
//        context: JsonSerializationContext?
//    ): JsonElement {
//        // 如果枚举实例不为空，则返回其 value 属性的值
//        return JsonPrimitive(src?.value)
//    }
//
//    // 反序列化：将 JSON 转换为 UnreadMark
//    override fun deserialize(
//        json: JsonElement?,
//        typeOfT: Type?,
//        context: JsonDeserializationContext?
//    ): UnreadMark {
//        val value = json?.asString
//        // 使用你的 fromValue 伴生函数来处理反序列化，这样可以处理空值或未识别的值
//        return UnreadMark.fromValue(value)
//    }
//}