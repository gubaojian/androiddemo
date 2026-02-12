package com.zhongpin.mvvm_android.ui.scan

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.blankj.utilcode.util.SizeUtils
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.zhilianshidai.pindan.app.databinding.ActivityScanCaptureBinding
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil


class ScanCaptureActivity : BaseVMActivity<ScanCaptureViewModel>() {


    private lateinit var mBinding: ActivityScanCaptureBinding;

    private lateinit var capture: CaptureManager;
    private lateinit var barcodeScannerView: DecoratedBarcodeView;

    private var mSavedInstanceState: Bundle? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        mSavedInstanceState = savedInstanceState
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityScanCaptureBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.appBarContainer)
        mBinding.ivBack.setOnClickListener { finish() }
        barcodeScannerView = mBinding.zxingBarcodeScanner
        val layoutParams = barcodeScannerView.statusView.layoutParams
        (layoutParams as MarginLayoutParams).bottomMargin = SizeUtils.dp2px(40.0f)
        barcodeScannerView.statusView.layoutParams = layoutParams


        capture = CaptureManager(this, mBinding.zxingBarcodeScanner)
        capture.initializeFromIntent(intent, mSavedInstanceState)
        capture.decode()
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

}