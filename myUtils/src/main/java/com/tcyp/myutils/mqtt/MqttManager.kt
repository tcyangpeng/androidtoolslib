package com.tcyp.myutils.mqtt

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.tcyp.myutils.LogUtils
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.concurrent.atomic.AtomicBoolean

/**
 * MQTT管理器类，用于处理MQTT连接、订阅、发布等操作
 */
class MqttManager {
    private val TAG = "MqttManager"

    private val _connectionStateFlow = MutableStateFlow(MqttConnectionState.DISCONNECTED)
    val connectionStateFlow: StateFlow<MqttConnectionState> = _connectionStateFlow.asStateFlow()

    private var mqttClient: MqttAndroidClient? = null
    private var options: MqttConnectOptions? = null

    private val isMqttConnected = AtomicBoolean(false)
    private val isMqttConnecting = AtomicBoolean(false)

    private var mqtt_server_url: String = ""
    private var mqtt_client_id: String = ""
    private var keep_alive_interval: Int = 60
    private var connection_timeout: Int = 30

    //重连相关
    private var reconnect_delay_interval: Int = 10
    private val reconnect_attempts_max: Int = 10 //重连次数
    private var reconnect_attempts_count: Int = 0 //当前重连次数

    // 收到消息时发送的 SharedFlow（热流，可被多个地方收集）
    private val _messageFlow = MutableSharedFlow<MqttMsgEvent>(
        extraBufferCapacity = 100, // 防止背压
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val messageFlow: SharedFlow<MqttMsgEvent> = _messageFlow.asSharedFlow()

    var connectListener: ((MqttConnectionState) -> Unit)? = null

    /**
     * 设置MQTT连接配置参数
     *
     * @param mqtt_server_url MQTT服务器地址
     * @param mqtt_client_id 客户端ID
     * @param keep_alive_interval 心跳间隔（秒）
     * @param connection_timeout 连接超时时间（秒）
     * @param reconnect_delay_interval 重连延迟间隔（秒）
     */
    private fun setConfig(mqtt_server_url: String,
                          mqtt_client_id: String,
                          keep_alive_interval: Int,
                          connection_timeout: Int,
                          reconnect_delay_interval: Int) {
        this.mqtt_server_url = mqtt_server_url
        this.mqtt_client_id = mqtt_client_id
        this.keep_alive_interval = keep_alive_interval
        this.connection_timeout = connection_timeout
        this.reconnect_delay_interval = reconnect_delay_interval
    }

    /**
     * 初始化并开始尝试连接
     *
     * @param context Android上下文
     * @param connectListener 连接状态监听器
     */
    fun initAndConnect(context: Context, connectListener: ((MqttConnectionState) -> Unit)?) {
        if (mqttClient?.isConnected == true) return
        this.connectListener = connectListener
        mqttClient = MqttAndroidClient(context, mqtt_server_url, mqtt_client_id).apply {
            setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    isMqttConnecting.set(false)
                    isMqttConnected.set(true)
                    //重置重连次数
                    reconnect_attempts_count = 0
                    LogUtils.d(TAG, "MQTT 连接成功: $serverURI (重连:$reconnect)")
                    connectListener?.invoke(MqttConnectionState.CONNECTED)
                    _connectionStateFlow.value = MqttConnectionState.CONNECTED
                }

                override fun connectionLost(cause: Throwable?) {
                    isMqttConnected.set(false)
                    LogUtils.d(TAG, "MQTT 断开连接: $cause")
                    connectListener?.invoke(MqttConnectionState.DISCONNECTED)
                    _connectionStateFlow.value = MqttConnectionState.DISCONNECTED
                    //尝试再次连接
                    scheduleConnect()
                }

                override fun messageArrived(
                    topic: String?,
                    message: MqttMessage?
                ) {
                    //收到消息，再次分发
                    LogUtils.d(TAG, "MQTT 收到消息: $topic $message")
                    if (topic?.isEmpty() ==  true || message?.payload?.isEmpty() == true) {
                        return
                    }
                    try {
                        val payload = message?.payload?.toString(Charsets.UTF_8)
                        val mqttMsgEvent = MqttMsgEvent(topic!!, payload!!)
                        _messageFlow.tryEmit(mqttMsgEvent)
                    } catch (e: Exception) {
                        LogUtils.e(TAG, "MQTT 解析消息失败: $e")
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    //发布完成回调
                }

            })
        }

        options = MqttConnectOptions().apply {
            isCleanSession = false
            keepAliveInterval = keep_alive_interval
            connectionTimeout = connection_timeout
        }

