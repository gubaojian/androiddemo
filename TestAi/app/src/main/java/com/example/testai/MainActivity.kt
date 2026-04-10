package com.example.testai

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val kLineView = findViewById<KLineView>(R.id.kLineView)
        kLineView.setData(generateMockData(120))

        val lineChartView = findViewById<LineChartView>(R.id.lineChartView)
        lineChartView.setPoints(listOf(12f, 18f, 9f, 25f, 22f, 30f, 27f, 35f, 28f, 40f))
    }

    /** 生成模拟K线数据（随机游走） */
    private fun generateMockData(count: Int): List<KLineData> {
        val list = mutableListOf<KLineData>()
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -count) }
        var price = 100f
        val rng = Random(42)

        repeat(count) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            // 跳过周末
            while (cal.get(Calendar.DAY_OF_WEEK) in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val open = price
            val change = (rng.nextFloat() - 0.48f) * price * 0.04f
            val close = (open + change).coerceAtLeast(1f)
            val high = maxOf(open, close) + rng.nextFloat() * price * 0.015f
            val low = minOf(open, close) - rng.nextFloat() * price * 0.015f
            val volume = (50_0000f + rng.nextFloat() * 500_0000f)

            list.add(
                KLineData(
                    timestamp = cal.timeInMillis,
                    open = open,
                    close = close,
                    high = high,
                    low = low.coerceAtLeast(0.01f),
                    volume = volume
                )
            )
            price = close
        }
        return list
    }
}
