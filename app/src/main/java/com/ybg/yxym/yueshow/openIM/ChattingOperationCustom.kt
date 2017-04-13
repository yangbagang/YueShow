package com.ybg.yxym.yueshow.openIM

import android.support.v4.app.Fragment
import android.text.TextUtils
import com.alibaba.mobileim.aop.Pointcut
import com.alibaba.mobileim.aop.custom.IMChattingPageOperateion
import com.alibaba.mobileim.aop.model.YWChattingPlugin
import com.alibaba.mobileim.conversation.YWConversation
import com.alibaba.mobileim.conversation.YWMessage
import com.alibaba.mobileim.conversation.YWMessageChannel
import org.json.JSONObject

/**
 * 聊天界面(单聊和群聊界面)的定制点(根据需要实现相应的接口来达到自定义聊天界面)，不设置则使用openIM默认的实现
 * 1.CustomChattingTitleAdvice 自定义聊天窗口标题 2. OnUrlClickChattingAdvice 自定义聊天窗口中
 * 当消息是url是点击的回调。用于isv处理url的打开处理。不处理则用第三方浏览器打开 如果需要定制更多功能，需要实现更多开放的接口
 * 需要.继承BaseAdvice .实现相应的接口
 *
 *
 * 另外需要在Application中绑定
 * AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_POINTCUT,
 * ChattingOperationCustomSample.class);

 * @author jing.huai
 */
class ChattingOperationCustom// 默认写法
(pointcut: Pointcut) : IMChattingPageOperateion(pointcut) {

    internal var mIMKit = LoginHelper.instance.imKit!!

    object CustomMessageType {
        val GREETING = "Greeting"
        val CARD = "CallingCard"
        val IMAGE = "PrivateImage"
        val READ_STATUS = "PrivateImageRecvRead"
    }

    /**
     * 是否使用听筒模式播放语音消息

     * @param fragment
     * *
     * @param message
     * *
     * @return true：使用听筒模式， false：使用扬声器模式
     */
    override fun useInCallMode(fragment: Fragment?, message: YWMessage?): Boolean {
        return mUserInCallMode
    }

    /***************** 以下是定制自定义消息view的示例代码  */

    //自定义消息view的种类数
    private val typeCount = 4

    /** 自定义viewType，viewType的值必须从0开始，然后依次+1递增，且viewType的个数必须等于typeCount，切记切记！！！ */
    //地理位置消息
    private val type_0 = 0

    //群自定义消息(Say-Hi消息)
    private val type_1 = 1

    //单聊自定义消息(名片消息)
    private val type_2 = 2

    //单聊阅后即焚消息
    private val type_3 = 3


    /**
     * 自定义消息view的种类数
     * @return  自定义消息view的种类数
     */
    override fun getCustomViewTypeCount(): Int {
        return typeCount
    }

    /**
     * 自定义消息view的类型，开发者可以根据自己的需求定制多种自定义消息view，这里需要根据消息返回view的类型
     * @param message 需要自定义显示的消息
     * *
     * @return  自定义消息view的类型
     */
    override fun getCustomViewType(message: YWMessage?): Int {
        if (message!!.subType == YWMessage.SUB_MSG_TYPE.IM_GEO) {
            //            return type_0;
            return super.getCustomViewType(message)
        } else if (message.subType == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || message.subType == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS) {
            var msgType: String? = null
            try {
                val content = message.messageBody.content
                val `object` = JSONObject(content)
                msgType = `object`.getString("customizeMessageType")
            } catch (e: Exception) {

            }

            if (!TextUtils.isEmpty(msgType)) {
                if (msgType == CustomMessageType.GREETING) {
                    return type_1
                } else if (msgType == CustomMessageType.CARD) {
                    return type_2
                } else if (msgType == CustomMessageType.IMAGE) {
                    return type_3
                }
            }
        }
        return super.getCustomViewType(message)
    }

    /**
     * 是否需要隐藏头像
     * @param viewType 自定义view类型
     * *
     * @return true: 隐藏头像  false：不隐藏头像
     */
    override fun needHideHead(viewType: Int): Boolean {
        if (viewType == type_2) {
            return true
        }
        return super.needHideHead(viewType)
    }

    /**
     * 是否需要隐藏显示名
     * @param viewType 自定义view类型
     * *
     * @return true: 隐藏显示名  false：不隐藏显示名
     */
    override fun needHideName(viewType: Int): Boolean {
        if (viewType == type_2) {
            return true
        }
        return super.needHideName(viewType)
    }


    companion object {

        private val TAG = "ChattingOperationCustom"

        var count = 1


        /**
         * 发送单聊地理位置消息
         */
        fun sendGeoMessage(conversation: YWConversation) {
            conversation.messageSender.sendMessage(
                    YWMessageChannel.createGeoMessage(30.2743790000,
                            120.1422530000, "浙江省杭州市西湖区"), 120, null)
        }

        /**
         * 开发者可以根据用户操作设置该值
         */
        private var mUserInCallMode = false

        //不要使用60000之前的值，防止和SDK中使用的产生冲突
        private val CAMERA_WITH_DATA = 60001
        private val PHOTO_PICKED_WITH_DATA = 60002
        val IMAGE_CAMERA_WITH_DATA = 60003
        private val IMAGE_PHOTO_PICKED_WITH_DATA = 60004


        /**
         * 请注意不要和内部的ID重合
         * [YWChattingPlugin.ReplyBarItem.ID_CAMERA]
         * [YWChattingPlugin.ReplyBarItem.ID_ALBUM]
         * [YWChattingPlugin.ReplyBarItem.ID_SHORT_VIDEO]
         */
        private val ITEM_ID_1 = 0x1
        private val ITEM_ID_2 = 0x2
        private val ITEM_ID_3 = 0X3

        private var mConversation: YWConversation? = null


        private var compiledShortVideoLibrary = false
        private var haveCheckedShortVideoLibrary = false
    }
}