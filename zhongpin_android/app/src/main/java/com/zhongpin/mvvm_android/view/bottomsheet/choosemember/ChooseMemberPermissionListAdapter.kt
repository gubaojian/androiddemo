package com.zhongpin.mvvm_android.view.bottomsheet.choosemember

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListChooseMemberPermissionItemBinding
import com.zhongpin.mvvm_android.bean.RoleItem

class ChooseMemberPermissionListAdapter(val mActivity: Activity, val mData: MutableList<RoleItem>)
    : BaseQuickAdapter<RoleItem, ChooseMemberPermissionListAdapter.VH>(mData) {

    val selectItems: HashMap<Int, RoleItem> = hashMapOf()

    fun getSortedRoleList():List<RoleItem> {
        val keys = selectItems.keys.toList().sorted()
        val list = mutableListOf<RoleItem>();
        keys.forEach {
            selectItems.get(it)?.let { element -> list.add(element) }
        }
        return list
    }

    fun setSelectItems(selectItems: List<RoleItem>?) {
        selectItems?.forEach {
            val index = mData.indexOf(it)
            if (index >= 0) {
                this.selectItems.put(index, it)
            }
        }
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: RoleItem?
    ) {
        val itemCount = getItemCount()
        item?.let {
            holder.binding.apply {
                if (selectItems.contains(position)) {
                    checkbox.setImageResource(R.mipmap.role_item_checked)
                } else {
                    checkbox.setImageResource(R.mipmap.role_item_un_checked)
                }
                permissionName.text = item.roleName
                root.setOnClickListener {
                    if (selectItems.contains(position)) {
                        selectItems.remove(position)
                    } else {
                        selectItems.put(position, item)
                    }
                    notifyDataSetChanged()
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
        val binding: ListChooseMemberPermissionItemBinding = ListChooseMemberPermissionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}