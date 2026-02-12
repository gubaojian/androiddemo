package com.zhongpin.mvvm_android.network

import com.zhongpin.mvvm_android.bean.*
import com.zhongpin.mvvm_android.common.utils.Constant
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/***
 * 数据url：https://mockapi.eolink.com/JiPqtefd9325e3466dc720a8c0ad3364c4f791e227debcd/banner/json
 * */
interface ApiService {


    @GET(USER_INFO)
    @Deprecated("已废弃")
    suspend fun getUserInfoCo(): BaseResponse<List<UserInfoResponse>>

    @GET(USER_INFO)
    suspend fun getUserInfo(): BaseResponse<UserInfoResponse>

    @POST(UPDATE_USER_INFO)
    suspend fun updateUserInfo(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    //`type` int NOT NULL DEFAULT '2' COMMENT '工厂类型(0:一级厂,1:二级厂,2:三级厂)'
    @POST(LOGIN)
    suspend fun loginCo(@Body parameters:HashMap<String,Any>): BaseResponse<LoginResponse>

    @POST(REGISTER)
    suspend fun registerCo(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    /**
     *      pcRegister(0, "pcRegister", "pc账号注册"),------>前台账号注册(手机端)
     *
     *     pcLogin(1, "pcLogin", "pc账号登录"),------>前台登录(手机端)
     *
     *     pcResetPassword(2,"pcResetPassword","pc重置密码"), ------>前台重置密码(手机端)
     *
     *     mngResetPassword(3,"mngResetPassword","mng重置密码"), ------>后台重置密码
     *
     *     pcUpdateMobile(4,"pcUpdateMobile","替换手机号"), ------>前台换手机号(手机端)
     *
     *     mngLogin(5,"mngLogin","mng账号登录"); ------>后台登录
     *
     *
     * */
    @POST(SEND_VERIFY_CODE)
    suspend fun sendVerifyCo(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(RESET_PASSWORD)
    suspend fun resetPassword(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @Multipart
    @POST(IDENTIFY_ID_CARD)
    suspend fun identifyIdCard(@Part file: MultipartBody.Part): BaseResponse<IdCardInfoResponse>


    @Multipart
    @POST(UPLOAD_IMAGE)
    suspend fun uploadImage(@Part file: MultipartBody.Part): BaseResponse<String>


    @POST(SUBMIT_USER_INFO_AUTH)
    suspend fun submitUserInfoAuth(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(SUBMIT_ENT_INFO_AUTH)
    suspend fun submitEntInfoAuth(@Body parameters:HashMap<String,Any>): BaseResponse<Long>


    @POST(EDIT_ENT_INFO_AUTH)
    suspend fun editEntInfoAuth(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(UPDATE_ENT_CONTRACT)
    suspend fun updateEntContract(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(EDIT_ENT_SIGN_AUTH)
    suspend fun signEntInfoAuth(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @GET(DELETE_ENT_INFO_AUTH)
    suspend fun deleteEntInfoAuth(@Query("id") id: Long): BaseResponse<Boolean>


    @GET(SELECT_ADDRESS_POI_INFO)
    suspend fun getLntLngInfo(@Query("address") address: String): BaseResponse<LatLntResponse>


    @Multipart
    @POST(ENT_INFO_IDENTIFY)
    suspend fun identifyEntCard(@Part file: MultipartBody.Part): BaseResponse<EntInfoResponse>


    @POST(COMPANY_LIST)
    suspend fun getCompanyList(@Body parameters:HashMap<String,Any>): BaseResponse<CompanyListResponse>

    @GET(QUERY_COMPANY_INTO)
    suspend fun getCompanyInfo(): BaseResponse<CompanyListItemResponse>

    @GET(USER_AUTH_INFO)
    suspend fun getUserAuthInfo(): BaseResponse<UserInfoAuthResponse>

    @Deprecated(ADDRESS_LIST)
    @GET(ADDRESS_LIST)
    suspend fun getEntReceiveAddressList(@Query("entId") entId: Long): BaseResponse<List<AddressListItemResponse>>

    @GET(ADDRESS_LIST)
    suspend fun getReceiveAddressList(): BaseResponse<List<AddressListItemResponse>>

    @POST(ADD_RECEIVE_ADDRESS)
    suspend fun addReceiveAddress(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(UPDATE_RECEIVE_ADDRESS)
    suspend fun updateReceiveAddress(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(DELETE_RECEIVE_ADDRESS)
    suspend fun deleteReceiveAddress(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(ORDER_PREVIEW)
    suspend fun orderPreview(@Body parameters:HashMap<String,Any>): BaseResponse<PreviewOrderResponse>


    @POST(CREATE_ORDER)
    suspend fun createOrder(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(CREATE_ORDER_V6)
    suspend fun createOrderV6(@Body parameters:HashMap<String,Any>): BaseResponse<PurchaseOrderDetail>


    @POST(MATERIAL_PRICE_LIST)
    suspend fun getMaterialPriceList(@Body parameters:HashMap<String,Any>): BaseResponse<MaterialPriceListResponse>


    @POST(MATERIAL_PRICE_DETAIL)
    suspend fun getMaterialPriceDetail(@Body parameters:HashMap<String,Any>): BaseResponse<MaterialPriceDetailItem>

    @POST(PLATFORM_MATERIAL_LIST)
    suspend fun getPlatformMaterialList(@Body parameters:HashMap<String,Any>): BaseResponse<PlatformMaterialListResponse>


    @POST(CALCULATE_BOX_CONVERT)
    suspend fun calculateBoxConvert(@Body parameters:HashMap<String,Any>): BaseResponse<BoxConvertResponse>


    @POST(HISTORY_ORDER_LIST)
    suspend fun getHisOrderList(@Body parameters:HashMap<String,Any>): BaseResponse<HistoryOrderListResponse>

    @POST(SELECT_HISTORY_ORDER_LIST)
    suspend fun getSelectHisOrderList(@Body parameters:HashMap<String,Any>): BaseResponse<SelectHistoryOrderListResponse>


    @POST(CHECK_MATERIAL)
    suspend fun checkMaterial(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(LEN_TYPE_CONFIG)
    suspend fun getLenTypeConfig (): BaseResponse<List<LenTypeConfigItem>>

    @POST(BOX_TYPE_CONFIG)
    suspend fun getBoxTypeConfig (): BaseResponse<List<BoxTypeConfigItem>>

    @POST(ORDER_LIST)
    suspend fun getOrderList(@Body parameters:HashMap<String,Any>): BaseResponse<OrderListResponse>

    @POST(ORDER_DETAIL)
    suspend fun getOrderDetail(@Body parameters:HashMap<String,Any>): BaseResponse<OrderDetailItem>

    @POST(PURCHASE_ORDER_DETAIL)
    suspend fun getPurchaseOrderDetail(@Body parameters:HashMap<String,Any>): BaseResponse<PurchaseOrderDetail>


    @POST(CONFIRM_ORDER_RECEIVE)
    suspend fun confirmOrderReceiveDone(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(CANCEL_ORDER)
    suspend fun cancelOrder(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>



    @GET(LOGIN_OUT)
    suspend fun loginOut(@Query("token") token: String): BaseResponse<Boolean>

    @POST(ORDER_DELIVERY_PROF_LIST)
    suspend fun getOrderDeliveryProofList(@Body parameters:HashMap<String, Any>): BaseResponse<OrderDeliveryProofListResponse>



    @POST(ORDER_FEEDBACK_LIST)
    suspend fun getOrderFeedbackList(@Body parameters:HashMap<String,Any>): BaseResponse<OrderFeedbackListResponse>

    @POST(ORDER_FEEDBACK_DETAIL)
    suspend fun getOrderFeedbackDetail(@Body parameters:HashMap<String,Any>): BaseResponse<OrderFeedbackItem>


    @POST(ADD_ORDER_FEEDBACK )
    suspend fun addOrderFeedback(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(EDIT_ORDER_FEEDBACK)
    suspend fun editOrderFeedback(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(CANCEL_ORDER_FEEDBACK)
    suspend fun cancelOrderFeedback(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @GET(ADD_PAY_CHARGE_INPUT)
    suspend fun addChargeInput(@Query("amount") amount: String): BaseResponse<PayItem>

    @POST(GET_PAY_URL)
    suspend fun getPayUrl(@Body parameters:HashMap<String,Any>): BaseResponse<PayUrlItem>


    @GET(GET_PAY_STATUS)
    suspend fun getPayStatus(@Query("id") id: Long): BaseResponse<PayOrderStatus>

    @GET(CANCEL_PAY_CHARGE_INPUT)
    suspend fun cancelPayItem(@Query("id") id: Long): BaseResponse<Boolean>

    @POST(PAY_LIST)
    suspend fun getPayList(@Body parameters:HashMap<String,Any>): BaseResponse<PayItemListResponse>

    @GET(WAIT_PAY_LIST)
    suspend fun getWaitPayList(): BaseResponse<List<PayItem>>


    @GET(PAY_ACCOUNT_BALANCE)
    suspend fun getPayAccountBalance(): BaseResponse<PayAccountBalance>


    @POST(MEMBER_LIST)
    suspend fun getCompanyMemberList(@Body parameters:HashMap<String,Any>): BaseResponse<MemberListResponse>


    @GET(ROLE_LIST)
    suspend fun getCompanyRoleList(): BaseResponse<List<RoleItem>>

    @POST(ADD_MEMBER)
    suspend fun addCompanyMember(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(EDIT_MEMBER)
    suspend fun editCompanyMember(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(REMOVE_MEMBER)
    suspend fun removeCompanyMember(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    @POST(PRICE_TIP_CONFIG)
    suspend fun getPriceTipResponse(): BaseResponse<PriceTipResponse>

    @POST(ORDER_STATIC_DATA)
    suspend fun getOrderStatisticsData(@Body parameters:HashMap<String,Any>): BaseResponse<OrderStatisticsData>

    //应用类型(0:安卓,1:ios)
    @GET(GET_APP_UPDATE_INFO)
    suspend fun getAppUpdateInfo(@Query("appType") appType: String): BaseResponse<AppUpdateInfo>


    @POST(PURCHASE_ORDER_LIST)
    suspend fun getPurchaseOrderList(@Body parameters:HashMap<String,Any>): BaseResponse<PurchaseOrderListResponse>


    @POST(CANCEL_PURCHASE_ORDER)
    suspend fun cancelPurchaseOrder(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>

    @POST(PAY_PURCHASE_ORDER)
    suspend fun payPurchaseOrder(@Body parameters:HashMap<String,Any>): BaseResponse<Boolean>


    companion object {
        const val LOGIN = "/mini/login"

        const val LOGIN_OUT = "/mini/logout"
        const val REGISTER = "/mini/register"
        const val SEND_VERIFY_CODE = "/mini/sendCode"
        const val RESET_PASSWORD = "/mini/resetPassword"
        const val USER_INFO = "/mini/selectUserInfo"

        const val UPDATE_USER_INFO = "/mini/updateUserInfo"

        const val IDENTIFY_ID_CARD ="/mini/identifyIdCard"

        const val UPLOAD_IMAGE  ="/mini/uploadImage"

        const val SUBMIT_USER_INFO_AUTH = "/mini/submitUserInfoAuth"

        //提交企业认证
        const val SUBMIT_ENT_INFO_AUTH = "/mini/addCompanyInfo"

        const val EDIT_ENT_INFO_AUTH = "/mini/updateCompanyInfo"

        const val UPDATE_ENT_CONTRACT = "/mini/updateContact"

        const val EDIT_ENT_SIGN_AUTH = "/mini/signCompanyInfo"


        const val DELETE_ENT_INFO_AUTH = "/mini/deleteEntInfoAuth"


        //获取经纬度
        const val SELECT_ADDRESS_POI_INFO = "/mini/selectExactInfo"

        const val ENT_INFO_IDENTIFY = "/mini/identify"


        const val COMPANY_LIST = "/mini/selectEntInfoAuthPage"

        const val QUERY_COMPANY_INTO  = "/mini/selectCompanyInfo";


        const val ADDRESS_LIST = "/mini/selectRevAddrList"

        const val ORDER_PREVIEW = "/mini/purchase/orderScan"

        const val CREATE_ORDER = "/mini/purchase/confirmOrder"

        const val CREATE_ORDER_V6 = "/mini/purchase/confirmOrderNew"



        const val ORDER_LIST = "/mini/order/orderList"

        const val ORDER_DETAIL = "/mini/order/orderDetail"

        const val CONFIRM_ORDER_RECEIVE = "/mini/order/updateSignInfo"

        const val CANCEL_ORDER = "/mini/order/cancel"

        const val ADD_ORDER_FEEDBACK  = "/mini/appeal/saveAppeal"

        const val EDIT_ORDER_FEEDBACK = "/mini/appeal/updateAppeal"

        const val CANCEL_ORDER_FEEDBACK = "/mini/appeal/deleteAppeal"

        const val ORDER_FEEDBACK_DETAIL = "/mini/appeal/detail"

        const val ORDER_FEEDBACK_LIST = "/mini/appeal/appealList"


        const val ORDER_DELIVERY_PROF_LIST = "/mini/delivery/deliveryList"

        const val ADD_RECEIVE_ADDRESS = "/mini/addRevAddr"

        const val UPDATE_RECEIVE_ADDRESS = "/mini/updateRevAddr"

        const val DELETE_RECEIVE_ADDRESS = "/mini/deleteRevAddr"

        const val MATERIAL_PRICE_LIST = "/mini/material/priceList";

        const val MATERIAL_PRICE_DETAIL = "/mini/material/priceDetail";

        const val PLATFORM_MATERIAL_LIST = "/mini/material/list";

        const val CALCULATE_BOX_CONVERT = "/mini/calculate/boxConvert"

        const val HISTORY_ORDER_LIST = "/mini/order/hisOrderList"

        const val SELECT_HISTORY_ORDER_LIST = "/mini/order/selectHisOrder"

        const val CHECK_MATERIAL = "/mini/material/check"

        const val LEN_TYPE_CONFIG = "/mini/material/lenTypeConfig"

        const val BOX_TYPE_CONFIG = "/mini/material/boxTypeConfig"

        const val USER_AUTH_INFO = "/mini/selectUserInfoAuth"

        const val ADD_PAY_CHARGE_INPUT  = "/mini/addCompanyAccountOrder"

        const val GET_PAY_URL  = "/mini/toPay"

        const val GET_PAY_STATUS  = "/mini/selectPayStatus"

        const val CANCEL_PAY_CHARGE_INPUT  = "/mini/cancelPay"

        const val PAY_ACCOUNT_BALANCE = "/mini/selectCompanyAccount"


        const val PAY_LIST = "/mini/selectCompanyAccountOrderPage"

        const val WAIT_PAY_LIST = "/mini/selectCompanyAccountOrderList"

        const val MEMBER_LIST = "/mini/selectUserInfoPage"

        const val ROLE_LIST = "/mini/selectRoleList"

        const val ADD_MEMBER = "/mini/addMember"

        const val EDIT_MEMBER = "/mini/updateMember"

        const val REMOVE_MEMBER = "/mini/deleteMember"

        const val PRICE_TIP_CONFIG = "/mini/config/orderPrice"


        const val ORDER_STATIC_DATA = "/mini/order/orderStatistic"

        const val PURCHASE_ORDER_DETAIL  = "/mini/purchase/orderDetail"

        const val GET_APP_UPDATE_INFO = "/mini/selectAppVersion"


        const val PURCHASE_ORDER_LIST = "/mini/purchase/orderList"

        const val CANCEL_PURCHASE_ORDER = "/mini/purchase/cancelOrder"

        const val PAY_PURCHASE_ORDER = "/mini/purchase/payOrder"




        const val COMMON_KEY = Constant.COMMON_KEY

    }

}