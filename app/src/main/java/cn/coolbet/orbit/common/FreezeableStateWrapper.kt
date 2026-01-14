package cn.coolbet.orbit.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

/**
 * 一个支持“视觉锁定”的状态持有器
 * 当调用 [freeze] 后，暴露给 UI 的 [state] 将不再更新，直到调用 [unfreeze]
 */
class FreezeableStateWrapper<T>(
    private val scope: CoroutineScope,
    initialValue: T
) {
    // 真实的数据源，始终反映最新状态
    private val _internalState = MutableStateFlow(initialValue)
    val value: T get() = _internalState.value

    // 锁定信号
    private val isFrozen = MutableStateFlow(false)

    // 暴露给 UI 的流
    val state: StateFlow<T> = _internalState
        .combine(isFrozen) { value, frozen -> value to frozen }
        .scan(initialValue) { lastEmitted, (currentReal, frozen) ->
            // 核心逻辑：锁定期间，拦截所有新值，只复读最后一次发出的旧值
            if (frozen) lastEmitted else currentReal
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = initialValue
        )

    /**
     * 更新状态（正常业务逻辑使用）
     */
    fun update(action: (T) -> T) {
        _internalState.update(action)
    }

    /**
     * 更新状态（直接赋值）
     */
    fun setValue(value: T) {
        _internalState.value = value
    }

    fun freeze() {
        isFrozen.value = true
    }

    /**
     * 解锁状态更新
     * 建议在 A 页面显示时调用
     */
    fun unfreeze() {
        isFrozen.value = false
    }
}

//// 1. 定义你的状态管理类
//class ListDetailManager(scope: CoroutineScope) {
//    // 使用刚才封装的泛型类
//    val holder = FreezeableStateHolder(scope, ListDetailState())
//
//    // 业务方法
//    fun loadBData() = holder.update { it.copy(...) }
//}
//
//// 2. 在 B 页面回退时调用
//BackHandler {
//    // 锁定并恢复到 A 的快照
//    listDetailManager.holder.freezeAndRestore(snapshotOfA)
//    navigator.pop()
//}
//
//// 3. 在 A 页面显示时调用
//LaunchedEffect(Unit) {
//    listDetailManager.holder.unfreeze()
//}