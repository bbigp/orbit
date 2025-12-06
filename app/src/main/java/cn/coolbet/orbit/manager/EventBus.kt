package cn.coolbet.orbit.manager

import cn.coolbet.orbit.model.domain.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventBus @Inject constructor() {

    private val _events = MutableSharedFlow<Evt>(replay = 1, extraBufferCapacity = 0)
    val events: SharedFlow<Evt> = _events.asSharedFlow()

    suspend fun emitEvent(event: Evt) {
        _events.emit(event)
    }

    fun post(event: Evt): Boolean {
        return _events.tryEmit(event)
    }

    suspend inline fun <reified T : Evt> subscribe(
        crossinline action: (T) -> Unit
    ) {
        this.events.filterIsInstance<T>()
            .collect { event ->
                action(event)
            }
    }

    inline fun <reified T : Evt> subscribe(
        scope: CoroutineScope,
        crossinline action: (T) -> Unit
    ) {
        scope.launch {
            this@EventBus.events.filterIsInstance<T>()
                .collect { event ->
                    action(event)
                }
        }
    }
}

sealed class Evt {
    data class CacheInvalidated(val userId: Long): Evt()
    data class EntryUpdated(val entry: Entry): Evt()
}