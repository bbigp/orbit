package cn.coolbet.orbit.model.domain

import androidx.room.TypeConverter

enum class ReaderPageState {
    Idle, Success, Failure, Fetching
}

class ReaderPageStateConverters {
    @TypeConverter
    fun fromReaderPageState(value: ReaderPageState): String = value.name

    @TypeConverter
    fun toReaderPageState(value: String): ReaderPageState = ReaderPageState.valueOf(value)
}