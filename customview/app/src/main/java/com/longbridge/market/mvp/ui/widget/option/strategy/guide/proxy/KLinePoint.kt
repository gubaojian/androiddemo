package com.longbridge.market.mvp.ui.widget.option.strategy.guide.proxy

data class KLinePoint(
    val price:String,
    var isVirtualPoint: Boolean = false,
    var timestamp: String? = null,
    var daysTime: String? = null
)