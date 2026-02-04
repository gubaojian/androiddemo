package com.longbridge.mdtrade.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lb.price.one.databinding.WeathDialogModifyOptionChanceBinding
import com.lb.util.LogUtils


class ModifyOptionChanceDialog : BottomSheetDialogFragment() {

    private var _binding: WeathDialogModifyOptionChanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = WeathDialogModifyOptionChanceBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        private const val TAG = "OptionChanceDialog"
        fun newInstance(): ModifyOptionChanceDialog {
            return ModifyOptionChanceDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                // 设置为全展开
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                // 允许隐藏，peek 使用自动值，保证可以进入 HIDDEN 状态
                behavior.isHideable = true
                behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置标题
        binding.tvDcaConfirmFinanceAllowTitle.text = "修改期权机会"

        // 设置关闭按钮点击事件
        binding.ivDcaConfirmFinanceAllowClose.setOnClickListener { safeDismiss() }

        // 设置确认按钮点击事件
        binding.btnDcaConfirmFinanceAllowContinue.setOnClickListener {
            onConfirmClick()
        }

        initOptionScrollView()

        initOptionDateTimeLine()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.dismissWithAnimation = true
        dialog.setOnShowListener(OnShowListener { dialogInterface: DialogInterface? ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout?>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> =
                    BottomSheetBehavior.from<FrameLayout?>(bottomSheet)
                behavior.isHideable = true // 允许隐藏
                // 当进入隐藏态时真正关闭，确保 Window 与 dim 一并移除
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottom: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            safeDismiss()
                        }
                    }

                    override fun onSlide(bottom: View, slideOffset: Float) { /* no-op */
                    }
                })
            }
        })

        return dialog
    }

    private fun initOptionScrollView() {
        // 创建价格选项（1-100）
        // todo temp data
        binding.srlOptionPrice.dataSource =
            binding.srlOptionPrice.convertDateLine.getFakePriceData()
        val tempCurrPrice = "95.2"
        binding.srlOptionPrice.currentPrice = tempCurrPrice.toDouble()
//        binding.srlOptionPrice.initialScrollToIndex = 9
        val nearIndex = binding.srlOptionPrice.convertDateLine.getNearIndex(
            tempCurrPrice.toDouble(),
            binding.srlOptionPrice.convertDateLine.getFakePriceData()
        )
        LogUtils.d(TAG, "initOptionScrollView: nearIndex : $nearIndex")
        binding.srlOptionPrice.initialScrollToIndex = nearIndex

        binding.srlOptionPrice.didSelectItemAtIndex = { idx ->
            LogUtils.i(TAG, "price center index = $idx")
            val info = binding.srlOptionPrice.dataSource[idx]
            LogUtils.i(TAG, "price = ${info.price}")
        }
    }


    private fun initOptionDateTimeLine() {
        // todo temp data
        binding.srlOptionDateLine.dataSource =
            binding.srlOptionDateLine.convertDateLine.getFakeTimelineData()

        binding.srlOptionDateLine.didSelectItemAtIndex = { idx ->
            LogUtils.i(TAG, "date center index = $idx")
            val date = binding.srlOptionDateLine.dataSource[idx]
            LogUtils.i(TAG, "date = ${date.expireDate?.expire_date}")
        }
    }

    private fun onConfirmClick() {
        // 这里添加确认按钮的逻辑
        safeDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun safeDismiss() {
        try {
            if (isAdded) dismissAllowingStateLoss() else dismiss()
        } catch (_: Throwable) {
            // ignore
        }
    }
}