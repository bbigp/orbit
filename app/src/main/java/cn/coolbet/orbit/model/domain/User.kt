package cn.coolbet.orbit.model.domain

import androidx.annotation.DrawableRes
import cn.coolbet.orbit.R
import com.google.gson.annotations.SerializedName


data class User(
    val id: Long,
    val username: String = "",
    val authToken: String,
    val baseURL: String,
) {

    val isEmpty: Boolean get() = id == 0L
    val isNotEmpty: Boolean get() = id != 0L
    companion object {
        val EMPTY = User(id = 0, authToken = "", baseURL = "")
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