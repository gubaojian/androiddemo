package com.zhongpin.mvvm_android.ui.mine.company.member.add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityCompanyAddMemberBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MemberInfoChangeEvent
import com.zhongpin.mvvm_android.bean.MemberItem
import com.zhongpin.mvvm_android.bean.RoleItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import com.zhongpin.mvvm_android.ui.utils.MobileUtil
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.view.bottomsheet.choosemember.showChooseMemberPermissionDialog


class CompanyAddMemberActivity : BaseVMActivity<CompanyAddMemberViewModel>() {


    private lateinit var mBinding: ActivityCompanyAddMemberBinding;

    private var memberItem: MemberItem? = null;

    private var mRoleList:List<RoleItem>? = null;

    private var mSelectList: MutableList<RoleItem> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            memberItem = IntentUtils.getSerializableExtra(intent, "memberItem",  MemberItem::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanyAddMemberBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.permissionList.setPlaceholder("请选择角色权限")

        mBinding.permissionListContainer.setOnClickListener {
            showMemberPermissionPicker();
        }

        mBinding.permissionList.setContentClickListener {
            showMemberPermissionPicker();
        }

        mBinding.add.setOnClickListener {
            checkAndSubmit()
        }

        mBinding.save.setOnClickListener {
            checkAndSubmit();
        }

        mBinding.deleteMember.setOnClickListener {
            onDeleteMemberClick();
        }

        memberItem?.let {
            fillEditData()
        }

        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)

    }



    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mRoleData.observe(this) {
            if (it.success) {
                mRoleList = it.data
                memberItem?.let { memberIt ->
                    val ids = PingDanAppUtils.roleIdList(memberIt.roleId)
                    mRoleList?.forEach { roleIt ->
                        if (ids.contains(roleIt.id?.toString())) {
                            mSelectList.add(roleIt)
                        }
                    }
                    val roleNames = mSelectList.map { it.roleName ?: "" }.toList()
                    mBinding.permissionList.setPermissionList(roleNames)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getCompanyRoleList()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun fillEditData() {
        memberItem?.let {
            mBinding.memberName.text.clear()
            mBinding.memberName.text.append(it.nickName ?: "")
            mBinding.memberPhone.text.clear()
            mBinding.memberPhone.text.append(it.mobile ?: "")
            mBinding.add.gone()
            mBinding.save.visible()
            mBinding.deleteMember.visible()
        }
    }

    private fun showMemberPermissionPicker() {
        if (mRoleList.isNullOrEmpty()) {
            Toast.makeText(applicationContext,"请在PC端添加好权限后再操作", Toast.LENGTH_LONG).show()
            return
        }
        showChooseMemberPermissionDialog(mRoleList, mSelectList, { roleListIt ->
            mSelectList = roleListIt.toMutableList()
            val roleNames = mSelectList.map { it.roleName ?: "" }.toList()
            mBinding.permissionList.setPermissionList(roleNames)
        })
    }


    private fun checkAndSubmit() {
        if (TextUtils.isEmpty(mBinding.memberName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入姓名", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.memberPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系电话", Toast.LENGTH_LONG).show()
            return
        }
        val phone = mBinding.memberPhone.text.trim().toString();
        if (!MobileUtil.checkMobile(phone)) {
            Toast.makeText(applicationContext,"请输入合法手机号", Toast.LENGTH_LONG).show()
            return
        }
        if (mSelectList.isNullOrEmpty()) {
            Toast.makeText(applicationContext,"请选择角色权限", Toast.LENGTH_LONG).show()
            return
        }
        submitForm();
    }


    private fun submitForm() {
        val parameter:HashMap<String,Any> = hashMapOf()
        val name = mBinding.memberName.text.trim().toString();
        val phone = mBinding.memberPhone.text.trim().toString();
        val roleIds = mSelectList.map { it.id.toString() }.toList()
        parameter["nickName"] = name
        parameter["mobile"] = phone
        parameter["roleId"] = roleIds
        if (memberItem == null) {
            showLoadingDialogV2()
            mViewModel.addCompanyMember(parameter).observe(this) {
                dismissLoadingDialogV2()
                if (it.success && it.data == true) {
                    EventBusUtils.postEvent(MemberInfoChangeEvent(true))
                    Toast.makeText(this, "企业成员添加成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    ToastExt.throttleToast(it.msg, {
                        Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                    })
                }
            }
        } else {
            parameter["id"] = memberItem?.id.toString()
            showLoadingDialogV2()
            mViewModel.editCompanyMember(parameter).observe(this) {
                dismissLoadingDialogV2()
                if (it.success && it.data == true) {
                    EventBusUtils.postEvent(MemberInfoChangeEvent(true))
                    Toast.makeText(this, "企业成员更新成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    ToastExt.throttleToast(it.msg, {
                        Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }

    }

    private fun onDeleteMemberClick() {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "确认移除成员吗？",
            message = HtmlCompat.fromHtml("<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            confirmText = "确认",
            onConfirm = {
                deleteMember()
            }
        );
        dialog.showDialog(this)
    }

    private fun deleteMember() {
        showLoadingDialogV2()
        mViewModel.deleteCompanyMember(memberItem?.id ?: 0L).observe(this) {
            dismissLoadingDialogV2()
            if (it.success && it.data == true) {
                EventBusUtils.postEvent(MemberInfoChangeEvent(true))
                Toast.makeText(this, "企业成员移除成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

}