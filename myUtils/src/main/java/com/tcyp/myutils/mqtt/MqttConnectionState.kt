package com.tcyp.myutils.mqtt

enum class MqttConnectionState {
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTED,
    ERROR
}