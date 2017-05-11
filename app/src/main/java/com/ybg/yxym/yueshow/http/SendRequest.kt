package com.ybg.yxym.yueshow.http

import android.content.Context
import android.util.Pair

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.http.builder.GetRequestBuilder
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.UploadListener
import java.io.File

import java.util.IdentityHashMap

object SendRequest {

    /**
     * 默认配置了 api_key 这个参数("api_key", "B8EA0154BFE72EFD720A")在post方法中
     * 所有请求都添加一个tag 这个tag 是Context类型的方便结束请求
     */

    private val API_KEY = "B8EA0154BFE72EFD720A"
    private var mParams: MutableMap<String, Any>? = IdentityHashMap()
    private val mGson = GsonBuilder().serializeNulls().create()

    private fun appendParams(): String {
        if (mParams == null) {
            mParams = IdentityHashMap<String, Any>()
        }
        mParams!!.put("api_key", API_KEY)
        return mGson.toJson(mParams)
    }

    private val params: Map<String, Any>
        get() {
            if (mParams == null) {
                mParams = IdentityHashMap<String, Any>()
            }
            mParams!!.put("api_key", API_KEY)
            return mParams!!
        }

    /**
     * 1.1获取验证码
     *
     * @param mobile 手机号
     */
    fun getCaptcha(tag: Context, mobile: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("mobile" to mobile)
        OkHttpProxy.post(HttpUrl.getCaptchaUrl, tag, params, callback)
    }

    /**
     * 1.2验证码校验
     *
     * @param mobile 手机号
     * @param captcha  验证码的值
     */

    fun checkCaptcha(tag: Context, mobile: String, captcha: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("mobile" to mobile, "captcha" to captcha)
        OkHttpProxy.post(HttpUrl.checkCaptchaUrl, tag, params, callback)
    }

    /**
     * 1.3用户注册接口
     *
     * @param mobile 手机号
     * @param password  密码
     */
    fun userRegister(tag: Context, mobile: String, password: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("mobile" to mobile, "password" to password)
        OkHttpProxy.post(HttpUrl.userRegisterUrl, tag, params, callback)
    }


    /**
     * 1.4 用户登录接口
     *
     * @param mobile 手机号
     * @param password 密码
     */
    fun userLogin(tag: Context, mobile: String, password: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("mobile" to mobile, "password" to password)
        OkHttpProxy.post(HttpUrl.userLoginUrl, tag, params, callback)
    }

