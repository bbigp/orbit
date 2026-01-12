package cn.coolbet.orbit.common

val Int.toBadgeText: String
    get() = if (this > 99) "99+" else this.toString()