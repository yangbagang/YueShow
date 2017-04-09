package com.ybg.yxym.yueshow.utils

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.http.HttpUrl

import java.util.HashMap

import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import io.rong.message.ImageMessage
import io.rong.message.RichContentMessage
import io.rong.message.TextMessage
import io.rong.message.VoiceMessage


object RongCloudUtil {

    /**
     * 启动聊天

     * @param mContext   上下文
     * *
     * @param userInfoId 用户id
     * *
     * @param nickName   昵称
     * *
     * @param photo      头像
     */
    fun startPrivateChat(mContext: Context, userBase: UserBase) {
        val avatar = HttpUrl.getImageUrl(userBase.avatar)
        RongCloudUtil.setRongIMUser("${userBase.id}", userBase.nickName, avatar)
        RongIM.getInstance().refreshUserInfoCache(UserInfo("${userBase.id}", userBase.nickName, Uri.parse(avatar)))
        RongIM.getInstance().startPrivateChat(mContext, "${userBase.id}", userBase.nickName)
    }


    /**
     *
     * 连接服务器，在整个应用程序全局，只需要调用一次，需在 初始化 之后调用。
     *
     * 如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。
     * RongIM  客户端核心类的实例。
     *
     * @param rlToken 从服务端获取的用户身份令牌（Token）。融云token
     */
    fun connect(rlToken: String) {
        RongIM.connect(rlToken, object : RongIMClient.ConnectCallback() {
            /**
             * Token 错误。可以从下面两点检查
             * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
             * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
             */
            override fun onTokenIncorrect() {
                println("--Token 错误--")
            }

            /**
             * 连接融云成功
             * @param userid 当前 token 对应的用户 id
             */
            override fun onSuccess(userid: String) {
                println("--onSuccess  --  连接成功 userId =  " + userid)
            }

            /**
             * 连接融云失败
             * @param errorCode 错误码，可到官网 查看错误码对应的注释
             */
            override fun onError(errorCode: RongIMClient.ErrorCode) {
                println("--连接融云失败--" + errorCode.getMessage())
            }
        })
    }

    /**
     * 设置 融云 用户信息
     *
     * @param id      用户id
     * @param name    昵称
     * @param headUrl 用户头像
     */
    fun setRongIMUser(id: String, name: String, headUrl: String) {
        val userInfo = UserInfo(id, name, Uri.parse(headUrl))

        /**
         * 设置当前用户信息，
         * @param userInfo 当前用户信息
         */
        RongIM.getInstance().setCurrentUserInfo(userInfo)
    }


    /**
     * 发送普通消息

     * @param targetId 会话ID
     * *
     * @param msg      消息的内容
     */
    fun sendMessage(targetId: String, msg: String) {
        /**
         * 发送普通消息
         *
         * @param conversationType      会话类型
         * @param targetId              会话ID
         * @param content               消息的内容，一般是MessageContent的子类对象
         * @param pushContent           接收方离线时需要显示的push消息内容
         * @param pushData              接收方离线时需要在push消息中携带的非显示内容
         */
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, targetId,
                TextMessage.obtain(msg), msg, msg,
                object : IRongCallback.ISendMessageCallback {
                    override fun onAttached(message: Message) {
                        //消息已存储数据库。
                        println("--消息已存储数据库--")
                    }

                    override fun onSuccess(message: Message) {
                        //消息发送成功。
                        println("--发送成功--")
                    }

                    override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                        //消息发送失败。
                        println("--发送失败--" + errorCode.message)
                    }
                })
    }


    /**
     * 获取 回话列表信息

     * @return 回话列表信息
     */
    //私聊是否聚合
    //群组
    //讨论组
    //公众服务号
    //订阅号
    //系统
    val conversationList: Map<String, Boolean>
        get() {
            val map = HashMap<String, Boolean>()
            map.put(Conversation.ConversationType.PRIVATE.getName(), false)
            map.put(Conversation.ConversationType.GROUP.getName(), false)
            map.put(Conversation.ConversationType.DISCUSSION.getName(), false)
            map.put(Conversation.ConversationType.PUBLIC_SERVICE.getName(), false)
            map.put(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), false)
            map.put(Conversation.ConversationType.SYSTEM.getName(), false)
            return map
        }

    /**
     * 获取 回话列表信息

     * @return 回话列表信息
     */
    fun getConversationList(mContext: Context): Uri {
        return Uri.parse("rong://" + mContext.applicationInfo.packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")//私聊是否聚合
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公众服务号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                .build()
    }


    /**
     * 不需要用户信息
     * 回话界面

     * @param mContext          上下文
     * *
     * @param mConversationType 回话类型
     * *
     * @param mTargetId         回话ID
     * *
     * @return 回话界面
     */
    fun getConversation(mContext: Context, mConversationType: Conversation.ConversationType, mTargetId: String): Uri {
        return Uri.parse("rong://" + mContext.applicationInfo.packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build()
    }

    /**
     * 需要用户信息
     * 回话界面

     * @param mContext 上下文
     * *
     * @param message  消息
     * *
     * @return 回话界面
     */
    fun getConversation(mContext: Context, message: Message): Uri {
        val conversation = getConversation(mContext, message.conversationType, message.targetId)
        val userInfo = message.content.userInfo
        if (null == userInfo) {
            println("--用户信息为空--")
            return conversation
        }
        return conversation.buildUpon().appendQueryParameter("title", message.content.userInfo.name).build()
    }

    /**
     * 显示消息

     * @param message 消息
     * *
     * @return 过滤后的提醒消息
     */
    fun meessageType(message: Message?, msgNum: Int): String {
        if (null == message)
            return ""
        val messageContent = message.content
        var msg = ""
        if (messageContent is TextMessage) {//文本消息
            val textMessage = messageContent
            println("onSent-TextMessage:" + textMessage.content)
            msg = textMessage.content
        } else if (messageContent is ImageMessage) {//图片消息
            println("onSent-ImageMessage:" + messageContent.remoteUri)
            msg = "[图片]"
        } else if (messageContent is VoiceMessage) {//语音消息
            println("onSent-voiceMessage:" + messageContent.uri.toString())
            msg = "[语音]"
        } else if (messageContent is RichContentMessage) {//图文消息
            println("onSent-RichContentMessage:" + messageContent.content)
            msg = "[链接]"
        } else {
            println("onSent-其他消息，自己来判断处理")
        }
        if (1 < msgNum)
            msg = "[" + msgNum + "条]" + message.content.userInfo.name + ":" + msg
        return msg
    }

    /**
     * 消息回执类型

     * @return 消息回执类型
     */
    fun conversationTypes(): Array<Conversation.ConversationType> {
        return arrayOf(Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.PUBLIC_SERVICE)
    }

}
