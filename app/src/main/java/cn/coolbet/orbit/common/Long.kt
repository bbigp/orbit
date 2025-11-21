package cn.coolbet.orbit.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// 定义常用的时间格式化器
@RequiresApi(Build.VERSION_CODES.O)
private val TODAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
@RequiresApi(Build.VERSION_CODES.O)
private val THIS_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.getDefault())
@RequiresApi(Build.VERSION_CODES.O)
private val THIS_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.getDefault())
@RequiresApi(Build.VERSION_CODES.O)
private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())

// 定义时区（使用系统默认时区，确保比较逻辑准确）
@RequiresApi(Build.VERSION_CODES.O)
private val ZONE_ID = ZoneId.systemDefault()

// 阈值：2000年1月1日的毫秒时间戳
private const val MIN_VALID_TIMESTAMP_MS = 946684800000L
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toRelativeTime(
    isMilliseconds: Boolean = true
): String {
    if (this == 0L) {
        return ""
    }

    // --- 阶段 2: 校验时间戳是否在合理范围内 ---
    if (this < MIN_VALID_TIMESTAMP_MS) {
        // 如果值小于我们定义的有效阈值 (很可能是 ID 或错误数据)
        return ""
    }

    // 1. 获取目标时间点和当前时间点 (转换为 Instant)
    val targetInstant = if (isMilliseconds) {
        Instant.ofEpochMilli(this)
    } else {
        Instant.ofEpochSecond(this)
    }
    val nowInstant = Instant.now()

    // 2. 计算时间差 (Duration)
    val duration = Duration.between(targetInstant, nowInstant)
    val minutesDifference = duration.toMinutes()

    // 3. 相对时间逻辑判断

    // --- 阶段 1: 几分钟前 ---
    if (minutesDifference in 0L..59L) {
        if (minutesDifference < 1) {
            return "刚刚" // 0 分钟前
        }
        return "${minutesDifference}分钟前"
    }

    // --- 阶段 2: 超过 60 分钟 (需要日期判断) ---

    // 转换为本地时间用于日期比较 (必须带上时区)
    val targetDateTime = targetInstant.atZone(ZONE_ID).toLocalDateTime()
    val nowDateTime = nowInstant.atZone(ZONE_ID).toLocalDateTime()

    // 检查是否是今天
    if (targetDateTime.toLocalDate() == nowDateTime.toLocalDate()) {
        // 当天：只显示小时和分钟 (HH:mm)
        return targetDateTime.format(TODAY_FORMATTER)
    }

    // 检查是否是今年
    if (targetDateTime.year == nowDateTime.year) {
        // 今年 (但不是今天)：显示月日时分 (MM-dd HH:mm)
        // 我们可以简化为只判断年份，因为月份和日期判断在前面被排除
        return targetDateTime.format(THIS_YEAR_FORMATTER)
    }

    // --- 阶段 3: 跨年 (显示完整日期) ---
    // 其他情况：显示年-月-日 时:分 (yyyy-MM-dd HH:mm)
    return targetDateTime.format(DEFAULT_FORMATTER)
}

// 定义时间常量（毫秒）
val ONE_MINUTE = TimeUnit.MINUTES.toMillis(1)
val ONE_HOUR = TimeUnit.HOURS.toMillis(1)
val ONE_DAY = TimeUnit.DAYS.toMillis(1)
val ONE_WEEK = TimeUnit.DAYS.toMillis(7)

/**
 * 将 Long 毫秒时间戳格式化为相对时间字符串。
 * 规则：几分钟前 -> 几小时前 -> 几天前 -> 月日 -> 年月日
 * @return 格式化后的字符串
 */
fun Long.showTime(): String {
    if (this == 0L) {
        return ""
    }
    if (this < MIN_VALID_TIMESTAMP_MS) {
        return ""
    }

    val now = System.currentTimeMillis()
    val diff = now - this // 计算时间差 (毫秒)

    // 如果时间戳在未来或时间差为负，返回绝对时间
    if (diff < 0) {
        return ""
    }

    return when {
        diff < ONE_MINUTE -> "刚刚"
        diff < ONE_HOUR -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "${minutes}分钟前"
        }
        diff < ONE_DAY -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "${hours}小时前"
        }
        diff < ONE_WEEK -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}天前"
        }
        else -> {
            val targetDate = Date(this)

            val targetCalendar = Calendar.getInstance().apply { time = targetDate }
            val currentCalendar = Calendar.getInstance().apply { time = Date(now) }

            val targetYear = targetCalendar.get(Calendar.YEAR)
            val currentYear = currentCalendar.get(Calendar.YEAR)

            return if (targetYear == currentYear) {
                SimpleDateFormat("MM-dd", Locale.getDefault()).format(targetDate)
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(targetDate)
            }
        }
    }
}