package com.zhongpin.mvvm_android.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.sum.glide.setUrlWithDrawablePlaceholder
import com.yalantis.ucrop.UCrop
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.R
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.utils.convertTo
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.PermissionConfirmDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.bean.AppUpdateInfo
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.HideSplashEvent
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.MemberItem
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.OrderFeedbackItem
import com.zhongpin.mvvm_android.bean.OrderFilterQuery
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayItemFilterQuery
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.SwitchToMainTabEvent
import com.zhongpin.mvvm_android.bean.SwitchToOrderSubTabEvent
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import com.zhongpin.mvvm_android.biz.utils.BizPermissionUtil
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.location.OnLocationCallback
import com.zhongpin.mvvm_android.location.OnceLocationHelper
import com.zhongpin.mvvm_android.location.OnceLocationHelper.Companion.lastCacheLocation
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.photo.selector.GlideUCropEngine
import com.zhongpin.mvvm_android.ui.about.AboutActivity
import com.zhongpin.mvvm_android.ui.find.FindPwdActivity
import com.zhongpin.mvvm_android.ui.home.mineprice.MinePriceListActivity
import com.zhongpin.mvvm_android.ui.home.mineprice.detail.MinePriceDetailActivity
import com.zhongpin.mvvm_android.ui.home.platform.detail.PlatformPriceDetailActivity
import com.zhongpin.mvvm_android.ui.home.platform.list.PlatformPriceListActivity
import com.zhongpin.mvvm_android.ui.me.profile.UserProfileActivity
import com.zhongpin.mvvm_android.ui.me.profile.change.head.ChangeHeadImageActivity
import com.zhongpin.mvvm_android.ui.me.profile.change.nick.ChangeNickActivity
import com.zhongpin.mvvm_android.ui.me.profile.change.phone.ChangePhoneActivity
import com.zhongpin.mvvm_android.ui.me.profile.change.tip.ChangePhoneTipActivity
import com.zhongpin.mvvm_android.ui.mine.company.address.AddressListActivity
import com.zhongpin.mvvm_android.ui.mine.company.member.CompanyMemberListActivity
import com.zhongpin.mvvm_android.ui.mine.company.member.add.CompanyAddMemberActivity
import com.zhongpin.mvvm_android.ui.mine.company.member.search.CompanyMemberSearchQueryActivity
import com.zhongpin.mvvm_android.ui.mine.company.profile.CompanyProfileActivity
import com.zhongpin.mvvm_android.ui.mine.company.profile.change.ChangeCompanyContractActivity
import com.zhongpin.mvvm_android.ui.mine.company.result.CompanySignResultActivity
import com.zhongpin.mvvm_android.ui.mine.company.sign.CompanySignActivity
import com.zhongpin.mvvm_android.ui.mine.company.submit.SubmitCompanyInfoActivity
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.order.add.item.add.AddPurchaseItemActivity
import com.zhongpin.mvvm_android.ui.order.confirmreceipt.ConfirmReceiptOrderActivity
import com.zhongpin.mvvm_android.ui.order.confirmreceipt.result.ConfirmReceiptOrderPayResultActivity
import com.zhongpin.mvvm_android.ui.order.delivery.DeliveryDetailActivity
import com.zhongpin.mvvm_android.ui.order.detail.OrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.feedback.list.OrderFeedbackListActivity
import com.zhongpin.mvvm_android.ui.order.feedback.submit.SubmitFeedbackActivity
import com.zhongpin.mvvm_android.ui.order.preview.result.SubmitOrderPayResultActivity
import com.zhongpin.mvvm_android.ui.order.purchaselist.PurchaseOrderListActivity
import com.zhongpin.mvvm_android.ui.order.purchaselist.pay.PayPurchaseOrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.search.OrderSearchQueryActivity
import com.zhongpin.mvvm_android.ui.order.view.ConfirmReceiptBottomSheet
import com.zhongpin.mvvm_android.ui.order.view.OrderFilterBottomSheet
import com.zhongpin.mvvm_android.ui.pay.PayAccountDetailActivity
import com.zhongpin.mvvm_android.ui.pay.chargeinput.ChargeInputActivity
import com.zhongpin.mvvm_android.ui.pay.chargepay.ChargePayActivity
import com.zhongpin.mvvm_android.ui.pay.detail.PayBillDetailActivity
import com.zhongpin.mvvm_android.ui.pay.view.PayItemFilterBottomSheet
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import com.zhongpin.mvvm_android.ui.utils.ApkDownloadUtil
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.web.WebActivity
import com.zhongpin.mvvm_android.view.NoSelectLinkMovementMethod
import com.zhongpin.mvvm_android.view.bottomsheet.confirmreceipt.ConfirmReceiptNoPayBottomSheet
import com.zhongpin.mvvm_android.view.bottomsheet.confirmreceipt.ConfirmReceiptPayBottomSheet
import com.zhongpin.mvvm_android.view.bottomsheet.purchase.PurchaseOrderConfirmPayBottomSheet
import com.zhongpin.mvvm_android.view.dialog.DatePickerDialog
import com.zhongpin.mvvm_android.view.dialog.AppUpdateConfirmDialog
import com.zhongpin.mvvm_android.view.dialog.ProtocolConfirmDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


