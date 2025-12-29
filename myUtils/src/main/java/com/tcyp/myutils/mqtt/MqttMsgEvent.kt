package com.tcyp.myutils.mqtt

data class MqttMsgEvent(
    val topic: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)