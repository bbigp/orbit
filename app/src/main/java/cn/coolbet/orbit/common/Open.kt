package cn.coolbet.orbit.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri


fun openURL(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun copyText(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboardManager.setPrimaryClip(clip)
}

fun shareText(context: Context, text: String, subject: String? = null) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        subject?.let {
            putExtra(Intent.EXTRA_SUBJECT, it)
        }
    }
    val chooserIntent = Intent.createChooser(shareIntent, "分享到...")
    context.startActivity(chooserIntent)
}