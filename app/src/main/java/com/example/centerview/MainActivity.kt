package com.example.centerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.centerview.databinding.MainBinding
import com.longbridge.common.uiLib.chart.minutes.MinutesChart
import com.longbridge.core.comm.FApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FApp.application = application
        enableEdgeToEdge()
        val binding = MainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val points: MutableList<KLinePoint> = mutableListOf();
        for (i in 0 until 100) {
            val price = ((Math.random()*1000).toInt()).toString()
            points.add(KLinePoint(price = price))
        }
        val proxy = OptionDateMinutesDrawProxy(this)
        binding.minuteCharts.setDrawProxy(proxy)
        proxy.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts)
        binding.minuteCharts.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        proxy.initData(points)

        val proxy2 = OptionPriceTrendMinutesDrawProxy(this)
        binding.minuteCharts2.setDrawProxy(proxy2)
        proxy2.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts2) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts2)
        binding.minuteCharts2.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts2.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        proxy2.initData(points)



    }
}

