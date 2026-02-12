package com.zhongpin.mvvm_android.ui.private

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.core.text.HtmlCompat
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.DialogPrivateTipBinding
import com.zhongpin.mvvm_android.ui.web.WebActivity

class PrivateTipDialog(
    val mContext:Context,
    val cancelListener: OnClickListener? = null,
    val confirmListener: OnClickListener? = null,
) : Dialog(mContext, R.style.AlertDialogStyle) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = DialogPrivateTipBinding.inflate(LayoutInflater.from(mContext))
        setContentView(mBinding.root)
        val privacyText = "<h3><font color='#57C248'>隐私政策</font></h3>" +
                "<p>本应用尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，本应用会按照本隐私政策的规定使用和披露您的个人信息。但本应用将以高度的勤勉、审慎义务对待这些信息。除本隐私政策另有规定外，在未征得您事先许可的情况下，本应用不会将这些信息对外披露或向第三方提供。本应用会不时更新本隐私政策。 您在同意本应用服务使用协议之时，即视为您已经同意本隐私政策全部内容。本隐私政策属于本应用服务使用协议不可分割的一部分。</p>" +
                "<h4>1. 适用范围</h4>" +
                "<p>(a) 在您注册本应用帐号时，您根据本应用要求提供的个人注册信息；</p>" +
                "<p>(b) 在您使用本应用网络服务，或访问本应用平台网页时，本应用自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据；</p>" +
                "<p>(c) 本应用通过合法途径从商业伙伴处取得的用户个人数据。</p>" +
                "<h4>2. 信息使用</h4>" +
                "<p>(a) 本应用不会向任何无关第三方提供、出售、出租、分享或交易您的个人信息，除非事先得到您的许可，或该第三方和本应用（含本应用关联公司）单独或共同为您提供服务，且在该服务结束后，其将被禁止访问包括其以前能够访问的所有这些资料。</p>" +
                "<p>(b) 本应用亦不允许任何第三方以任何手段收集、编辑、出售或者无偿传播您的个人信息。任何本应用平台用户如从事上述活动，一经发现，本应用有权立即终止与该用户的服务协议。</p>" +
                "<p>(c) 为服务用户的目的，本应用可能通过使用您的个人信息，向您提供您感兴趣的信息，包括但不限于向您发出产品和服务信息，或者与本应用合作伙伴共享信息以便他们向您发送有关其产品和服务的信息（后者需要您的事先同意）。</p>" +
                "<h4>3. 收集的信息</h4>" +
                "<p>(a) 与您的设备或SIM卡相关的信息： 例如IMEI/OAID、GAID编号、IMSI编号、MAC地址、序列号、系统版本及类型、ROM版本、Android版本、Android ID、Space ID、SIM卡运营商和归属地、屏幕显示信息、设备输入信息、设备激活时间、设备制造商信息和型号名称、网络运营商、连接类型、硬件基础配置信息、销售渠道及使用相关信息（例如CPU、内存、电量使用情况、设备分辨率、设备温度、相机镜头型号、亮屏次数和解锁次数）。</p>" +
                "<p>(b) 第三方服务提供商与业务合作伙伴指定的与您相关的信息： 我们可能收集并使用如第三方服务提供商与业务合作伙伴分配的广告ID。</p>" +
                "<p>(c) 与您的应用程序使用相关的信息： 包括应用内唯一标识符（例如VAID号、OAID、AAID号、Instance ID），应用基础信息，例如应用ID信息、SDK版本、系统更新设置、应用设置（地区、语言、时区、字体），应用进入/退出前台的时间，以及应用状态记录（例如下载、安装、更新、删除）。</p>"




        mBinding.privacyContent.text = HtmlCompat.fromHtml(privacyText, HtmlCompat.FROM_HTML_MODE_LEGACY)


        mBinding.ivTitle.setOnClickListener {
            val intent = Intent(mContext, WebActivity::class.java)
            intent.putExtra("title","用户服务协议")
            intent.putExtra("url","https://www.baidu.com")
            mContext.startActivity(intent)
        }

        mBinding.privacyContent.setOnClickListener {
            val intent = Intent(mContext, WebActivity::class.java)
            intent.putExtra("title","用户服务协议")
            intent.putExtra("url","https://www.baidu.com")
            mContext.startActivity(intent)
        }

        mBinding.disAgree.setOnClickListener {
            dismiss()
            cancelListener?.onClick(mBinding.disAgree)
        }

        mBinding.agree.setOnClickListener {
            dismiss()
            confirmListener?.onClick(mBinding.agree)
        }
    }
}