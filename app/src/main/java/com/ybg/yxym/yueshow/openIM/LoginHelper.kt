package com.ybg.yxym.yueshow.openIM

import android.preference.PreferenceManager
import android.text.TextUtils
import com.alibaba.mobileim.*
import com.alibaba.mobileim.channel.LoginParam
import com.alibaba.mobileim.channel.constant.B2BConstant
import com.alibaba.mobileim.channel.event.IWxCallback
import com.alibaba.mobileim.channel.util.AccountUtils
import com.alibaba.mobileim.channel.util.WxLog
import com.alibaba.mobileim.conversation.YWCustomMessageBody
import com.alibaba.mobileim.conversation.YWMessage
import com.alibaba.mobileim.gingko.model.tribe.YWTribe
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember
import com.alibaba.mobileim.login.YWLoginState
import com.alibaba.mobileim.login.YWPwdType
import com.alibaba.mobileim.utility.IMAutoLoginInfoStoreUtil
import com.alibaba.mobileim.utility.IMNotificationUtils
import com.alibaba.tcms.env.EnvManager
import com.alibaba.tcms.env.TcmsEnvType
import com.alibaba.tcms.env.YWEnvManager
import com.alibaba.tcms.env.YWEnvType
import com.alibaba.wxlib.util.SysUtil
import com.ybg.yxym.yueshow.app.ShowApplication
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LoginHelper {

    // openIM UI解决方案提供的相关API，创建成功后，保存为全局变量使用
    var imKit: YWIMKit? = null

    private var mApp: ShowApplication? = null

    val tribeInviteMessages: List<Map<YWTribe, YWTribeMember>> = ArrayList()

    fun initIMKit(userId: String, appKey: String) {
        imKit = YWAPI.getIMKitInstance<YWIMKit>(userId, appKey)
        addPushMessageListener()
        //添加联系人通知和更新监听 在初始化后、登录前添加监听，离线的联系人系统消息才能触发监听器
        addContactListeners()
    }

    var autoLoginState = YWLoginState.idle

    /**
     * 初始化SDK

     * @param context
     */
    fun initSDK(context: ShowApplication) {
        mApp = context
        sEnvType = YWEnvManager.getEnv(context)
        //初始化IMKit
        val userId = IMAutoLoginInfoStoreUtil.getLoginUserId()
        val appkey = IMAutoLoginInfoStoreUtil.getAppkey()
        val type = EnvManager.getInstance().getCurrentEnvType(mApp)
        if (type == TcmsEnvType.ONLINE || type == TcmsEnvType.PRE) {
            if (TextUtils.isEmpty(appkey)) {
                YWAPI.init(mApp, mApp!!.imKey)
            } else {
                YWAPI.init(mApp, appkey)
            }
        } else if (type == TcmsEnvType.TEST) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val appKey = preferences.getString("app_key", "")
            WxLog.i("APPKEY", "appKey = " + appKey!!)
            if (!TextUtils.isEmpty(appKey)) {
                YWAPI.aliInit(mApp, appKey, AccountUtils.SITE_CNALICNH)
            } else {
                YWAPI.aliInit(mApp, B2BConstant.APPKEY.APPKEY_B2B, AccountUtils.SITE_CNALICNH)
            }
            //YWAPI.init(mApp, APP_KEY_TEST);
        }

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(appkey)) {
            LoginHelper.instance.initIMKit(userId, appkey)
        }

        //通知栏相关的初始化
        NotificationInitHelper.init()

    }

    /**
     * 登录操作

     * @param userId   用户id
     * *
     * @param password 密码
     * *
     * @param callback 登录操作结果的回调
     */
    //------------------请特别注意，OpenIMSDK会自动对所有输入的用户ID转成小写处理-------------------
    //所以开发者在注册用户信息时，尽量用小写
    fun login(userId: String, password: String, appKey: String,
              callback: IWxCallback) {

        if (imKit == null) {
            return
        }

        SysUtil.setCnTaobaoInit(true)
        val loginParam = YWLoginParam.createLoginParam(userId,
                password)
        if (TextUtils.isEmpty(appKey) || appKey == YWConstants.YWSDKAppKey
                || appKey == YWConstants.YWSDKAppKeyCnHupan) {
            loginParam.serverType = LoginParam.ACCOUNTTYPE_WANGXIN
            loginParam.pwdType = YWPwdType.pwd
        }
        // openIM SDK提供的登录服务
        val mLoginService = imKit!!.loginService

        mLoginService.login(loginParam, callback)
    }

    /**
     * 添加新消息到达监听，该监听应该在登录之前调用以保证登录后可以及时收到消息
     */
    private fun addPushMessageListener() {
        if (imKit == null) {
            return
        }

        val conversationService = imKit!!.conversationService
        //添加单聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeP2PPushListener(mP2PListener)
        conversationService.addP2PPushListener(mP2PListener)

        //添加群聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeTribePushListener(mTribeListener)
        conversationService.addTribePushListener(mTribeListener)
    }

    private val mContactOperateNotifyListener = ContactOperateNotifyListenerImpl()

    private val mContactCacheUpdateListener = ContactCacheUpdateListenerImpl()

    /**
     * 联系人相关操作通知回调，SDK使用方可以实现此接口来接收联系人操作通知的监听
     * 所有方法都在UI线程调用
     * SDK会自动处理这些事件，一般情况下，用户不需要监听这个事件
     * @author shuheng
     */
    private fun addContactListeners() {
        //添加联系人通知和更新监听，先删除再添加，以免多次添加该监听
        removeContactListeners()
        if (imKit != null) {
            if (mContactOperateNotifyListener != null)
                imKit!!.contactService.addContactOperateNotifyListener(mContactOperateNotifyListener)
            if (mContactCacheUpdateListener != null)
                imKit!!.contactService.addContactCacheUpdateListener(mContactCacheUpdateListener)

        }
    }

    private fun removeContactListeners() {
        if (imKit != null) {
            if (mContactOperateNotifyListener != null)
                imKit!!.contactService.removeContactOperateNotifyListener(mContactOperateNotifyListener)
            if (mContactCacheUpdateListener != null)
                imKit!!.contactService.removeContactCacheUpdateListener(mContactCacheUpdateListener)

        }
    }

    private val mP2PListener = IYWP2PPushListener { contact, messages ->
        for (message in messages) {
            if (message.subType == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS) {
                if (message.messageBody is YWCustomMessageBody) {
                    val messageBody = message.messageBody as YWCustomMessageBody
                    if (messageBody.transparentFlag == 1) {
                        val content = messageBody.content
                        try {
                            val `object` = JSONObject(content)
                            if (`object`.has("text")) {
                                val text = `object`.getString("text")
                                IMNotificationUtils.getInstance().showToast(mApp, "透传消息，content = " + text)
                            } else if (`object`.has("customizeMessageType")) {
                                val customType = `object`.getString("customizeMessageType")
                                if (!TextUtils.isEmpty(customType) && customType == ChattingOperationCustom.CustomMessageType.READ_STATUS) {
                                    val conversation = imKit!!.conversationService.getConversationByConversationId(message.conversationId)
                                    val msgId = java.lang.Long.parseLong(`object`.getString("PrivateImageRecvReadMessageId"))
                                    conversation.updateMessageReadStatus(conversation, msgId)
                                }
                            }
                        } catch (e: JSONException) {
                        }

                    }
                }
            }
        }
    }

    private val mTribeListener = IYWTribePushListener { tribe, messages ->
        //TODO 收到群消息
    }

    /**
     * 登出
     */
    fun loginOut() {
        if (imKit == null) {
            return
        }


        // openIM SDK提供的登录服务
        val mLoginService = imKit!!.loginService
        mLoginService.logout(object : IWxCallback {

            override fun onSuccess(vararg arg0: Any) {

            }

            override fun onProgress(arg0: Int) {

            }

            override fun onError(arg0: Int, arg1: String) {

            }
        })
    }

    companion object {

        val instance = LoginHelper()

        var sEnvType = YWEnvType.TEST
    }
}
