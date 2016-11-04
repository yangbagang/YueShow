package com.ybg.yxym.yueshow.pay

import com.google.gson.annotations.SerializedName

/**
 * @author Jax
 * *
 * @version V1.0.0
 * *
 * @Created on 2015/10/23 18:01.
 */
class PayParams {

    /**
     * prepayid : wx20151023175649e2485a44dd0286253307
     * package : Sign=WXPay
     * noncestr : 9VUxelEBDMYpvh7m
     * timestamp : 1445594209
     * sign : 9e1e08d353bc147c43102416eedcc282
     * retcode : SUCCESS
     * out_trade_no : 1247019301201510231756497
     */

    var prepayid: String? = null
    @SerializedName("package")
    var `package`: String? = null
    var noncestr: String? = null
    var timestamp: String? = null
    var sign: String? = null
    var retcode: String? = null
    var order_name: String? = null
    var total_fee: String? = null
    var out_trade_no: String? = null

    var lucky_number: String? = null//活动部分的幸运号
}
