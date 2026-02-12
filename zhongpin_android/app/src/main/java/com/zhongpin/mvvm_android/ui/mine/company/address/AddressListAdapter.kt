package com.zhongpin.mvvm_android.ui.mine.company.address

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListAddressItemBinding
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.ui.mine.company.address.choose.OnSetDefaultItemCallback
import com.zhongpin.mvvm_android.ui.utils.AreaUtil

fun interface OnDeleteItemCallback {
    fun  onDelete(position: Int, item: AddressListItemResponse)
}

fun interface OnEditItemCallback {
    fun  onEdit(position: Int, item: AddressListItemResponse)
}

class AddressListAdapter(val mActivity: AppCompatActivity,
                         val onDeleteItemAction : OnDeleteItemCallback,
                         val onEditItemAction : OnEditItemCallback,
                         val mSetDefaultAction:OnSetDefaultItemCallback,
                         data: MutableList<AddressListItemResponse>)
    : BaseQuickAdapter<AddressListItemResponse, AddressListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: AddressListAdapter.VH,
        position: Int,
        item: AddressListItemResponse?
    ) {
        item?.let {
            holder.binding.apply {
                detailAddress.text =  item.toShouHuoAddress()
                name.text = item.name ?: ""
                contractPhone.text = item.mobile ?: ""
                if (item.status == 1) {
                    setDefaultAddress.visibility = View.GONE
                    defaultAddress.visibility = View.VISIBLE
                } else {
                    setDefaultAddress.visibility = View.VISIBLE
                    defaultAddress.visibility = View.GONE
                }
                setDefaultAddress.setOnClickListener {
                    mSetDefaultAction.onSetDefault(position, item);
                }

                deleteButtonContainer.setOnClickListener {
                    onDeleteItemAction.onDelete(position, item)
                }
                editButtonContainer.setOnClickListener {
                    onEditItemAction.onEdit(position, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): AddressListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListAddressItemBinding = ListAddressItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}