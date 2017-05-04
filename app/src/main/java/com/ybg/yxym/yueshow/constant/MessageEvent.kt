package com.ybg.yxym.yueshow.constant

/**
 * Time 16/3/26 12:46.
 * Description: 处理消息仿照android.os.Message的方式来进行
 */
class MessageEvent {
    var what: Int = 0
    var obj: Any? = null

    //需要接收状态并传值的时候使用
    constructor(what: Int, obj: Any) {
        this.what = what
        this.obj = obj
    }

    //只需要接收状态不需要接收值情况下使用
    constructor(what: Int) {
        this.what = what
    }

    companion object {
        ///////////////////////////////////////////////////////////////////////////
        // 用一些常量来标识MessageEvent的类型,通过类型来进行相应处理
        ///////////////////////////////////////////////////////////////////////////
        val MESSAGE_USER_LOGIN = 0
        val MESSAGE_USER_LOGOUT = 1
        val MESSAGE_USREINFO_CHANGE = 2
        val MESSAGE_SEND_GIFT = 10
    }
}
