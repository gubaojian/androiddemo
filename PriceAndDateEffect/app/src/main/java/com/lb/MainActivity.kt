package com.lb

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.lb.demo.LBGenericScrollView
import com.lb.demo.LBPriceScrollView
import com.lb.price.one.R
import com.lb.util.LogUtils
import com.longbridge.market.mvp.ui.widget.option.strategy.guide.view.OptionDateScrollView
import com.longbridge.mdtrade.dialogs.ModifyOptionChanceDialog


class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "Main_PAD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val genericView = findViewById<LBGenericScrollView>(R.id.generic_view)
        genericView.dataSource = buildOptionModels()
        genericView.initialScrollToIndex = 3

        val priceView = findViewById<LBPriceScrollView>(R.id.price_view)
        priceView.dataSource = buildPriceModels()
        priceView.currentPrice = 103.8f
        priceView.initialScrollToIndex = 5



        initEvent()

        val optionDateScrollView = findViewById<OptionDateScrollView>(R.id.srl_option_date_line)
        optionDateScrollView.dataSource =
            optionDateScrollView.convertDateLine.getFakeTimelineData()

        optionDateScrollView.didSelectItemAtIndex = { idx ->
            LogUtils.i("MainActivity", "date center index = $idx")
            val date = optionDateScrollView.dataSource[idx]
            LogUtils.i("MainActivity", "date = ${date.expireDate?.expire_date}")
        }
    }

    private fun initEvent() {
        findViewById<AppCompatButton>(R.id.btn_option_dialog).setOnClickListener {
            showModifyOptionChanceDialog()
        }

        Log.d(TAG, "initEvent: ")
        Log.i(TAG, "jar jar")


    }

    private fun buildOptionModels(): List<LBGenericScrollViewCellModel> {
        val items = listOf(
            "01月" to "2025-01",
            "02月" to "2025-02",
            "03月" to "2025-03",
            "04月" to "2025-04",
            "05月" to "2025-05",
            "06月" to "2025-06",
            "07月" to "2025-07",
            "08月" to "2025-08"
        )
        return items.mapIndexed { idx, (first, second) ->
            val normal = "$first\n$second"
            val selected = "$first\n$second"
            LBGenericScrollViewCellModel(
                normalAttributedText = normal,
                selectedAttributedText = selected,
                isSameYear = (idx % 2 == 0)
            )
        }
    }

    private fun buildPriceModels(): List<LBGenericScrollViewCellModel> {
        val prices = listOf(
            98.6f, 99.9f, 100.5f, 101.2f, 102.0f, 102.8f, 103.6f, 104.1f, 105.0f,
            106.3f,
            107.5f,
            109.2f,
            109.1f,
        )
        return prices.mapIndexed { idx, p ->
            val title = "%.2f".format(p)
            val sub = if (idx % 2 == 0) "" else ""
            LBGenericScrollViewCellModel(
                normalAttributedText = title,
                selectedAttributedText = title,
                isSameYear = (idx % 3 == 0),
                price = p
            )
        }
    }


    private fun showModifyOptionChanceDialog() {
        val dialog = ModifyOptionChanceDialog.newInstance()
        dialog.show(supportFragmentManager, "ModifyOptionChanceDialog")
    }


}