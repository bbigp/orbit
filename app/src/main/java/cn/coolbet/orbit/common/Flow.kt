package cn.coolbet.orbit.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


fun MutableStateFlow<Map<String, Int>>.increment(
    key: String,
    increment: Int
) {
    this.update { it ->
        val newMap = it.toMutableMap()
        val currentValue = newMap[key] ?: 0
        val newValue = currentValue + increment.coerceIn(
            minimumValue = 0,
            maximumValue = Int.MAX_VALUE
        )
        newMap[key] = newValue
        newMap
    }
}

fun MutableStateFlow<Map<String, Int>>.increment(
    incrementsMap: Map<String, Int>
) {
    this.update { it ->
        val newMap = it.toMutableMap()
        incrementsMap.forEach { (key, increment) ->
            val currentValue = newMap[key] ?: 0
            val newValue = currentValue + increment.coerceIn(
                minimumValue = 0,
                maximumValue = Int.MAX_VALUE
            )
            newMap[key] = newValue
        }
        newMap
    }
}