fun Activity.goAddOrderPage() {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureSubmitOrderPermission {
                    val intent = Intent(this, AddOrderActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能下单\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}

fun Activity.goOrderSearchQueryActivity() {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureViewOrderPermission {
                    val intent = Intent(this, OrderSearchQueryActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能搜索\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}


fun Activity.goPurchaseOrderListActivity() {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureSubmitOrderPermission {
                    val intent = Intent(this, PurchaseOrderListActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能搜索\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}

fun Activity.goBuyOrderAgain(orderItem: OrderItem) {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureSubmitOrderPermission {
                    val intent = Intent(this, AddPurchaseItemActivity::class.java)
                    ShareParamDataUtils.orderItem = orderItem;
                    intent.putExtra("buyAgainOrderId", orderItem.id ?: 0L)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能下单\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}

fun Activity.goBuyOrderAgainFromPurchaseOrder(orderItem: PurchaseOrderDetail) {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureSubmitOrderPermission {
                    val intent = Intent(this, AddOrderActivity::class.java)
                    ShareParamDataUtils.putParams("purchaseOrderItem", orderItem)
                    intent.putExtra("buyAgainFromPurchaseOrder", true)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能下单\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}
fun Activity.goBuyOrderAgainWithOrderDetail(orderItem: OrderDetailItem) {
    orderItem.convertTo(OrderItem::class.java)?.let {
        goBuyOrderAgain(it);
    }
}

fun Activity.goBuyOrderAgain(materialPriceItem: MaterialPriceItem) {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                BizPermissionUtil.ensureSubmitOrderPermission {
                    val intent = Intent(this, AddPurchaseItemActivity::class.java)
                    ShareParamDataUtils.putParams("buyAgainMaterialPriceItem", materialPriceItem);
                    intent.putExtra("buyAgainMaterialPriceItemId", materialPriceItem.id ?: 0L)
                    startActivity(intent)
                }
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能下单\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}


@OptIn(DelicateCoroutinesApi::class)
fun Activity.goOrderTabSelectOrderIng() {
    LoginUtils.ensureLogin(
        this, {
            BizPermissionUtil.ensureViewOrderPermission {
                EventBusUtils.postEvent(SwitchToMainTabEvent(tabIndex = 1))
                GlobalScope.launch(Dispatchers.Main) {
                    delay(30)
                    EventBusUtils.postEvent(SwitchToOrderSubTabEvent(tabIndex = 1))
                }
            }
   });
}

@OptIn(DelicateCoroutinesApi::class)
fun Activity.goMyOrderTab() {
    LoginUtils.ensureLogin(
        this, {
            BizPermissionUtil.ensureViewOrderPermission {
                EventBusUtils.postEvent(SwitchToMainTabEvent(tabIndex = 1))
                GlobalScope.launch(Dispatchers.Main) {
                    delay(30)
                    EventBusUtils.postEvent(SwitchToOrderSubTabEvent(tabIndex = 0))
                }
            }
        });
}

fun Activity.ensureCompanyVerify(block: (() -> Unit)?) {
    LoginUtils.ensureLogin(
        this, {
            if (UserInfoUtil.hasCompanyVerified()) {
                block?.invoke()
            } else {
                val dialog = ConfirmDialog(
                    mContext = this,
                    title = "企业未认证",
                    message = "需企业认证后才能操作\n",
                    confirmText = "去认证",
                    onConfirm = {
                        goCompanyVerifyPage(UserInfoUtil.companyInfo)
                    }
                );
                dialog.showDialog(this)
            }
        }
    )
}

fun Activity.goOrderDetailActivity(orderItem: OrderItem) {
    val intent = Intent(this, OrderDetailActivity::class.java)
    ShareParamDataUtils.orderItem = orderItem; //代替intent，避免传递序列化异常
    intent.putExtra("orderId", orderItem.id ?: 0)
    startActivity(intent)
}



fun Activity.goDeliveryProfListPage(orderDetailItem: OrderDetailItem) {
    val intent = Intent(this, DeliveryDetailActivity::class.java)
    intent.putExtra("orderId", orderDetailItem.id)
    ShareParamDataUtils.putParams("orderDetailItem", orderDetailItem)
    startActivity(intent)
}


fun Activity.goOrderFeedbackList(orderId: Long) {
    val intent = Intent(this, OrderFeedbackListActivity::class.java)
    intent.putExtra("orderId", orderId)
    startActivity(intent)

}

fun Activity.goCompanyVerifyPage(item: CompanyListItemResponse?) {
    if (item == null) {
        val intent = Intent(this, SubmitCompanyInfoActivity::class.java)
        startActivity(intent)
    } else if (item.status == 0) {
        val intent = Intent(this, CompanySignActivity::class.java)
        intent.putExtra("companyInfo", item)
        startActivity(intent)
    } else {
        val intent = Intent(this, CompanySignResultActivity::class.java)
        intent.putExtra("companyInfo", item)
        startActivity(intent)
    }
}

fun Activity.goSubmitOrderResultActivity() {
    val intent = Intent(this, SubmitOrderPayResultActivity::class.java)
    startActivity(intent)
}


fun Activity.goConfirmReceiptOrderPayResultActivity() {
    val intent = Intent(this, ConfirmReceiptOrderPayResultActivity::class.java)
    startActivity(intent)
}


fun Activity.goMinePriceListActivity() {
    LoginUtils.ensureLogin(this, {
        val intent = Intent(this, MinePriceListActivity::class.java)
        startActivity(intent)
    })
}

fun Activity.goMinePriceDetailActivity(item: MaterialPriceItem) {
    LoginUtils.ensureLogin(this, {
        val intent = Intent(this, MinePriceDetailActivity::class.java)
        intent.putExtra("materialPriceId", item.id ?: 0L)
        ShareParamDataUtils.materialPriceItem = item
        startActivity(intent)
    })
}

fun Activity.goPlatformPriceDetailActivity(item: PlatformMaterialItem) {
    val intent = Intent(this, PlatformPriceDetailActivity::class.java)
    intent.putExtra("materialPriceId", item.id ?: 0L)
    ShareParamDataUtils.platformMaterialPriceItem = item
    startActivity(intent)
}

fun Activity.goPlatformPriceListActivity() {
    val intent = Intent(this, PlatformPriceListActivity::class.java)
    startActivity(intent)
}


// 自动收货完成后，自动跳转到反馈页面
var autoGoFeedbackWhenDoneConfirmReceiveAction: (() -> Unit)? = null;

fun Activity.autoFeedback(item: OrderItem?, confirmReceiveAction: (() -> Unit)?) {
    BizPermissionUtil.ensureOrderManagePermission {
        if (item == null) {
            return@ensureOrderManagePermission;
        }
        goSubmitFeedbackPage(item) //直接去反馈界面，后端自动处理确认收货。
    }
    /**
    BizPermissionUtil.ensureOrderManagePermission {
        if (item == null) {
            return@ensureOrderManagePermission;
        }
        if (item.orderStatus == 50) {
            val dialog = ConfirmDialog(
                mContext = this,
                title = "申诉订单",
                message = HtmlCompat.fromHtml("采购订单相关的问题（质量问题、数量不足或多余等）均可通过提交“申诉单”后由平台负责处理。<br/>" +
                        "<br/>" +
                        "点击<font color='#557EF7'>【确认】</font>后将自动完成收货确认，并跳转至创建申诉单页面<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                confirmText = "确定",
                onConfirm = {
                    autoGoFeedbackWhenDoneConfirmReceiveAction = {
                        goSubmitFeedbackPage(item)
                        autoGoFeedbackWhenDoneConfirmReceiveAction = null
                    }
                    confirmReceiveAction?.invoke()
                }
            );
            dialog.showDialog(this)
        } else {
            goSubmitFeedbackPage(item)
        }
    }*/

}

fun Activity.goSubmitFeedbackPage(item: OrderItem?) {
    BizPermissionUtil.ensureOrderManagePermission {
        val orderId = item?.id ?: 0L;
        val intent = Intent(this, SubmitFeedbackActivity::class.java)
        intent.putExtra("orderId", orderId)
        ShareParamDataUtils.orderItem = item
        startActivity(intent)
    }
}

fun Activity.goEditFeedbackPage(item: OrderFeedbackItem) {
    val intent = Intent(this, SubmitFeedbackActivity::class.java)
    intent.putExtra("feedbackItem", item)
    startActivity(intent)
}

fun Activity.goAboutMeActivity() {
    val intent = Intent(this, AboutActivity::class.java)
    startActivity(intent)
}

fun Activity.goWebActivity(url:String = "", title:String? = null) {
    val intent = Intent(this, WebActivity::class.java)
    intent.putExtra("title", title)
    intent.putExtra("url",url)
    startActivity(intent)
}

fun Activity.goAddressListActivity() {
    ensureCompanyVerify {
        BizPermissionUtil.ensureCompanyManagePermission {
            val intent = Intent(this, AddressListActivity::class.java)
            startActivity(intent)
        }
    }
}

fun Activity.goPayAccountDetailActivity() {
    ensureCompanyVerify {
        BizPermissionUtil.ensurePayManagePermission {
            val intent = Intent(this, PayAccountDetailActivity::class.java)
            startActivity(intent)
        }
    }
}

fun Activity.goCompanyProfileActivity() {
    ensureCompanyVerify {
        BizPermissionUtil.ensureCompanyManagePermission {
            val intent = Intent(this, CompanyProfileActivity::class.java)
            startActivity(intent)
        }
    }
}

fun Activity.goChangeCompanyContractActivity() {
    ensureCompanyVerify {
        val intent = Intent(this, ChangeCompanyContractActivity::class.java)
        startActivity(intent)
    }
}

fun Activity.goCompanyMemberActivity() {
    ensureCompanyVerify {
        BizPermissionUtil.ensureCompanyManagePermission {
            val intent = Intent(this, CompanyMemberListActivity::class.java)
            startActivity(intent)
        }
    }
}

fun Activity.goCompanyMemberSearchQueryActivity() {
    ensureCompanyVerify {
        val intent = Intent(this, CompanyMemberSearchQueryActivity::class.java)
        startActivity(intent)
    }
}

fun Activity.goCompanyAddMemberActivity() {
    ensureCompanyVerify {
        val intent = Intent(this, CompanyAddMemberActivity::class.java)
        startActivity(intent)
    }
}

fun Activity.goCompanyEditMemberActivity(item: MemberItem) {
    ensureCompanyVerify {
        val intent = Intent(this, CompanyAddMemberActivity::class.java)
        intent.putExtra("memberItem", item)
        startActivity(intent)
    }
}


fun Activity.goChargeInputActivity() {
    val intent = Intent(this, ChargeInputActivity::class.java)
    startActivity(intent)
}

fun Activity.goChargePayActivity(payItem: PayItem) {
    val intent = Intent(this, ChargePayActivity::class.java)
    intent.putExtra("payId", payItem.id ?: 0L);
    ShareParamDataUtils.payItem = payItem;
    startActivity(intent)
}


fun Activity.goPayRecordDetailActivity(item: PayItem) {
    val intent = Intent(this, PayBillDetailActivity::class.java)
    ShareParamDataUtils.payItem = item
    intent.putExtra("payId", item.id ?: 0L);
    startActivity(intent)
}


fun Activity.goUserProfileActivity() {
    val intent = Intent(this, UserProfileActivity::class.java)
    startActivity(intent)
}


fun Activity.goChangeHeadImageActivity() {
    val intent = Intent(this, ChangeHeadImageActivity::class.java)
    startActivity(intent)
}

fun Activity.goChangeUserNickActivity() {
    val intent = Intent(this, ChangeNickActivity::class.java)
    startActivity(intent)
}

fun Activity.goChangeUserPhoneTipActivity() {
    val intent = Intent(this, ChangePhoneTipActivity::class.java)
    startActivity(intent)
}

fun Activity.goChangeUserPhoneActivity() {
    val intent = Intent(this, ChangePhoneActivity::class.java)
    startActivity(intent)
}


fun Activity.previewHeaderImageActivity(url: String?) {
    if (TextUtils.isEmpty(url)) {
       val packageName = getPackageName();
       val drawableId = R.mipmap.ic_user_default
        val uri = ("android.resource://$packageName/$drawableId").toUri();
        val intent = Intent(this, PhonePreviewerActivity::class.java)
        intent.putExtra("imageUrls", arrayOf<String>(uri.toString()))
        startActivity(intent)
    } else {
        val intent = Intent(this, PhonePreviewerActivity::class.java)
        intent.putExtra("imageUrls", arrayOf<String>(url as String))
        startActivity(intent)
    }

}

fun Context.previewImageActivity(url: String?) {
    url?.let {
        val intent = Intent(this, PhonePreviewerActivity::class.java)
        intent.putExtra("imageUrls", arrayOf<String>(url))
        startActivity(intent)
    }
}

fun ImageView.setImageUrlAndEnablePreviewByXPopup(url: String?) {
    setUrlWithDrawablePlaceholder(url)
    enablePreviewImageByXPopup(url)
}
fun ImageView.enablePreviewImageByXPopup(url: String?) {
    setOnClickListener {
        if (TextUtils.isEmpty(url)) {
            return@setOnClickListener
        }
        XPopup.setNavigationBarColor(Color.BLACK)
        val popup = XPopup.Builder(context)
            .asImageViewer(this,
                url,
                SmartGlideImageLoader());
        popup.setBgColor(Color.BLACK)
        popup.isShowSaveButton(false)
        popup.show()
    }
}

fun Activity.checkNotificationPermission() {
    if (!NotificationUtils.areNotificationsEnabled()) {
        XXPermissions.with(this)
            .permission(PermissionLists.getPostNotificationsPermission())
            .request(OnPermissionCallback { permissions, allGranted ->
                if (!allGranted) {
                    return@OnPermissionCallback
                }
            })
    }
}

fun Activity.showOrderFilterDialog(filter: OrderFilterQuery) {
    val view = findViewById<View>(R.id.homeOrderFilterBottomView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(OrderFilterBottomSheet(this, filter))
        .show()
}

fun Activity.showOrderFilterDatePickerDialog(selectDate:DateEntity? = null, onPicker: OnDatePickedListener? = null) {
    val dialog = DatePickerDialog(
        mContext = this,
        startDate =  DateEntity.yearOnFuture(-30),
        endDate = DateEntity.today(),
        selectDate = selectDate ?: DateEntity.today(),
        onPicker= onPicker
    );
    dialog.showDialog(this)
}

fun Activity.showPayItemFilterDialog(filter: PayItemFilterQuery) {
    val view = findViewById<View>(R.id.payFilterBottomView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(PayItemFilterBottomSheet(this, filter))
        .show()
}

fun Activity.showPayItemFilterDatePickerDialog(selectDate:DateEntity? = null, onPicker: OnDatePickedListener? = null) {
    val dialog = DatePickerDialog(
        mContext = this,
        startDate =  DateEntity.yearOnFuture(-30),
        endDate = DateEntity.today(),
        selectDate = selectDate ?: DateEntity.today(),
        onPicker= onPicker
    );
    dialog.showDialog(this)
}

fun Activity.showDatePickerBottomSheet() {
    val dialog = ConfirmDialog(
        mContext = this,
        title = "联系客服",
        message = HtmlCompat.fromHtml("平台客服热线：<br/><br/> <font color='#557EF7'>${Constant.CONTRACT_KEFU_PHONE_NUM}</font><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "打电话",
        onConfirm = {
            callKeFuPhone()
        }
    );
    dialog.showDialog(this)
}


fun Activity.showConfirmReceiptDialog(orderItem: OrderItem,
                                      block: (()->Unit)? = null,
                                      dialogPaddingBottom :Int =  0) {
    BizPermissionUtil.ensureOrderManagePermission {
        autoGoFeedbackWhenDoneConfirmReceiveAction = null;
        XPopup.setNavigationBarColor(Color.WHITE)
        XPopup.Builder(this)
            .isDestroyOnDismiss(true)
            .isDarkTheme(false)
            .asCustom(ConfirmReceiptBottomSheet(
                this,

                orderItem,
                dialogPaddingBottom = dialogPaddingBottom,
                block = block))
            .show()
    }
}


fun Activity.showConfirmCancelOrderDialog(onConfirm: OnClickListener? = null) {
    BizPermissionUtil.ensureOrderManagePermission {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "取消订单",
            message = HtmlCompat.fromHtml("确定取消订单？<br/> <font color='#999999'>下单时间1小时内的订单可取消，订单金额将退回至预付款余额</font><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            confirmText = "确定",
            onConfirm = onConfirm
        );
        dialog.showDialog(this)
    }
}


fun Activity.showContractKeFuDialog() {
    val dialog = ConfirmDialog(
        mContext = this,
        title = "联系客服",
        message = HtmlCompat.fromHtml("平台客服热线：<br/><br/> <font color='#557EF7'>${Constant.CONTRACT_KEFU_PHONE_NUM}</font><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "打电话",
        onConfirm = {
            callKeFuPhone()
        }
    );
    dialog.showDialog(this)
}


fun Activity.showCancelFeedbackDialog(onConfirm: OnClickListener? = null) {
    val dialog = ConfirmDialog(
        mContext = this,
        title = "撤销申诉",
        message = HtmlCompat.fromHtml("确定撤销此申诉吗？<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "确定",
        onConfirm = {
           onConfirm?.onClick(it)
        }
    );
    dialog.showDialog(this)
}


fun Activity.callKeFuPhone() {
    try {
        val intent: Intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Constant.CONTRACT_KEFU_PHONE_TEL_URI.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "唤起系统电话应用失败，请手动输入号码拨打", Toast.LENGTH_SHORT).show()
    }
}

fun Activity.goResetPwd(from:String, autoFillPhoneNum:String?) {
    val intent = Intent(this, FindPwdActivity::class.java)
    intent.putExtra("from", from);
    intent.putExtra("autoFillPhoneNum", autoFillPhoneNum)
    startActivity(intent)
}

fun Activity.showConfirmUpdatePwdDialog() {
    val dialog = ConfirmDialog(
        mContext = this,
        title = "确认去修改登录密码吗？",
        message = HtmlCompat.fromHtml("<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "确认",
        onConfirm = {
            goResetPwd("fromUserProfile", UserInfoUtil.getPhone() )
        }
    );
    dialog.showDialog(this)
}

fun Activity.showConfirmLoginOutDialog(grantAction: (() -> Unit)? = null) {
    val dialog = ConfirmDialog(
        mContext = this,
        title = "确认退出登录吗？",
        message = HtmlCompat.fromHtml("<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "确认",
        onConfirm = {
            grantAction?.invoke()
        }
    );
    dialog.showDialog(this)
}

fun Activity.loginOut() {
    EventBusUtils.postEvent(TokenExpiredEvent(true));
}

fun Activity.loginOutManual() {
    EventBusUtils.postEvent(TokenExpiredEvent(true, isManual = true));
}

fun Activity.showImagePicker(maxSelectNum:Int = 1, onPickerImageCallback: ((filePath:List<String>)->Unit)) {
    val activity = this;
    val logTag = this.localClassName
    PictureSelector.create(activity)
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
                Luban.with(activity)
                    .load(source)
                    .ignoreBy(100).setCompressListener(
                        object : OnNewCompressListener {
                            override fun onStart() {

                            }

                            override fun onSuccess(source: String?, compressFile: File?) {
                                if (call != null) {
                                    if (compressFile != null) {
                                        if (BuildConfig.DEBUG) {
                                            LogUtils.d(
                                                logTag,
                                                "${logTag} compressFile " + source + " compress " + compressFile?.absolutePath
                                                        + " length " + compressFile!!.length() / 1024
                                            )
                                        }
                                    }
                                    call.onCallback(source, compressFile?.absolutePath);
                                }
                            }

                            override fun onError(source: String?, e: Throwable?) {
                                if (BuildConfig.DEBUG) {
                                    LogUtils.e(
                                        logTag,
                                        "${logTag} compressFile onError ${e?.message}" + source
                                    )
                                }
                                call?.onCallback(source, null)
                            }
                        }
                    ).launch();
            }

        })
        .setMaxSelectNum(maxSelectNum)
        .forResult(object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: ArrayList<LocalMedia?>?) {
                if (result.isNullOrEmpty()) {
                    return;
                }
                val images = mutableListOf<String>();
                result.forEach {
                    val localMedia = it;
                    if (localMedia == null) {
                       return@forEach;
                    }
                    val filePath = localMedia.compressPath ?: localMedia.realPath;

                    if(BuildConfig.DEBUG) {
                        Log.e(logTag, "${logTag} photo selector  ${filePath} ${GsonUtils.toJson(localMedia)}")
                    }

                    if (filePath.isNullOrEmpty()) {
                        return@forEach;
                    }
                    images.add(filePath)
                }
                onPickerImageCallback(images)
            }

            override fun onCancel() {
            }
        })
}

fun Activity.showImagePickerWithCrop(onPickerImageCallback: ((filePath:List<String>)->Unit)) {
   showHeaderPhotoPermissionDialog {
       showImagePickerWithCropDirect(onPickerImageCallback)
   }
}

fun Activity.showImagePickerWithCropDirect(onPickerImageCallback: ((filePath:List<String>)->Unit)) {
    val activity = this;
    val logTag = this.localClassName
    PictureSelector.create(activity)
        .openGallery(SelectMimeType.ofImage())
        .setCropEngine(object : CropFileEngine {
            override fun onStartCrop(
                fragment: Fragment?,
                srcUri: Uri?,
                destinationUri: Uri?,
                dataSource: java.util.ArrayList<String?>?,
                requestCode: Int
            ) {
                if (srcUri == null
                    || destinationUri == null
                    || dataSource == null
                    || fragment == null) {
                    return
                }
                val uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                 uCrop.setImageEngine(GlideUCropEngine.createGlideEngine());
                 val option = UCrop.Options()
                 option.setStatusBarColor(Color.BLACK)
                 option.setToolbarColor(Color.BLACK)
                 option.setToolbarWidgetColor(Color.WHITE)
                 option.setShowCropGrid(false)
                 option.setHideBottomControls(true)
                 option.withAspectRatio(512.0f, 512.0f)
                 option.withMaxResultSize(1024, 1024)
                 uCrop.withOptions(option);
                 uCrop.start(fragment.requireActivity(), fragment, requestCode);
            }

        })
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
                Luban.with(activity)
                    .load(source)
                    .ignoreBy(100).setCompressListener(
                        object : OnNewCompressListener {
                            override fun onStart() {

                            }

                            override fun onSuccess(source: String?, compressFile: File?) {
                                if (call != null) {
                                    if (compressFile != null) {
                                        if (BuildConfig.DEBUG) {
                                            LogUtils.d(
                                                logTag,
                                                "${logTag} compressFile " + source + " compress " + compressFile?.absolutePath
                                                        + " length " + compressFile!!.length() / 1024
                                            )
                                        }
                                    }
                                    call.onCallback(source, compressFile?.absolutePath);
                                }
                            }

                            override fun onError(source: String?, e: Throwable?) {
                                if (BuildConfig.DEBUG) {
                                    LogUtils.e(
                                        logTag,
                                        "${logTag} compressFile onError ${e?.message}" + source
                                    )
                                }
                                call?.onCallback(source, null)
                            }
                        }
                    ).launch();
            }

        })
        .setMaxSelectNum(1)
        .forResult(object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: ArrayList<LocalMedia?>?) {
                if (result.isNullOrEmpty()) {
                    return;
                }
                val images = mutableListOf<String>();
                result.forEach {
                    val localMedia = it;
                    if (localMedia == null) {
                        return@forEach;
                    }
                    val filePath = localMedia.compressPath ?: localMedia.realPath;

                    if(BuildConfig.DEBUG) {
                        Log.e(logTag, "${logTag} photo selector  ${filePath} ${GsonUtils.toJson(localMedia)}")
                    }

                    if (filePath.isNullOrEmpty()) {
                        return@forEach;
                    }
                    images.add(filePath)
                }
                onPickerImageCallback(images)
            }

            override fun onCancel() {
            }
        })
}


fun Activity.requestLocationPermission(
    grantAction: (() -> Unit)? = null,
    denyAction: (() -> Unit)? = null
) {
    XXPermissions
        .with(this)
        .permissions(
            listOf<IPermission>(
                PermissionLists.getAccessFineLocationPermission(),
                PermissionLists.getAccessCoarseLocationPermission(),
            )
        ).request(object : OnPermissionCallback {
            override fun onGranted(
                permissions: List<IPermission?>,
                allGranted: Boolean
            ) {
                grantAction?.invoke()
            }

            override fun onDenied(
                permissions: List<IPermission?>,
                doNotAskAgain: Boolean) {
                if (permissions.size == 2) { //全部deny才处理
                    denyAction?.invoke()
                }
            }
        })
}


fun Activity.requestLocation(locationCallback: OnLocationCallback) {
    if ((System.currentTimeMillis() - lastCacheLocation.time) <= OnceLocationHelper.locationCacheTime) {
        locationCallback.onLocation(lastCacheLocation)
        return
    }
    val onceLocationHelper = OnceLocationHelper();
    onceLocationHelper.setOnLocationCallback(locationCallback)
    requestLocationPermission(
        grantAction = {
            onceLocationHelper.getLocation(this)
        },
        denyAction = {
            locationCallback.onLocation(OnceLocationHelper.createErrorLocation())
        }
    )
}

fun Context.throttleToast(msg:String?) {
    if (!TextUtils.isEmpty(msg)) {
        msg?.let {
            ToastExt.throttleToast(msg, {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            })
        }
    }
}

fun Fragment.throttleToast(msg:String?) {
    if (!TextUtils.isEmpty(msg)) {
        msg?.let {
            ToastExt.throttleToast(msg, {
                Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
            })
        }
    }
}


fun Activity.showAppForgeUpdateDialog(appUpdateInfo: AppUpdateInfo?) {
    if(TextUtils.isEmpty(appUpdateInfo?.address)) {
        return
    }
    val dialog = AppUpdateConfirmDialog(
        mContext = this,
        title = "强制更新",
        message = HtmlCompat.fromHtml(appUpdateInfo?.describe ?: "需更新APP后才能使用\n", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "去更新",
        forceUpdate = true,
        onConfirm = {
           ApkDownloadUtil.downloadByBrowser(this, appUpdateInfo?.address)
        }
    );
    dialog.showDialog(this)
}

fun Activity.showAppNormalUpdateDialog(appUpdateInfo: AppUpdateInfo?) {
    if(TextUtils.isEmpty(appUpdateInfo?.address)) {
        return
    }
    val dialog = AppUpdateConfirmDialog(
        mContext = this,
        title = "更新提示",
        message = HtmlCompat.fromHtml(appUpdateInfo?.describe ?: "更新APP体验新功能\n", HtmlCompat.FROM_HTML_MODE_LEGACY),
        confirmText = "去更新",
        cancelText = "暂不更新",
        onCancel = {
            SPUtils.getInstance().put("ignore_app_update_version", appUpdateInfo?.maxVersion ?: "");
        },
        onConfirm = {
            ApkDownloadUtil.downloadByBrowser(this, appUpdateInfo?.address)
        }
    );
    dialog.showDialog(this)
}

fun Activity.showAppUseProtocolDialogIfNeed() {
    val hasShowAppUseProtocolDialogFlag = SPUtils.getInstance().getBoolean("showAppUseProtocolDialogFlag", false)
    if(!hasShowAppUseProtocolDialogFlag) {
        showAppUseProtocolDialog {
            SPUtils.getInstance().put("showAppUseProtocolDialogFlag", true)
            EventBusUtils.postEvent(HideSplashEvent(true))
        }
    }
}

fun Activity.hasShowAppUseProtocolDialog(): Boolean {
    val hasShowAppUseProtocolDialogFlag = SPUtils.getInstance().getBoolean("showAppUseProtocolDialogFlag", false)
    return hasShowAppUseProtocolDialogFlag;
}


fun Activity.showAppUseProtocolDialog(onConfirm: OnClickListener? = null) {
    val activity = this;
    val dialog = ProtocolConfirmDialog(
        mContext = this,
        title = "欢迎使用拼华夏",
        canceledOnTouchOutside = false,
        setMessageTextView = { it, protocolDialog ->
            it.movementMethod = NoSelectLinkMovementMethod();
            SpanUtils.with(it)
                .append("我已阅读并同意")
                .append("《用户协议》").setForegroundColor(Color.parseColor("#557EF7"))
                .setClickSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openLinkInSystemBrowser(Constant.USER_TERM_URL)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.linkColor = Color.parseColor("#557EF7")
                        ds.isUnderlineText = false
                    }
                })
                .append("和")
                .append("《隐私政策》").setForegroundColor(Color.parseColor("#557EF7")).setUnderline()
                .setClickSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openLinkInSystemBrowser(Constant.PRIVATE_TERM_URL)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.linkColor = Color.parseColor("#557EF7")
                        ds.isUnderlineText = false
                    }
                })
                .create()
        },
        cancelText = "不同意",
        onCancel = {
            AppUtils.exitApp()
        },
        confirmText = "同意并继续",
        onConfirm = onConfirm
    );
    dialog.showDialog(this)
}

