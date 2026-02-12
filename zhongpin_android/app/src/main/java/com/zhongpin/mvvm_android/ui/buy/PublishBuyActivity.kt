package com.zhongpin.mvvm_android.ui.buy

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPublishBuyBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.bean.PublishBuyEvent
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.buy.add.AddPublishBuyDetailActivity
import com.zhongpin.mvvm_android.ui.buy.fragment.PublishBuyListFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


@EventBusRegister
class PublishBuyActivity : BaseVMActivity<PublishBuyViewModel>() {


    private lateinit var mBinding: ActivityPublishBuyBinding;

    private var mTitles = arrayOf("未发布", "已发布")
    val mFragments: MutableList<Fragment> = mutableListOf()
    private var mLastSelectTabPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPublishBuyBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.publishButton.setOnClickListener {
            val intent = Intent(this@PublishBuyActivity, AddPublishBuyDetailActivity::class.java)
            startActivity(intent)
            return@setOnClickListener
            PictureSelector.create(this@PublishBuyActivity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCompressEngine(object: CompressFileEngine {
                    override fun onStartCompress(
                        context: Context?,
                        source: java.util.ArrayList<Uri>?,
                        call: OnKeyValueResultCallbackListener?
                    ) {
                        if (source == null || source.isEmpty()) {
                            return;
                        }
                        Luban.with(this@PublishBuyActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                                object : OnNewCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(source: String?, compressFile: File?) {
                                        if (call != null) {
                                            if (compressFile != null) {
                                                LogUtils.d(
                                                    "PersonVerifyActivity",
                                                    "PersonVerifyActivity compressFile " + source + " compress " + compressFile?.absolutePath
                                                            + " length " + compressFile!!.length() / 1024
                                                )
                                            }
                                            call.onCallback(source, compressFile?.absolutePath);
                                        }
                                    }

                                    override fun onError(source: String?, e: Throwable?) {
                                        if (call != null) {
                                            call.onCallback(source, null);
                                        }
                                    }
                                }
                            ).launch();
                    }

                })
                .setMaxSelectNum(6)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        if (result.isNullOrEmpty()) {
                            return;
                        }
                        if (result == null || result.isEmpty()) {
                            return;
                        }
                        val selectFiles: MutableList<String> = mutableListOf();
                        for(localMedia in result) {
                            if (localMedia == null) {
                                continue;
                            }
                            if(BuildConfig.DEBUG) {
                                Log.e("PublishBuyActivity", "PublishBuyActivity photo selector " + localMedia?.path + " cut path " + localMedia?.cutPath)
                            }
                            val filePath = localMedia?.compressPath ?: localMedia?.realPath;
                            if (filePath.isNullOrEmpty()) {
                                continue
                            }
                            filePath?.let {
                                selectFiles.add(filePath)
                            }
                        }
                        uploadPublichBuyImages(selectFiles)
                    }

                    override fun onCancel() {
                    }
                })
        }

        //
        mFragments.clear();
        mTitles.withIndex().forEach {
            val pageType = "${it.index}";
            mFragments.add(PublishBuyListFragment.newInstance(pageType))
        }

        mBinding.viewPager2.adapter = object : FragmentStateAdapter(this@PublishBuyActivity) {
            override fun getItemCount(): Int {
                return mFragments.size;
            }

            override fun createFragment(position: Int): Fragment {
                return mFragments.get(position);
            }
        }


        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2, object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                val customView = LayoutInflater.from(this@PublishBuyActivity)
                    .inflate(R.layout.publish_buy_custom_tab, mBinding.tabLayout, false)

                customView.findViewById<TextView>(R.id.testName).text = mTitles[position]
                val isSelected = (position == mBinding.viewPager2.currentItem)
                if (isSelected) {
                    customView?.let {
                        val text = it.findViewById<TextView>(R.id.testName)
                        text.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        it.findViewById<ImageView>(R.id.icon).visibility = View.VISIBLE
                    }
                }
                tab.customView = customView
                tab.view?.setOnClickListener {
                    mBinding.viewPager2.currentItem = tab.position
                }
            }

        }).attach();

        mBinding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != mLastSelectTabPosition) {
                    var unselectTab = mBinding.tabLayout.getTabAt(mLastSelectTabPosition)
                    unselectTab?.customView?.let {
                        val text = it.findViewById<TextView>(R.id.testName)
                        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f) // 选中时的大小
                        text.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                        it.findViewById<ImageView>(R.id.icon).visibility = View.INVISIBLE
                    }
                    val selectedTab = mBinding.tabLayout.getTabAt(position)
                    selectedTab?.customView?.let {
                        val text = it.findViewById<TextView>(R.id.testName)
                        text.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        it.findViewById<ImageView>(R.id.icon).visibility = View.VISIBLE
                    }
                    mLastSelectTabPosition = position;
                } else {
                    val selectedTab = mBinding.tabLayout.getTabAt(position)
                    selectedTab?.customView?.let {
                        val text = it.findViewById<TextView>(R.id.testName)
                        text.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        it.findViewById<ImageView>(R.id.icon).visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun uploadPublichBuyImages(selectFiles: MutableList<String>) {
        if (selectFiles.isEmpty()) {
            return
        }
        val imageUrls:MutableList<String> = mutableListOf();
        showLoadingDialog();
        mViewModel.uploadImageList(selectFiles).observe(this) {
            var allSuccess = true;
            var message : String = "";
            for(response in it) {
                if (!response.success) {
                    allSuccess = false;
                    if (!TextUtils.isEmpty(response.msg)) {
                        message = response.msg;
                    }
                }
                response.data?.let {
                        imageUrl ->
                    imageUrls.add(imageUrl)
                }
            }
            if (allSuccess) {
                submitFormInfo();
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext,"图片上传失败 " + message, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun showTipDialog() {
        val builder = AlertDialog.Builder(this@PublishBuyActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的采购信息已提交，\n" +
                "请等待人工处理。\n")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        builder.show()
    }

    private fun submitFormInfo() {
        dismissLoadingDialog();
        showTipDialog();
        EventBusUtils.postEvent(PublishBuyEvent(true));
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedback(feedbackChangeEvent: FeedbackChangeEvent) {
        finish();
    }

    private var mLoadingDialog: LoadingDialog? = null
    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        dismissLoadingDialog()
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this, false)
        }
        mLoadingDialog?.showDialogV2(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialogV2()
        mLoadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}