    /**
     * 1.5 获取用户基本信息
     *
     * @param token
     */
    fun getUserBase(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.userBaseUrl, tag, params, callback)
    }

    fun updateUserBase(tag: Context, token: String, nickName: String, avatar: String, ymMemo: String,
                       callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "nickName" to nickName, "avatar" to avatar, "ymMemo" to ymMemo)
        OkHttpProxy.post(HttpUrl.updateUserBaseUrl, tag, params, callback)
    }

    /**
     * 1.6 获取用户个性化信息
     *
     * @param token
     */
    fun getUserInfo(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.userInfoUrl, tag, params, callback)
    }

    fun updateUserInfo(tag: Context, token: String, birthday: String, position: String,
                       bodyHigh: Int, bodyWeight: Int, cupSize: String, bust: Int, waist: Int,
                       hips: Int, province: String, city: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "birthday" to birthday,
                "position" to position, "bodyHigh" to "$bodyHigh", "bodyWeight" to "$bodyWeight", "cupSize"
                to cupSize, "bust" to "$bust", "waist" to "$waist", "hips" to "$hips", "province" to
                province, "city" to city)
        OkHttpProxy.post(HttpUrl.updateUserInfoUrl, tag, params, callback)
    }

    fun getUserId(tag: Context, ymCode: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("ymCode" to ymCode)
        OkHttpProxy.post(HttpUrl.userIdUrl, tag, params, callback)
    }

    /**
     * 1.7 补充用户个性化信息
     *
     * @param token
     */
    fun completeUserInfo(tag: Context, token: String, birthday: String, nickName: String,
                         sex: String, avatar: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "birthday" to birthday,
                "nickName" to nickName, "sex" to sex, "avatar" to avatar)
        OkHttpProxy.post(HttpUrl.userCompleteUrl, tag, params, callback)
    }

    /**
     * 1.8 第三方用户登录，目前仅支持QQ，微信，新浪
     *
     * @param openid 手机号
     * @param platform 密码
     * @param nickName
     * @param userImage
     * @param sex
     */
    fun umLogin(tag: Context, openid: String, platform: String, nickName: String, userImage: String,
                sex: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("openid" to openid, "platform" to platform, "nickName" to nickName,
                "userImage" to userImage, "sex" to sex)
        OkHttpProxy.post(HttpUrl.umLoginUrl, tag, params, callback)
    }

    /**
     * 获取关注数
     */
    fun getFollowNum(tag: Context, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("userId" to userId)
        OkHttpProxy.post(HttpUrl.followNumUrl, tag, params, callback)
    }

    /**
     * 获取粉丝数
     */
    fun getFansNum(tag: Context, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("userId" to userId)
        OkHttpProxy.post(HttpUrl.fansNumUrl, tag, params, callback)
    }


    fun getUserLabel(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.getUserLabelUrl, tag, params, callback)
    }

    fun updateUserLabel(tag: Context, token: String, labels: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "labels" to labels)
        OkHttpProxy.post(HttpUrl.updateUserLabelUrl, tag, params, callback)
    }

    fun updateAppToken(tag: Context, userToken: String, appToken: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("userToken" to userToken, "appToken" to appToken)
        OkHttpProxy.post(HttpUrl.updateClientIdUrl, tag, params, callback)
    }

    /**
     * 2.1 获取美秀列表
     *
     * @param pageNum  第几页
     * @param pageSize  每页显示多少条
     * @param type  1最新 2最热
     */
    fun getShowList(tag: Context, pageNum: Int, pageSize: Int, type: Int, callback: OkCallback<*>) {
        val params = mapOf<String, Int>("pageNum" to pageNum, "pageSize" to pageSize, "type" to type)
        OkHttpProxy.post(HttpUrl.liveListUrl, tag, params, callback)
    }

    fun getUserShowList(tag: Context, userId: Int, pageNum: Int, pageSize: Int, callback: OkCallback<*>) {
        val params = mapOf<String, Int>("pageNum" to pageNum, "pageSize" to pageSize, "userId" to userId)
        OkHttpProxy.post(HttpUrl.userLiveListUrl, tag, params, callback)
    }

    fun getUserShowNum(tag: Context, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("userId" to userId)
        OkHttpProxy.post(HttpUrl.userLiveNumUrl, tag, params, callback)
    }

    /**
     * 获取发布者信息
     */
    fun getAuthorInfo(tag: Context, showId: Long, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token)
        OkHttpProxy.post(HttpUrl.authorInfoUrl, tag, params, callback)
    }

    /**
     * 获取附件信息
     */
    fun getShowFiles(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId")
        OkHttpProxy.post(HttpUrl.showFilesUrl, tag, params, callback)
    }

    /**
     * 获取评论信息信息
     */
    fun getShowPingList(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId")
        OkHttpProxy.post(HttpUrl.showPingUrl, tag, params, callback)
    }

    /**
     * 获取点赞列表信息
     */
    fun getShowZanList(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId")
        OkHttpProxy.post(HttpUrl.showZanUrl, tag, params, callback)
    }

    /**
     * 获取分享列表信息
     */
    fun getShowShareList(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId")
        OkHttpProxy.post(HttpUrl.showShareUrl, tag, params, callback)
    }

    /**
     * 2.2 新建美秀
     *
     * @param token
     * @param thumbnail
     * @param title
     * @param type 1 图片 2 视频
     */
    fun createShow(tag: Context, token: String, thumbnail: String,
                   title: String, type: String, price: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "thumbnail" to thumbnail,
                "title" to title, "type" to type, "price" to price)
        OkHttpProxy.post(HttpUrl.createLiveUrl, tag, params, callback)
    }

    fun addFiles(tag: Context, showId: String, files: String, type: String, callback:
    OkCallback<*>) {
        val params = mapOf<String, String>("showId" to showId, "fileIds" to files, "type" to type)
        OkHttpProxy.post(HttpUrl.appendFileUrl, tag, params, callback)
    }

    fun addEvents(tag: Context, showId: String, events: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to showId, "eventIds" to events)
        OkHttpProxy.post(HttpUrl.appendEventUrl, tag, params, callback)
    }

    /**
     * 美秀详情
     *
     * @param token
     * @param showId
     * @param content
     */
    fun viewLive(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId")
        OkHttpProxy.post(HttpUrl.showDetailUrl, tag, params, callback)
    }

    /**
     * 评论美秀
     *
     * @param token
     * @param showId
     * @param content
     */
    fun pingLive(tag: Context, token: String, showId: Long, content: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token, "content" to content)
        OkHttpProxy.post(HttpUrl.pingLiveUrl, tag, params, callback)
    }

    /**
     * 点赞
     *
     * @param token
     * @param showId
     */
    fun zanLive(tag: Context, token: String, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token)
        OkHttpProxy.post(HttpUrl.zanLiveUrl, tag, params, callback)
    }

    /**
     * 分享美秀
     *
     * @param token
     * @param showId
     */
    fun shareLive(tag: Context, token: String, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token)
        OkHttpProxy.post(HttpUrl.shareLiveUrl, tag, params, callback)
    }

    fun checkPayStatus(tag: Context, token: String, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token)
        OkHttpProxy.post(HttpUrl.checkShowPayStatusUrl, tag, params, callback)
    }

    fun payForShow(tag: Context, token: String, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("showId" to "$showId", "token" to token)
        OkHttpProxy.post(HttpUrl.payForShowUrl, tag, params, callback)
    }

    /**
     * 2.7 获得板块列表
     */
    fun getCategoryList(tag: Context, callback: OkCallback<*>) {
        OkHttpProxy.get(HttpUrl.categoryListUrl, tag, params, callback)
    }

    /**
     * 2.8 获得话题列表
     */
    fun getTopicList(tag: Context, callback: OkCallback<*>) {
        OkHttpProxy.post(HttpUrl.topicListUrl, tag, params, callback)
    }

    /**
     * 获取关注列表
     */
    fun getFollowList(tag: Context, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("userId" to userId)
        OkHttpProxy.post(HttpUrl.followListUrl, tag, params, callback)
    }

    /**
     * 关注
     */
    fun followUser(tag: Context, token: String, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("userId" to "$userId", "token" to token)
        OkHttpProxy.post(HttpUrl.followUserUrl, tag, params, callback)
    }

    fun checkFollowStatus(tag: Context, token: String, userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("userId" to "$userId", "token" to token)
        OkHttpProxy.post(HttpUrl.checkFollowStatusUrl, tag, params, callback)
    }

    /**
     * 获取装扮背景列表
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param start
     * *
     * @param count
     * *
     * @param callback
     */
    fun getDressesList(tag: Context, userid: String, token: String, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.dressesList +
                "?userid=" + userid +
                "&token=" + token +
                "&start=" + start +
                "&count=" + count +
                "&api_key=" + API_KEY
        LogUtil.d(url)
        try {
            val builder = OkHttpProxy.get().url(url).tag(tag)
            builder.enqueue(callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 上传文件
     */
    fun uploadFile(tag: Context, folder: String, file: File, uploadListener: UploadListener) {
        try {
            val uploadBuilder = OkHttpProxy.upload().url(HttpUrl.FILE_SERVER_UPLOAD).tag(tag)
            uploadBuilder.addParams("folder", folder)
                    .file(Pair("Filedata", file))
                    .start(uploadListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRuiMeiBang(tag: Context, beginTime: String, endTime: String, pageNum: Int, pageSize: Int,
                      callback: OkCallback<*>) {
        val params = mapOf<String, String>("beginTime" to beginTime, "endTime" to endTime,
                "pageNum" to "$pageNum", "pageSize" to "$pageSize")
        OkHttpProxy.post(HttpUrl.ruiMeiBang, tag, params, callback)
    }

    fun getRenQiBang(tag: Context, beginTime: String, endTime: String, pageNum: Int, pageSize: Int,
                     callback: OkCallback<*>) {
        val params = mapOf<String, String>("beginTime" to beginTime, "endTime" to endTime,
                "pageNum" to "$pageNum", "pageSize" to "$pageSize")
        OkHttpProxy.post(HttpUrl.renQiBang, tag, params, callback)
    }

    fun getHuoLiBang(tag: Context, beginTime: String, endTime: String, pageNum: Int, pageSize: Int,
                     callback: OkCallback<*>) {
        val params = mapOf<String, String>("beginTime" to beginTime, "endTime" to endTime,
                "pageNum" to "$pageNum", "pageSize" to "$pageSize")
        OkHttpProxy.post(HttpUrl.huoLiBang, tag, params, callback)
    }

    fun getMiAiBang(tag: Context, beginTime: String, endTime: String, pageNum: Int, pageSize: Int,
                    userId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, String>("beginTime" to beginTime, "endTime" to endTime,
                "pageNum" to "$pageNum", "pageSize" to "$pageSize", "userId" to "$userId")
        OkHttpProxy.post(HttpUrl.miAiBang, tag, params, callback)
    }

    /**
     * 3.1 新建直播
     *
     * @param token
     * @param barId
     * @param thumbnail
     * @param event
     */
    fun createLive(tag: Context, token: String, barId: String, thumbnail: String,
                   event: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "barId" to barId, "thumbnail" to thumbnail,
                "event" to event)
        OkHttpProxy.post(HttpUrl.createLiveUrl2, tag, params, callback)
    }

    /**
     * 3.2 关闭直播
     */
    fun closeLive(tag: Context, token: String, showId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "showId" to showId)
        OkHttpProxy.post(HttpUrl.closeLiveUrl, tag, params, callback)
    }

    /**
     * 3.3 观看直播
     */
    fun showLive(tag: Context, token: String, showId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "showId" to showId)
        OkHttpProxy.post(HttpUrl.showLiveUrl, tag, params, callback)
    }

    /**
     * 3.4 离开直播
     */
    fun leaveLive(tag: Context, token: String, showId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "showId" to showId)
        OkHttpProxy.post(HttpUrl.leaveLiveUrl, tag, params, callback)
    }

    /**
     * 3.5 发送直播消息
     */
    fun sendLiveMsg(tag: Context, token: String, showId: String, flag: String, type: String, content: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "showId" to showId, "flag" to flag, "type" to type, "content" to content)
        OkHttpProxy.post(HttpUrl.sendLiveMsgUrl, tag, params, callback)
    }

    /**
     * 3.6 检查直播状态
     */
    fun checkLiveStatus(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("showId" to showId)
        OkHttpProxy.post(HttpUrl.checkStatusUrl, tag, params, callback)
    }

    /**
     * 3.7 直播數據統計
     */
    fun getLiveDetail(tag: Context, showId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Long>("showId" to showId)
        OkHttpProxy.post(HttpUrl.endLiveUrl, tag, params, callback)
    }

    /**
     * 4.1 好友首页消息列表
     */
    fun getMsgList(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.friendMsgUrl, tag, params, callback)
    }

    /**
     * 4.2 好友列表
     */
    fun getFriendList(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.friendListUrl, tag, params, callback)
    }

    /**
     * 4.3 查看约会详情
     */
    fun viewDateDetail(tag: Context, token: String, dateId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "dateId" to dateId)
        OkHttpProxy.post(HttpUrl.dateDetailUrl, tag, params, callback)
    }

    /**
     * 4.4 接受约会
     */
    fun acceptDate(tag: Context, token: String, dateId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "dateId" to dateId)
        OkHttpProxy.post(HttpUrl.acceptDateUrl, tag, params, callback)
    }

    /**
     * 4.5 拒绝约会
     */
    fun rejectDate(tag: Context, token: String, dateId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "dateId" to dateId)
        OkHttpProxy.post(HttpUrl.rejectDateUrl, tag, params, callback)
    }

    /**
     * 4.6 忽略约会
     */
    fun ignoreDate(tag: Context, token: String, dateId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "dateId" to dateId)
        OkHttpProxy.post(HttpUrl.ignoreDateUrl, tag, params, callback)
    }

    /**
     * 4.7 查询好友请求列表
     */
    fun getFriendRequestList(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.friendRequestListUrl, tag, params, callback)
    }

    /**
     * 4.8 同意好友请求
     */
    fun acceptFriendRequest(tag: Context, token: String, requestId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "requestId" to requestId)
        OkHttpProxy.post(HttpUrl.acceptFriendRequestUrl, tag, params, callback)
    }

    /**
     * 4.8 拒绝好友请求
     */
    fun rejectFriendRequest(tag: Context, token: String, requestId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "requestId" to requestId)
        OkHttpProxy.post(HttpUrl.rejectFriendRequestUrl, tag, params, callback)
    }

    /**
     * 4.9 获取成员列表
     */
    fun getMemberList(tag: Context, token: String, groupId: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "groupId" to groupId)
        OkHttpProxy.post(HttpUrl.acceptFriendRequestUrl, tag, params, callback)
    }

    /**
     * 4.10 查询是否是好友
     */
    fun checkFriend(tag: Context, token: String, ymCode: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "ymCode" to ymCode)
        OkHttpProxy.post(HttpUrl.checkFriendUrl, tag, params, callback)
    }

    /**
     * 4.11 查看好友资料
     */
    fun getFriendInfo(tag: Context, token: String, ymCode: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "ymCode" to ymCode)
        OkHttpProxy.post(HttpUrl.getFriendInfoUrl, tag, params, callback)
    }

    /**
     * 4.12 查看用户资料
     */
    fun getUserDetailInfo(tag: Context, ymCode: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("ymCode" to ymCode)
        OkHttpProxy.post(HttpUrl.getUserInfoUrl, tag, params, callback)
    }

    /**
     * 4.13 改变提醒状态
     */
    fun changeDisturbState(tag: Context, token: String, friendId: Long, disturbing: Int, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("token" to token, "friendId" to friendId, "disturbing" to disturbing)
        OkHttpProxy.post(HttpUrl.changeDisturbStateUrl, tag, params, callback)
    }

    /**
     * 4.14 改变黑名单状态
     */
    fun changeBlackListState(tag: Context, token: String, friendId: Long, inBlacklist: Int, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("token" to token, "friendId" to friendId, "inBlacklist" to inBlacklist)
        OkHttpProxy.post(HttpUrl.changeBlackListStateUrl, tag, params, callback)
    }

    /**
     * 4.15 删除好友
     */
    fun deleteFriend(tag: Context, token: String, friendId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("token" to token, "friendId" to friendId)
        OkHttpProxy.post(HttpUrl.deleteFriendUrl, tag, params, callback)
    }

    /**
     * 4.16 请求添加好友
     */
    fun createFriendRequest(tag: Context, token: String, userId: Long, reason: String, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("token" to token, "userId" to userId, "reason" to reason)
        OkHttpProxy.post(HttpUrl.createFriendRequestUrl, tag, params, callback)
    }

    /**
     * 5.1 获得充值卡
     */
    fun getCardList(tag: Context, callback: OkCallback<*>) {
        val params = emptyMap<String, String>()
        OkHttpProxy.post(HttpUrl.getCardListUrl, tag, params, callback)
    }

    /**
     * 5.2 创建订单
     */
    fun createOrder(tag: Context, token: String, cardId: Long, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("token" to token, "cardId" to cardId)
        OkHttpProxy.post(HttpUrl.createOrderUrl, tag, params, callback)
    }

    /**
     * 5.3 查询订单状态
     */
    fun checkOrderStatus(tag: Context, orderNo: String, callback: OkCallback<*>) {
        val params = mapOf<String, Any>("orderNo" to orderNo)
        OkHttpProxy.post(HttpUrl.checkOrderStatusUrl, tag, params, callback)
    }

    /**
     * 5.4 创建交易对象
     */
    fun createCharge(tag: Context, orderNo: String, payType: String,callback: OkCallback<*>) {
        val params = mapOf("orderNo" to orderNo, "payType" to payType)
        OkHttpProxy.post(HttpUrl.createChargeUrl, tag, params, callback)
    }

    /**
     * 5.5 查询订单状态
     */
    fun getGiftList(tag: Context, pageNum: Int, pageSize: Int, callback: OkCallback<*>) {
        val params = mapOf("pageNum" to pageNum, "pageSize" to pageSize)
        OkHttpProxy.post(HttpUrl.getGiftListUrl, tag, params, callback)
    }

    /**
     * 5.6 查询订单状态
     */
    fun sendGift(tag: Context, token: String, userId: Long, giftId: Long, callback: OkCallback<*>) {
        val params = mapOf("token" to token, "userId" to userId, "giftId" to giftId)
        OkHttpProxy.post(HttpUrl.sendGiftUrl, tag, params, callback)
    }

}