fun Activity.openLinkInSystemBrowser(downloadUrl:String?) {
    if (downloadUrl.isNullOrEmpty()) {
        ToastUtils.showShort("参数错误")
        return
    }
    if (!downloadUrl.startsWith("http://", ignoreCase = true)
        && !downloadUrl.startsWith("https://", ignoreCase = true)) {
        ToastUtils.showShort("链接必须是http/https协议")
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val context = this;
        context.startActivity(intent)
    } catch (e: Exception) {
        ToastUtils.showShort("打开失败：${e.message ?: "未知错误"}")
    }
}


fun Context.goPayPurchaseOrderActivity(purchaseOrderId: Long?) {
    purchaseOrderId?.let {
        val intent = Intent(this, PayPurchaseOrderDetailActivity::class.java)
        intent.putExtra("purchaseOrderId", purchaseOrderId)
        startActivity(intent)
    }

}


fun Activity.showConfirmPayPurchaseBottomSheet( confirmAction: View.OnClickListener? = null,
                                                inFullScreenActivity: Boolean = true,
                                                purchaseOrderDetail: PurchaseOrderDetail? = null,) {
    val view = findViewById<View>(R.id.bottomSheetAnchorView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(PurchaseOrderConfirmPayBottomSheet(this,
                  confirmAction = confirmAction,
                  inFullScreenActivity = inFullScreenActivity,
                  purchaseOrderDetail = purchaseOrderDetail
            )
        )
        .show()
}



fun Context.goConfirmReceiptOrderActivity(orderItem: OrderItem) {
    autoGoFeedbackWhenDoneConfirmReceiveAction = null;
    BizPermissionUtil.ensureOrderManagePermission {
        val intent = Intent(this, ConfirmReceiptOrderActivity::class.java)
        intent.putExtra("orderId", orderItem.id)
        ShareParamDataUtils.orderItem = orderItem
        startActivity(intent)
    }
}


fun Activity.showHeaderPhotoPermissionDialog(action: () -> Unit) {
    if (XXPermissions.isGrantedPermission(this, PermissionLists.getReadMediaImagesPermission())) {
        action.invoke()
    } else {
        val permissions = mutableListOf<IPermission>()
        permissions.add(PermissionLists.getReadMediaImagesPermission()) //相册权限必须
        permissions.add(PermissionLists.getCameraPermission())
        val dialog = PermissionConfirmDialog(
            mContext = this,
            title = "权限申请说明",
            requestPermissions = permissions,
            confirmText = "去申请",
            onConfirm = {
                action.invoke()
            },
            message = HtmlCompat.fromHtml("拼华夏App需要申请相册，存储及相机权限。你可以查看并选择相册里的图片,上传照片，以设置头像、保存应用内的图片至相册等。你还可以在其他场景访问设备里的照片视频和文件，以及保存内容到设备。", HtmlCompat.FROM_HTML_MODE_LEGACY),
        );
        dialog.showDialog(this)
    }
}



fun Activity.showConfirmReceiptNoPayBottomSheet(confirmAction: View.OnClickListener? = null,
                                                inFullScreenActivity: Boolean = false) {
    val view = findViewById<View>(R.id.bottomSheetAnchorView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(ConfirmReceiptNoPayBottomSheet(this,
            confirmAction = confirmAction,
            inFullScreenActivity = inFullScreenActivity
        )
        )
        .show()
}

fun Activity.showConfirmReceiptPayBottomSheet(confirmAction: View.OnClickListener? = null,
                                              inFullScreenActivity: Boolean = false,
                                              orderDetailItem: OrderDetailItem? = null) {
    val view = findViewById<View>(R.id.bottomSheetAnchorView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(ConfirmReceiptPayBottomSheet(this,
            confirmAction = confirmAction,
            inFullScreenActivity = inFullScreenActivity,
            orderDetailItem = orderDetailItem
        )
        )
        .show()
}


fun Activity.autoShowConfirmReceiptBottomSheet(confirmAction: View.OnClickListener? = null,
                                               orderDetailItem: OrderDetailItem? = null,
                                               inFullScreenActivity: Boolean = false) {
    val payPrice = orderDetailItem?.extraPrice?.toDoubleOrNull() ?: 0.0;
    if (payPrice > 0) {
        showConfirmReceiptPayBottomSheet(
            confirmAction = confirmAction,
            inFullScreenActivity = inFullScreenActivity,
            orderDetailItem = orderDetailItem
        )
    } else {
        showConfirmReceiptNoPayBottomSheet(
            confirmAction = confirmAction,
            inFullScreenActivity = inFullScreenActivity
        )
    }
}






