package com.zhongpin.mvvm_android.ui.me

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.header.ClassicsHeader
import com.sum.glide.setHeaderImage
import com.zhilianshidai.pindan.app.databinding.FragmentMineBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.base.view.BaseVMFragment
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.BizPermissionUtil
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goAboutMeActivity
import com.zhongpin.mvvm_android.ui.common.goAddressListActivity
import com.zhongpin.mvvm_android.ui.common.goCompanyMemberActivity
import com.zhongpin.mvvm_android.ui.common.goCompanyProfileActivity
import com.zhongpin.mvvm_android.ui.common.goCompanyVerifyPage
import com.zhongpin.mvvm_android.ui.common.goPayAccountDetailActivity
import com.zhongpin.mvvm_android.ui.common.goUserProfileActivity
import com.zhongpin.mvvm_android.ui.common.throttleToast
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * A simple [Fragment] subclass.
 * Use the [MineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class MineFragment : BaseVMFragment<MineViewModel>() {


    private var param1: String? = null

    private lateinit var mBinding: FragmentMineBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(true).init()
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentMineBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(activity, mBinding.content)


        mBinding.userInfoContainer.setOnClickListener {
            requireActivity().goUserProfileActivity()
        }

        mBinding.mineAboutItem.setOnClickListener {
            requireActivity().goAboutMeActivity()
        }


        mBinding.companyInfo.setOnClickListener {
            requireActivity().goCompanyProfileActivity()
        }

        mBinding.memberInfo.setOnClickListener {
            requireActivity().goCompanyMemberActivity()
        }

        mBinding.addressInfo.setOnClickListener {
            requireActivity().goAddressListActivity()
        }



        mBinding.companyItemContainer.setOnClickListener {
            if (UserInfoUtil.companyInfo == null
                || !UserInfoUtil.hasCompanyVerified()) {
                requireActivity().goCompanyVerifyPage(UserInfoUtil.companyInfo)
            }
        }

        mBinding.payAccountDetail.setOnClickListener {
            requireActivity().goPayAccountDetailActivity()

        }
        mBinding.permissionList.setPlaceholder("暂无")
        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(requireActivity()))
        mBinding.refreshLayout.setOnRefreshListener {
            doRequestData()
        }
        registerDefaultLoad(mBinding.content, Constant.COMMON_KEY)
    }


    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPageData.observe(this) {
            mBinding.refreshLayout.finishRefresh()
            if (it.success) {
                showPageData();
            }
        }
    }

    private fun showPageData() {
        val pageData = mViewModel.mPageData.value?.data;
        pageData?.userInfo?.let {
            val userInfo = it.data;
            userInfo?.let {
                if (!TextUtils.isEmpty(userInfo.nickName)) {
                    mBinding.userNick.text = userInfo.nickName
                    mBinding.userNick.visibility = View.VISIBLE;
                } else {
                    mBinding.userNick.visibility = View.GONE;
                }
                mBinding.userPhone.text = "手机号：" + UserInfoUtil.maskPhone(userInfo.mobile)
                mBinding.mineAvatar.setHeaderImage(userInfo.headImage)

                if (UserInfoUtil.hasCompanyVerified()) {
                    mBinding.companyName.visible()
                    mBinding.mineCompanyVerifyNow.gone()
                    val companyName = UserInfoUtil.companyInfo?.companyName ?: userInfo.companyName;
                    mBinding.companyName.text = companyName
                    mBinding.permissionList.setPermissionList(PingDanAppUtils.roleNameList(userInfo.roleName))
                } else {
                    mBinding.companyName.gone()
                    mBinding.mineCompanyVerifyNow.visible()
                    if (UserInfoUtil.companyInfo == null) { //认证状态展示
                        mBinding.mineCompanyVerifyStatus.text = "立即认证";
                    } else if (UserInfoUtil.companyInfo?.status == 0
                         || UserInfoUtil.companyInfo?.status == 1) {
                        mBinding.mineCompanyVerifyStatus.text = "认证中";
                    } else if (UserInfoUtil.companyInfo?.status == 3) {
                        mBinding.mineCompanyVerifyStatus.text = "审核拒绝";
                    }
                    mBinding.permissionList.setPermissionList(emptyList())
                }
            }
        }

        pageData?.payAccountInfo?.let {
            if (it.success) {
                mBinding.payAccountDetail.visible()
                mBinding.remainAmount.text = PingDanAppUtils.showAmount(it?.data?.amount ?: "0.00")

                //没充值权限不展示
                if (!BizPermissionUtil.hasPayManagePermission()) {
                    mBinding.payAccountDetail.gone()
                }
            } else {
                mBinding.payAccountDetail.gone()
                throttleToast(it.msg)
            }
        }

        pageData?.statisticsData?.let {
            mBinding.orderNum.text = it.data?.orderTotalCount ?: "0";
            SpanUtils.with( mBinding.totalAreaAmount)
                .append(it.data?.totalArea ?: "0")
                .setFontSize(18, true)
                .append("㎡")
                .setFontSize(11, true)
                .create()
            mBinding.buyCount.text = it.data?.purchaseCount ?: "0";
        }

    }


    override fun initData() {
        super.initData()
        doRequestData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(loginEvent : LoginEvent){
        if (loginEvent.isLogin) {
            doRequestData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserInfoChangeEvent(event : UserInfoChangeEvent){
        if (event.isChange) {
            doRequestData()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {
            mViewModel.getPageData()
        }
    }

    fun doRequestData() {
        mViewModel.getPageData()
    }



    companion object {
        private val ARG_PARAM1 = "param1"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        @JvmStatic
        fun newInstance(param1: String) =
            MineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}