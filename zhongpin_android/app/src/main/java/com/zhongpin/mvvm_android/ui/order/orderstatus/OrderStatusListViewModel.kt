package com.zhongpin.mvvm_android.ui.order.orderstatus

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderListResponse
import com.zhongpin.mvvm_android.bean.PurchaseOrderListResponse
import com.zhongpin.mvvm_android.biz.utils.BizPermissionUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState
import kotlinx.coroutines.delay

class OrderStatusListViewModel  : BaseViewModel<OrderStatusListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<OrderListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<OrderListResponse>> = MutableLiveData()


    fun getFirstPageOrderList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<OrderListResponse>> {
        initiateRequest({
            if (!BizPermissionUtil.hasViewOrderPermission()) {
                val response = BaseResponse(data = OrderListResponse(
                    records = emptyList<OrderItem>(),
                    noViewOrderPermission = true
                ), success = true);
                delay(300)
                mFirstPageData.value = response.showLoadingState(loadState, Constant.COMMON_KEY)
                return@initiateRequest
            }
            mFirstPageData.value = mRepository.getOrderList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState, mFirstPageData)
        return mFirstPageData;
    }

    fun getOrderListMore(pageNo:Int, query: HashMap<String, Any>) : MutableLiveData<BaseResponse<OrderListResponse>> {
        initiateRequest({
            if (!BizPermissionUtil.hasViewOrderPermission()) {
                val response = BaseResponse(data = OrderListResponse(
                    records = emptyList<OrderItem>(),
                    noViewOrderPermission = true
                ), success = true);
                delay(300)
                mFirstPageData.value = response.showLoadingState(loadState, Constant.COMMON_KEY)
                return@initiateRequest
            }
            mMorePageData.value = mRepository.getOrderList(pageNo, query)
        }, loadState, mMorePageData)
        return mMorePageData;
    }

    fun confirmOrderReceiveDone(orderId: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.confirmOrderReceiveDone(orderId);
        }, dialogLoadState)
        return mLiveData
    }


    val mWaitPayOrderPageData: MutableLiveData<BaseResponse<PurchaseOrderListResponse>> = MutableLiveData()
    fun getWaitPayPurchaseOrderList() : MutableLiveData<BaseResponse<PurchaseOrderListResponse>> {
        initiateRequest({
            val query: HashMap<String, Any> = hashMapOf();
            query.put("status", "0")
            mWaitPayOrderPageData.value = mRepository.getPurchaseOrderList(1, query);
        }, loadState)
        return mWaitPayOrderPageData;
    }

}