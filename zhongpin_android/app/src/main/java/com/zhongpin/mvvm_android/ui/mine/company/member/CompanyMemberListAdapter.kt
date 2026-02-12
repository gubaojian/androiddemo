package com.zhongpin.mvvm_android.ui.mine.company.member

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.sum.glide.setHeaderImage
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListCompanyMemberItemBinding
import com.zhongpin.lib_base.ktx.invisible
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.MemberItem
import com.zhongpin.mvvm_android.ui.common.goCompanyEditMemberActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

class CompanyMemberListAdapter(val mActivity: CompanyMemberListActivity, data: MutableList<MemberItem>)
    : BaseQuickAdapter<MemberItem, CompanyMemberListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: CompanyMemberListAdapter.VH,
        position: Int,
        item: MemberItem?
    ) {
        val itemCount = getItemCount()
        item?.let {
            holder.binding.apply {
                if (itemCount == 1) {
                    memberItem.setBackgroundResource(R.drawable.bg_member_item_all_corner)
                    bottomLine.invisible()
                } else if (position == 0) {
                    memberItem.setBackgroundResource(R.drawable.bg_member_item_top_corner)
                    bottomLine.visible()
                } else if (position == (itemCount - 1)) {
                    memberItem.setBackgroundResource(R.drawable.bg_member_item_bottom_corner)
                    bottomLine.invisible()
                } else {
                    memberItem.setBackgroundResource(R.drawable.bg_member_item_middle)
                    bottomLine.visible()
                }
                avatar.setHeaderImage(item.headImage)
                memberName.text = item.nickName ?: item.mobile
                permissionList.setPermissionList(PingDanAppUtils.roleNameList(item.roleName))

                if (item.isMasterAccount()) {
                    editIcon.invisible()
                } else {
                    editIcon.visible()
                }

                permissionList.setContentClickListener {
                    mActivity.goCompanyEditMemberActivity(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListCompanyMemberItemBinding = ListCompanyMemberItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}