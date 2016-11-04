package com.ybg.yxym.yueshow.pay.alipay

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.widget.Toast

import com.alipay.sdk.app.PayTask
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.pay.BasePayUtils
import com.ybg.yxym.yueshow.pay.Constants
import com.ybg.yxym.yueshow.pay.PayParams
import com.ybg.yxym.yueshow.utils.ToastUtil

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

/**
 * 支付宝支付类
 * notify_url 要求后台给出
 * 使用时注意lib 文件用最新的否则可能出现不能直接调用的情况(加载的网页支付内容空白)
 * 使用时只需配置参数:
 * 1.商户PID 合作伙伴身份（PID）--PARTNER
 * 2.商户收款账号 --SELLER
 * 3.商户私钥，pkcs8格式(一般要求生成3个密钥,后台使用一个,前端只需要使用pkcs8格式的--去空格和--begin--及--end--部分) --RSA_PRIVATE
 */
class AliPayUtil(mActivity: Activity) : BasePayUtils() {

    private var mNotifyUrl = WALLET_NOTIFY_URL

    private val mHandler: Handler

    private val mCallback = Handler.Callback { message ->
        when (message.what) {
            SDK_PAY_FLAG -> {
                val payResult = PayResult(message.obj as String)
                //打印用户操作结果
                if (!TextUtils.isEmpty(payResult.memo)) {
                    ToastUtil.toast(payResult.memo!!)
                }
                val resultStatus = payResult.resultStatus
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtil.toast("支付成功")
                    //支付成功发送广播刷新余额
                    //mActivity.sendBroadcast(new Intent(IntentExtra.Broadcast.ACTION_RECHARGE_SUCCESS));
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        ToastUtil.toast("支付结果确认中")
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        ToastUtil.toast("支付失败")
                    }
                }
            }
            else -> {
            }
        }
        false
    }

    init {
        this.mActivity = mActivity
        mHandler = Handler(mCallback)
    }

    override fun setPayType(type: Int) {
        super.setPayType(type)
        when (mPayType) {
            BasePayUtils.TYPE_ORDER -> mNotifyUrl = ACT_NOTIFY_URL
            BasePayUtils.TYPE_WALLET -> mNotifyUrl = WALLET_NOTIFY_URL
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    override fun pay(params: PayParams) {
        var outTradeNo: String = ""
        var subject: String = ""
        var body: String = ""
        var price: String = ""
        var orderInfo: String = ""
        if (Constants.PAY_DEBUG_MODE) {//测试数据
            orderInfo = getOrderInfo(outTradeNo, "测试的商品", "该测试商品的详细描述", "0.01")
        } else {
            if (params == null || TextUtils.isEmpty(params.out_trade_no) || TextUtils.isEmpty(params.order_name) || TextUtils.isEmpty(params.total_fee)) {
                ToastUtil.toast("支付信息不全，请重新选择！")
                return
            }
            outTradeNo = params.out_trade_no!!
            subject = params.order_name!!
            body = params.order_name!!
            price = params.total_fee!!
            LogUtil.d(TAG + ": " + params.out_trade_no)
            // 订单
            orderInfo = getOrderInfo(outTradeNo, subject, body, price)
        }
        if (TextUtils.isEmpty(orderInfo)) return
        // 对订单做RSA 签名
        var sign = sign(orderInfo)
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        // 完整的符合支付宝参数规范的订单信息
        val payInfo = "$orderInfo&sign=\"$sign\"&$signType"
        LogUtil.d(TAG + " " + payInfo)
        val payRunnable = Runnable {
            val alipay = PayTask(mActivity)
            // 调用支付接口，获取支付结果
            if (!TextUtils.isEmpty(payInfo)) {
                LogUtil.d(TAG + " this pay info is " + payInfo + "and  alipay is null")
                val result = alipay.pay(payInfo, true)
                if (!TextUtils.isEmpty(result)) {
                    LogUtil.d(TAG + " " + result)
                    val msg = Message()
                    msg.what = SDK_PAY_FLAG
                    msg.obj = result
                    mHandler.sendMessage(msg)
                }
            }
        }
        // 必须异步调用
        val payThread = Thread(payRunnable)
        // Thread的run方法是不抛出任何检查型异常(checked exception)的,
        // 但是它自身却可能因为一个异常而被终止，导致这个线程的终结。最麻烦的是，在线程中抛出的异常即使使用try...catch也无法截获，
        // 因此可能导致一些问题出现，比如异常的时候无法回收一些系统资源，或者没有关闭当前的连接等等。自定义的一个UncaughtExceptionHandler,
        payThread.setUncaughtExceptionHandler { thread, throwable ->
            LogUtil.d(TAG + " This is:" + thread.name + ",Message:" + throwable.message)
            throwable.printStackTrace()
        }
        payThread.start()
    }


    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    override fun checkSupportPay(): Boolean {
        return !TextUtils.isEmpty(sdkVersion)
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    val sdkVersion: String
        get() {
            val payTask = PayTask(mActivity)
            val version = payTask.version
            Toast.makeText(mActivity, version, Toast.LENGTH_SHORT).show()
            return version
        }

    /**
     * create the order info. 创建订单信息
     */
    private fun getOrderInfo(goodsOrder: String, subject: String, body: String, price: String): String {
        // 签约合作者身份ID
        var orderInfo = "partner=" + "\"" + Constants.ALIPAY_PARTNER + "\""

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.ALIPAY_SELLER + "\""

        // 商户网站唯一订单号 ,之前用的是getOutTradeNo()
        // 方法。现在根据公司需求改成SubmitOrderData.getOrder_sn()方法、
        orderInfo += "&out_trade_no=" + "\"" + goodsOrder + "\""

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\""

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\""

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\""

        // 服务器异步通知页面路径,notify_url为公司服务器的地址。

        orderInfo += "&notify_url=" + "\"" + mNotifyUrl + "\""

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\""

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\""

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\""

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\""

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\""

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo
    }


    /**
     * sign the order info. 对订单信息进行签名

     * @param content 待签名订单信息
     */
    private fun sign(content: String): String? {
        return SignUtils.sign(content, Constants.ALIPAY_RSA_PRIVATE)
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private val signType: String
        get() = "sign_type=\"RSA\""

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private val outTradeNo: String
        get() {
            val format = SimpleDateFormat("MMddHHmmss", Locale.getDefault())
            val date = Date()
            var key = format.format(date)

            val r = Random()
            key = key + r.nextInt()
            key = key.substring(0, 15)
            return key
        }

    companion object {

        private val ACT_NOTIFY_URL = "http://app.sobig.cc/alipay/new_act_notify_url.php"//订单支付的回调
        private val WALLET_NOTIFY_URL = "http://jzdtl.astu.cc/public/alipay/recharge_notify_url.php"//钱包充值回调

        private val SDK_PAY_FLAG = 1
    }
}
