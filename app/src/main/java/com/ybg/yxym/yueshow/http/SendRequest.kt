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
     * @param barId
     * @param thumbnail
     * @param title
     */
    fun createShow(tag: Context, token: String, barId: String, thumbnail: String,
                   title: String, type: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "barId" to barId, "thumbnail" to thumbnail,
                "title" to title, "type" to type)
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

    /**
     * 获取好友列表
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param friendtype [1:我关注的，2:关注我的, 3:相互关注的, 4:我屏蔽的, 8:屏蔽我的]
     * *
     * @param start
     * *
     * @param count
     * *
     * @param callback
     */
    fun getFriendList(tag: Context, userid: String, token: String, friendtype: Int, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.friendList +
                "?userid=" + userid +
                "&token=" + token +
                "&start=" + start +
                "&count=" + count +
                "&friendtype=" + friendtype +
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

}
