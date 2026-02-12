package com.zhongpin.mvvm_android.view.bottomsheet.choosemember

import android.app.Activity
import android.widget.Toast
import com.zhilianshidai.pindan.app.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.databinding.BottomSheetChoosePermissionBinding
import com.zhongpin.mvvm_android.bean.RoleItem

fun interface OnChooseRoleAction {
    fun onChooseRole(roles: List<RoleItem>)
}

class ChooseMemberPermissionBottomSheet(val mContext: Activity,
                                        val action: OnChooseRoleAction,
                                        val selectItems:List<RoleItem>? = null,
                                        var mDatas: List<RoleItem>) : PartShadowPopupView(mContext) {

    private val listAdapter: ChooseMemberPermissionListAdapter = ChooseMemberPermissionListAdapter(mContext, mDatas.toMutableList())

    override fun getImplLayoutId(): Int {
        return R.layout.bottom_sheet_choose_permission
    }

    override fun initPopupContent() {
        super.initPopupContent()
        val mBinding: BottomSheetChoosePermissionBinding = BottomSheetChoosePermissionBinding.bind(attachPopupContainer.getChildAt(0))

        mBinding.closePopup.setOnClickListener {
            dismiss()
        }

        listAdapter.setSelectItems(selectItems)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mBinding.recyclerView.adapter = listAdapter



        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnSubmit.setOnClickListener {
            val roles = listAdapter.getSortedRoleList()
            if (roles.isNullOrEmpty()) {
                Toast.makeText(mContext, "请选择角色", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            action.onChooseRole(roles)
            dismiss()
        }
    }


}