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

    /**
     * 1.6 获取用户个性化信息
     *
     * @param token
     */
    fun getUserInfo(tag: Context, token: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token)
        OkHttpProxy.post(HttpUrl.userInfoUrl, tag, params, callback)
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
     * 2.2 新建美秀
     *
     * @param token
     * @param barId
     * @param thumbnail
     * @param title
     */
    fun createShow(tag: Context, token: String, barId: String, thumbnail: String,
                   title: String, callback: OkCallback<*>) {
        val params = mapOf<String, String>("token" to token, "barId" to barId, "thumbnail" to thumbnail,
                "title" to title)
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
     * 2.3 switch live
     * path: /live/v1/live/switch
     * method: POST
     * params:

     * @param username username string required
     * *
     * @param token    token string required
     * *
     * @param type     token_type int required
     * *
     * @param livingId living_id int required
     * *
     * @param living   living bool required
     */
    fun switchLive(tag: Context, username: String, token: String, type: Int, livingId: Int, living: Boolean, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("living_id", livingId)
        mParams!!.put("living", living)
        OkHttpProxy.postJson(HttpUrl.switchLiveUrl, tag, appendParams(), callback)
    }

    /**
     * 2.4 comment live
     * path: /live/v1/live/comments
     * method: POST
     * params:

     * @param username string required
     * *
     * @param token    token string required
     * *
     * @param type     token_type int required
     * *
     * @param livingId living_id int required
     * *
     * @param comment  comment string
     * *
     * @param extra    extra string
     */
    fun commentLive(tag: Context, username: String, token: String, type: Int, livingId: Int, comment: String, extra: String, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("living_id", livingId)
        mParams!!.put("comment", comment)
        mParams!!.put("extra", extra)
        OkHttpProxy.postJson(HttpUrl.commentLiveUrl, tag, appendParams(), callback)
    }

    /**
     * 2.5 like comment
     * path: /live/v1/live/like
     * method: POST
     * params:

     * @param username username string required
     * *
     * @param token    token string required
     * *
     * @param type     token_type int required
     * *
     * @param livingId living_id int required
     * *
     * @param like     like bool
     */
    fun likeLive(tag: Context, username: String, token: String, type: Int, livingId: Int, like: Boolean, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("living_id", livingId)
        mParams!!.put("like", like)
        OkHttpProxy.postJson(HttpUrl.likeLiveUrl, tag, appendParams(), callback)
    }

    /**
     * 2.6 Forward live
     * path: /live/v1/live/Forward
     * method: POST
     * params:

     * @param username username string required
     * *
     * @param token    token string required
     * *
     * @param type     token_type int required
     * *
     * @param livingId living_id int required
     * *
     * @param opinion  opinion string
     */
    fun forwardLive(tag: Context, username: String, token: String, type: Int, livingId: Int, opinion: String, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("living_id", livingId)
        mParams!!.put("opinion", opinion)
        OkHttpProxy.postJson(HttpUrl.forwardLiveUrl, tag, appendParams(), callback)
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
        OkHttpProxy.postJson(HttpUrl.topicListUrl, tag, appendParams(), callback)
    }

    /**
     * 搜索用户

     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param keyword
     * *
     * @param start
     * *
     * @param count
     * *
     * @param callback
     */
    fun getUserList(tag: Context, userid: String, token: String,
                    keyword: String, start: String, count: String, callback: OkCallback<*>) {
        val url = HttpUrl.userList +
                "?userid=" + userid +
                "&start=" + start +
                "&count=" + count +
                "&&keyword=" + keyword +
                "&api_key=" + API_KEY +
                "&token=" + token
        LogUtil.d(url)
        try {
            val builder = OkHttpProxy.get().url(url).tag(tag)
            builder.enqueue(callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 首页hot
     * @param tag
     * //     * @param userid 不设定时则不显示好友相关信息，设定时显示好友信息
     * *
     * @param posttype 1直播，2是图片，3是视频，0全部
     * *
     * @param start 起始
     * *
     * @param count  多少条
     * *
     * @param callback 回调
     */
    fun getHomeHot(tag: Context, posttype: Int, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.homeHot +
                "?start=" + start +
                "&count=" + count +
                "&posttype=" + posttype +
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
     * 首页最鲜
     * @param tag
     * *
     * @param userid
     * *
     * @param posttype
     * *
     * @param start
     * *
     * @param count
     * *
     * @param callback
     */
    fun getHomeNew(tag: Context, userid: String, posttype: Int, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.homeNew +
                "?userid=" + userid +
                "&start=" + start +
                "&count=" + count +
                "&posttype=" + posttype +
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
     * 首页友秀圈
     * @param tag
     * *
     * @param userid
     * *
     * @param posttype
     * *
     * @param start
     * *
     * @param count
     * *
     * @param callback
     */
    fun getHomeFriend(tag: Context, userid: String, token: String, friendtype: Int, posttype: Int, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.homeFriend +
                "?userid=" + userid +
                "&token=" + token +
                "&friendtype=" + friendtype +
                "&start=" + start +
                "&count=" + count +
                "&posttype=" + posttype +
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
     * 点赞 2016-09-23 提交有问题
     * method : Post
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param livingId
     * *
     * @param like
     * *
     * @param callback
     */
    fun like(tag: Context, userid: String, token: String, livingId: Long?, like: String,
             callback: OkCallback<*>) {
        mParams!!.put("userid", userid)
        mParams!!.put("token", token)
        mParams!!.put("livingId", livingId!!)
        mParams!!.put("like", like)
        mParams!!.put("api_key", API_KEY)
        OkHttpProxy.post(HttpUrl.like, tag, mParams!!, callback)
    }


    /**
     * 获取用户详细信息
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param callback
     */
    fun getUserDetailInfo(tag: Context, userid: String, token: String, callback: OkCallback<*>) {
        val url = HttpUrl.userInfo +
                "?userid=" + userid +
                "&token=" + token +
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
     * 获取融云token
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param callback
     */
    fun getRongyunToken(tag: Context, userid: String, token: String, callback: OkCallback<*>) {
        val url = HttpUrl.rongyunToken +
                "?userid=" + userid +
                "&token=" + token +
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
     * 获取融云token
     * @param tag
     * *
     * @param userid
     * *
     * @param token
     * *
     * @param callback
     */
    fun getCommentList(tag: Context, userid: String, token: String, livedid: Int, start: Int, count: Int, callback: OkCallback<*>) {
        val url = HttpUrl.commentList +
                "?userid=" + userid +
                "&token=" + token +
                "&liveid=" + livedid +
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
}
