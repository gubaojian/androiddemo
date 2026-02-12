package com.zhongpin.mvvm_android.ui.order.detail.timeline


class OrderStatusTimelineModel(
        var title: String,
        var date: String,
        var status: Int
){
        companion object {
                val UN_DONE = 0;
                val ACTIVE = 1;
                val DONE  = 2;
        }
}
