package cn.coolbet.orbit.manager

import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
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
        crossinline action: suspend (T) -> Unit
    ): EventBus {
        this.events.filterIsInstance<T>()
            .collect { event ->
                action(event)
            }
        return this
    }

    inline fun <reified T : Evt> subscribe(
        scope: CoroutineScope,
        crossinline action: suspend (T) -> Unit
    ): EventBus {
        scope.launch {
            this@EventBus.events.filterIsInstance<T>()
                .collect { event ->
                    action(event)
                }
        }
        return this
    }
}

sealed class Evt {
    data class CacheInvalidated(val userId: Long): Evt()
    data class EntryUpdated(val entry: Entry): Evt()
    data class EntryStatusUpdated(
        val entryId: Long,
        val status: EntryStatus,
        val feedId: Long,
        val folderId: Long
    ): Evt()
}