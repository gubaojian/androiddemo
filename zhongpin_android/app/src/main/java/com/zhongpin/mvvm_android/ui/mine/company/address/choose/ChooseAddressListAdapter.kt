package com.zhongpin.mvvm_android.ui.mine.company.address.choose

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListChooseAddressItemBinding
import com.zhongpin.mvvm_android.bean.AddressListItemResponse


fun interface OnSetDefaultItemCallback {
    fun  onSetDefault(position: Int, item: AddressListItemResponse)
}

class ChooseAddressListAdapter(val mActivity: AppCompatActivity,
                               val mSetDefaultAction:OnSetDefaultItemCallback,
                               data: MutableList<AddressListItemResponse>)
    : BaseQuickAdapter<AddressListItemResponse, ChooseAddressListAdapter.VH>(data) {

    var  selectPosition = -1;

    class VH(
        parent: ViewGroup,
        val binding: ListChooseAddressItemBinding = ListChooseAddressItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ChooseAddressListAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: ChooseAddressListAdapter.VH,
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
                    selectPosition = position;
                    notifyDataSetChanged();
                }
            }

            if (selectPosition == position) {
                holder.binding.addressItemContainer.setBackgroundResource(R.drawable.bg_address_item_selected)
            } else {
                holder.binding.addressItemContainer.setBackgroundResource(R.drawable.bg_address_item)
            }

            holder.binding.root.setOnClickListener {
                //can choose able
                selectPosition = position;
                notifyDataSetChanged();
            }
        }
    }

}