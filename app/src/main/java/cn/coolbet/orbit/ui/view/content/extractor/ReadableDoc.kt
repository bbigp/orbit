package cn.coolbet.orbit.ui.view.content.extractor

import com.google.gson.annotations.SerializedName

data class ReadableDoc(
    val metadata: SiteMetadata = SiteMetadata(),
    val extracted: ExtractedContent = ExtractedContent(),
    val url: String,
)

data class SiteMetadata(
    val url: String = "",
    val title: String = "",
    val description: String = "",
    val heroImage: String = "",
    val favicon: String = ""
)

data class ExtractedContent(
    val author: String? = "",
    val content: String = "",
    @SerializedName("date_published") val datePublished: String? = "",
    val domain: String? = "",
    val excerpt: String? = "",
    @SerializedName("lead_image_url") val leadImageUrl: String = "",
    val title: String? = "",
    val url: String? = ""
)
