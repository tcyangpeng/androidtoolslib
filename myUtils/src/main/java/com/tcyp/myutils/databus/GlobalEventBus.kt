package com.tcyp.myutils.databus

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.ConcurrentHashMap

 /**
 * 全局事件总线
  * EventBus.post(LoginEvent(true))
  * EventBus.postSticky(DroneConnectEvent(true))
  * 使用：
  *         viewModelScope.launch {
  *             EventBus.observe(LoginEvent::class.java)
  *                 .collect { event ->
  *                     // 处理事件
  *                     println("登录状态：${event.isLogin}")
  *                 }
  *         }
  */

object GlobalEventBus {
    /**
     * 非粘性事件
     */
    private val eventFlows =
        ConcurrentHashMap<Class<*>, MutableSharedFlow<Any>>()

    /**
     * 粘性事件（保存最后一次事件）
     */
    private val stickyEvents =
        ConcurrentHashMap<Class<*>, Any>()

    /**
     * 发送普通事件（不缓存）
     */
    fun <T : Any> post(event: T) {
        val flow = eventFlows.getOrPut(event::class.java) {
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1
            )
        }
        flow.tryEmit(event)
    }

    /**
     * 发送粘性事件（会缓存）
     */
    fun <T : Any> postSticky(event: T) {
        stickyEvents[event::class.java] = event
        post(event)
    }

    fun <T : Any> observe(eventClass: Class<T>): Flow<T> {
        val flow = eventFlows.getOrPut(eventClass) {
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1
            )
        }
        return flow.mapNotNull {
            eventClass.cast(it)
        }
    }

    fun <T : Any> observeSticky(eventClass: Class<T>): Flow<T> {
        val normalFlow = observe(eventClass)
        val sticky = stickyEvents[eventClass]

        return if (sticky != null) {
            normalFlow.onStart {
                emit(eventClass.cast(sticky))
            }
        } else {
            normalFlow
        }
    }
}