package com.example.centerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.longbridge.market.mvp.ui.widget.option.strategy.guide.proxy.KLinePoint
import com.longbridge.market.mvp.ui.widget.option.strategy.guide.proxy.OptionPriceDrawProxy
import com.example.centerview.databinding.MainBinding
import com.longbridge.common.uiLib.chart.minutes.MinutesChart
import com.longbridge.core.comm.FApp
import java.util.Locale

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
        val proxy = OptionPriceDrawProxy(this)
        proxy.drawScene = "choose_date"
        binding.minuteCharts.setDrawProxy(proxy)
        proxy.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts)
        binding.minuteCharts.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy.initData(points)

        val lastPrice = points[points.size -1].price.toFloat()

        val proxyPriceDown = OptionPriceDrawProxy(this)
        proxyPriceDown.drawScene = "two_price_move_with_in"
        proxyPriceDown.targetPrice = lastPrice  + 100
        proxyPriceDown.targetTrendPrice = String.format(Locale.getDefault(), "%.2f", proxyPriceDown.targetPrice)

        proxyPriceDown.targetPrice2 = lastPrice  + 500
        proxyPriceDown.targetTrendPrice2 = String.format(Locale.getDefault(), "%.2f", proxyPriceDown.targetPrice2)
        binding.minuteCharts1.setDrawProxy(proxyPriceDown)
        proxyPriceDown.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts1) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts1)
        binding.minuteCharts1.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts1.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxyPriceDown.initData(points)





        val proxy2 = OptionPriceDrawProxy(this)
        proxy2.drawScene = "single_price_trend"
        proxy2.targetTrendPrice = "1200.0"
        proxy2.targetPrice = 1200.0f
        binding.minuteCharts2.setDrawProxy(proxy2)
        proxy2.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts2) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts2)
        binding.minuteCharts2.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts2.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        proxy2.initData(points)

        val proxy7 = OptionPriceDrawProxy(this)
        proxy7.drawScene = "single_price_trend"
        proxy7.targetTrendPrice = "100.0"
        proxy7.targetPrice = 100.0f
        binding.minuteCharts7.setDrawProxy(proxy7)
        proxy7.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts7) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts7)
        binding.minuteCharts7.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts7.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy7.initData(points)



        val proxy3 = OptionPriceDrawProxy(this)
        proxy3.drawScene = "connect_options_premium_not_exceed"
        proxy3.targetTrendPrice = "800.0"
        proxy3.targetPrice = 800.0f
        binding.minuteCharts3.setDrawProxy(proxy3)
        proxy3.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts3) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts3)
        binding.minuteCharts3.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts3.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy3.initData(points)

        val proxy6 = OptionPriceDrawProxy(this)
        proxy6.drawScene = "connect_options_premium_not_fall_below"
        proxy6.targetTrendPrice = "100.0"
        proxy6.targetPrice = 100.0f
        binding.minuteCharts6.setDrawProxy(proxy6)
        proxy6.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts6) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts6)
        binding.minuteCharts6.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts6.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy6.initData(points)


        val proxy4 = OptionPriceDrawProxy(this)
        proxy4.drawScene = "two_price_move_at_least"
        proxy4.targetPrice = lastPrice  - 300
        proxy4.targetTrendPrice = String.format(Locale.getDefault(), "%.2f", proxy4.targetPrice)

        proxy4.targetPrice2 = lastPrice  + 300
        proxy4.targetTrendPrice2 = String.format(Locale.getDefault(), "%.2f", proxy4.targetPrice2)

        binding.minuteCharts4.setDrawProxy(proxy4)
        proxy4.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts4) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts4)
        binding.minuteCharts4.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts4.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy4.initData(points)

        val proxy5 = OptionPriceDrawProxy(this)
        proxy5.drawScene = "two_price_move_with_in"
        proxy5.targetPrice = lastPrice  - 300
        proxy5.targetTrendPrice = String.format(Locale.getDefault(), "%.2f", proxy4.targetPrice)

        proxy5.targetPrice2 = lastPrice + 300
        proxy5.targetTrendPrice2 = String.format(Locale.getDefault(), "%.2f", proxy4.targetPrice2)

        binding.minuteCharts5.setDrawProxy(proxy5)
        proxy5.setDataObserver(object : MinutesChart.DefaultDataObserver(binding.minuteCharts5) {
            override fun onDataChange() {
                doOnDataChange()
            }
        }, binding.minuteCharts5)
        binding.minuteCharts5.measure(MeasureSpec.AT_MOST,MeasureSpec.EXACTLY)
        binding.minuteCharts5.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        proxy5.initData(points)








    }
}

