package com.example.testai

data class KLineData(
    val timestamp: Long,
    val open: Float,
    val close: Float,
    val high: Float,
    val low: Float,
    val volume: Float
) {
    val isUp: Boolean get() = close >= open
}