        connect()
    }

    /**
     * 建立MQTT连接
     */
    fun connect() {
        if (isMqttConnecting.get() || isMqttConnected.get()) return
        isMqttConnecting.set(true)
        connectListener?.invoke(MqttConnectionState.CONNECTING)

        try {
            mqttClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    //连接成功回调已在callback中处理
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    isMqttConnecting.set(false)
                    LogUtils.d(TAG, "MQTT 连接失败: $exception")
                    connectListener?.invoke(MqttConnectionState.ERROR)
                    _connectionStateFlow.value = MqttConnectionState.ERROR
                    scheduleConnect()
                }

            })
        } catch (e: Exception) {
            LogUtils.e(TAG, "MQTT 连接失败: $e")
            isMqttConnecting.set(false)
        }
    }

    /**
     * 断开MQTT连接
     */
    fun disConnect() {
        if (mqttClient?.isConnected == false) return
        try {
            mqttClient?.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    isMqttConnecting.set(false)
                    isMqttConnected.set(false)
                    LogUtils.d(TAG, "MQTT 手动断开连接成功")
                    _connectionStateFlow.value = MqttConnectionState.DISCONNECTED
                    connectListener?.invoke(MqttConnectionState.DISCONNECTED)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    LogUtils.d(TAG, "MQTT 手动断开连接失败: $exception")
                }

            })
        } catch (e: Exception) {
            LogUtils.e(TAG, "MQTT 断开连接失败: $e")
        }
    }

    /**
     * 安排重连任务
     * 当连接失败或断开时，按照配置的延迟时间进行重连尝试
     */
    private fun scheduleConnect() {
        if (reconnect_attempts_count >= reconnect_attempts_max) {
            LogUtils.d(TAG, "MQTT 重连次数超过限制，停止重连")
            return
        }
        reconnect_attempts_count ++
        connectListener?.invoke(MqttConnectionState.RECONNECTING)
        _connectionStateFlow.value = MqttConnectionState.RECONNECTING

        LogUtils.d(TAG, "将在 ${reconnect_delay_interval}ms 后尝试第 $reconnect_attempts_count 次重连")
        mqttClient?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                LogUtils.d(TAG, "开始第 $reconnect_attempts_count 次重连")
                connect()
            }, reconnect_delay_interval.toLong())
        }
    }

    /**
     * 订阅MQTT主题
     *
     * @param topic 要订阅的主题
     * @param qos QoS级别，默认为2
     */
    fun subscribe(topic: String, qos: Int = 2) {
        if (mqttClient?.isConnected == false) return
        try {
            mqttClient?.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    LogUtils.d(TAG, "MQTT 订阅成功: $topic")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    LogUtils.e(TAG, "MQTT 订阅失败: $topic")
                }
            })
        } catch (e: Exception) {
            LogUtils.e(TAG, "MQTT 订阅异常: $topic ${e.message}")
        }
    }

    /**
     * 发布MQTT消息
     *
     * @param topic 目标主题
     * @param payload 消息内容
     * @param qos QoS级别，默认为2
     * @param retained 是否保留消息，默认为false
     */
    fun publish(topic: String, payload: String,  qos: Int = 2, retained: Boolean = false) {
        if (mqttClient?.isConnected == false) return
        try {
            val msg = payload.toByteArray()
            val mqttMsg = MqttMessage(msg).apply {
                //设置QOS级别
                this.qos = qos
                this.isRetained = retained
            }


            mqttClient?.publish(topic, mqttMsg, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    LogUtils.d(TAG, "MQTT 发送成功: $topic $payload")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    LogUtils.e(TAG, "MQTT 发送失败: $topic $payload")
                }
            })
        } catch (e: Exception) {
            LogUtils.e(TAG, "MQTT 发送失败: $topic ${e.message}")
        }
    }

    /**
     * 检查MQTT是否已连接
     *
     * @return true表示已连接，false表示未连接
     */
    fun isMqttConnected(): Boolean = isMqttConnected.get()

    /**
     * 销毁MQTT管理器，释放资源
     */
    fun destroy() {
        disConnect()
        mqttClient?.close()
        mqttClient = null
        options = null
        isMqttConnected.set(false)
        isMqttConnecting.set(false)
        reconnect_attempts_count = 0
    }

    /**
     * 获取当前连接状态
     */
    val connectionState: MqttConnectionState
        get() = when {
            isMqttConnected.get() -> MqttConnectionState.CONNECTED
            isMqttConnecting.get() -> MqttConnectionState.CONNECTING
            reconnect_attempts_count > 0 -> MqttConnectionState.RECONNECTING
            else -> MqttConnectionState.DISCONNECTED
        }

    /**
     * 检查是否真正连接（已连接且不在连接中）
     *
     * @return true表示真正连接，false表示未真正连接
     */
    fun isReallyConnected(): Boolean = isMqttConnected.get() && !isMqttConnecting.get()
}