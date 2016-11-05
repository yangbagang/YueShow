package com.ybg.yxym.yueshow.http

import android.content.Context

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.http.builder.GetRequestBuilder
import com.ybg.yxym.yueshow.http.callback.OkCallback

import java.util.IdentityHashMap

/**
 * @author Jax
 * *
 * @version V1.0.0
 * *
 * @Created on 2016/1/20 15:06.
 * * 后期json内容在封装下
 */
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
                "nickName" to nickName, "sex" to sex, "avatar" to  avatar)
        OkHttpProxy.post(HttpUrl.userInfoUrl, tag, params, callback)
    }

    /**
     * 2.1 Live  List
     * path: /live/v1/live/list
     * method: GET
     * params:

     * @param username username string required
     * *
     * @param token    token string required
     * *
     * @param type     token_type int required
     * *
     * @param start    start int start from 0
     * *
     * @param count    count int default is 10
     * *
     * @param order    order int hot/latest etc
     */
    fun getLiveList(tag: Context, username: String, token: String, type: Int, start: Int, count: Int, order: Int, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("start", start)
        mParams!!.put("count", count)
        mParams!!.put("order", order)
        OkHttpProxy.get(HttpUrl.liveListUrl, tag, params, callback)
    }

    /**
     * 2.2 live Create
     * path: /live/v1/live/create
     * method: POST
     * params:

     * @param username      username string required
     * *
     * @param token         token string required
     * *
     * @param type          token_type int required
     * *
     * @param relatedTopics related_topics [int]   如果找不到对应的topic，则需要先创建
     * *
     * @param category      category int required  表示选择的板块（0：逗萌乐悠悠 1：温湿暖潮潮 2：人生要BIBI 3：有颜有气质 4：唱演兼懂言 5：会秀更会美）
     */
    fun createLive(tag: Context, username: String, token: String, type: Int, relatedTopics: Int, category: Int, callback: OkCallback<*>) {
        mParams!!.put("username", username)
        mParams!!.put("token", token)
        mParams!!.put("token_type", type)
        mParams!!.put("related_topics ", relatedTopics)
        mParams!!.put("category ", category)
        OkHttpProxy.postJson(HttpUrl.createLiveUrl, tag, appendParams(), callback)
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
     * 2.7 get category list
     * path: /live/v1/category/list
     * method: GET
     * params: None
     */
    fun getCategoryList(tag: Context, callback: OkCallback<*>) {
        OkHttpProxy.get(HttpUrl.categoryListUrl, tag, params, callback)
    }

    /**
     * 2.8 Create Topic
     * path: /live/v1/topic/create
     * method: POST
     * params: None
     */

    fun createTopic(tag: Context, callback: OkCallback<*>) {
        OkHttpProxy.postJson(HttpUrl.createTopicUrl, tag, appendParams(), callback)
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

}
