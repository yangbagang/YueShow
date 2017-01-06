package com.ybg.yxym.yueshow.http

/**
 * 网络请求相关设置,配置请求地址及参数
 */
object HttpUrl {
    private val debug = false

    //开发服务器地址
    val API_HOST_DEBUG = "http://192.168.12.99:8080/ma"
    //生产服务器地址
    val API_HOST_PRODUCT = "https://139.224.186.241:8443/ma"
    //上传
    val FILE_SERVER_UPLOAD = "http://120.76.74.2/file/file/upload"
    //预览
    val FILE_SERVER_PREVIEW = "http://120.76.74.2/file/file/preview"
    //下载
    val FILE_SERVER_DOWNLOAD = "http://120.76.74.2/file/file/download"

    val ROOT_URL = if (debug) API_HOST_DEBUG else API_HOST_PRODUCT
    //获取验证码
    private val GET_CAPTCHA = "/system/getCaptcha"
    //验证验证码
    private val CHECK_CAPTCHA = "/system/checkCaptcha"
    //用户注册
    private val USER_REGISTER = "/userBase/register"
    //用户登录
    private val USER_LOGIN = "/userBase/login"
    //用户基本信息
    private val GET_USER_BASE = "/userBase/getUserBase"
    private val UPDATE_USER_BASE = "/userBase/updateUserBase"
    //用户个性化信息
    private val GET_USER_INFO = "/userInfo/getUserInfo"
    private val UPDATE_USER_INFO = "/userInfo/updateUserInfo"
    //用户个性化信息
    private val COMPLETE_USER_INFO = "/userBase/completeData"
    //用户爱好
    private val GET_USER_LABEL = "/userLabel/list"
    private val UPDATE_USER_LABEL = "/userLabel/update"
    //第三方用户登录
    private val UM_USER_LOGIN = "/userBase/umLogin"
    /*美秀列表*/
    private val LIVE_LIST = "/ruiShow/list"
    private val AUTHOR_INFO = "/userBase/getAuthorInfoByShowId"
    private val SHOW_FIELS = "/showFile/list"
    private val SHOW_PING_LIST = "/showPing/list"
    private val SHOW_ZAN_LIST = "/showZan/list"
    private val SHOW_SHARE_LIST = "/showShare/list"
    /*新建美秀*/
    private val CREATE_LIVE = "/ruiShow/create"
    private val APPEND_FILES = "/showFile/addFiles"
    private val APPEND_EVENTS = "/ruiEvent/appendEvent"
    private val LIVE_DETAIL = "/ruiShow/detail"
    /*评论美秀*/
    private val PING_LIVE = "/ruiShow/ping"
    /*点赞*/
    private val ZAN_LIVE = "/ruiShow/zan"
    /*分享*/
    private val SHARE_LIVE = "/ruiShow/share"
    /*美秀板块*/
    private val CATEGORY_LIST = "/ruiBar/list"
    /*活动主题*/
    private val TOPIC_LIST = "/ruiEvent/list"
    private val PING_LIST = "/showPing/list"
    private val FRIEND_LIST = "/userBase/friendList"
    private val DRESSES_LIST = "/userBase/dressList"

    private val FOLLOW_LIST = "/follow/list"
    private val FOLLOW_FOLLOW = "/follow/follow"

    //悦美榜
    val ruiMeiBang: String
        get() = ROOT_URL + "/meiLiHistory/meiliList"
    val renQiBang: String
        get() = ROOT_URL + "/meiLiHistory/renqiList"
    val huoLiBang: String
        get() = ROOT_URL + "/meiLiHistory/huoliList"
    val miAiBang: String
        get() = ROOT_URL + "/meiLiHistory/miAiList"

    //用户模块
    val userLoginUrl: String
        get() = ROOT_URL + USER_LOGIN

    val getCaptchaUrl: String
        get() = ROOT_URL + GET_CAPTCHA

    val checkCaptchaUrl: String
        get() = ROOT_URL + CHECK_CAPTCHA

    val userRegisterUrl: String
        get() = ROOT_URL + USER_REGISTER

    val userBaseUrl: String
        get() = ROOT_URL + GET_USER_BASE

    val updateUserBaseUrl: String
        get() = ROOT_URL + UPDATE_USER_BASE

    val userInfoUrl: String
        get() = ROOT_URL + GET_USER_INFO

    val updateUserInfoUrl: String
        get() = ROOT_URL + UPDATE_USER_INFO

    val userCompleteUrl: String
        get() = ROOT_URL + COMPLETE_USER_INFO

    val getUserLabelUrl: String
        get() = ROOT_URL + GET_USER_LABEL

    val updateUserLabelUrl: String
        get() = ROOT_URL + UPDATE_USER_LABEL

    val umLoginUrl: String
        get() = ROOT_URL + UM_USER_LOGIN

    val liveListUrl: String
        get() = ROOT_URL + LIVE_LIST

    val showDetailUrl: String
        get() = ROOT_URL + LIVE_DETAIL

    val authorInfoUrl: String
        get() = ROOT_URL + AUTHOR_INFO

    val showFilesUrl: String
        get() = ROOT_URL + SHOW_FIELS

    val showPingUrl: String
        get() = ROOT_URL + SHOW_PING_LIST

    val showZanUrl: String
        get() = ROOT_URL + SHOW_ZAN_LIST

    val showShareUrl: String
        get() = ROOT_URL + SHOW_SHARE_LIST

    val followUserUrl: String
        get() = ROOT_URL + FOLLOW_FOLLOW
    val followListUrl: String
        get() = ROOT_URL + FOLLOW_LIST

    val followNumUrl: String
        get() = ROOT_URL + "/follow/getFollowNum"

    val fansNumUrl: String
        get() = ROOT_URL + "/follow/getFansNum"


    /**
     * @return 好友列表 url
     */
    val friendList: String
        get() = ROOT_URL + FRIEND_LIST

    /**
     * @return 获取装扮背景列表
     */
    val dressesList: String
        get() = ROOT_URL + DRESSES_LIST

    /**
     * @return 点赞URL
     */
    val zanLiveUrl: String
        get() = ROOT_URL + ZAN_LIVE


    /**
     * @return 获取用户信息
     */
    val userInfo: String
        get() = ROOT_URL + GET_USER_INFO

    val commentList: String
        get() = ROOT_URL + PING_LIST


    val createLiveUrl: String
        get() = ROOT_URL + CREATE_LIVE

    val appendFileUrl: String
        get() = ROOT_URL + APPEND_FILES

    val appendEventUrl: String
        get() = ROOT_URL + APPEND_EVENTS

    val topicListUrl: String
        get() = ROOT_URL + TOPIC_LIST

    val categoryListUrl: String
        get() = ROOT_URL + CATEGORY_LIST

    val shareLiveUrl: String
        get() = ROOT_URL + SHARE_LIVE

    val pingLiveUrl: String
        get() = ROOT_URL + PING_LIVE

    fun getImageUrl(fid: String): String {
        if (fid.startsWith("http:", true) || fid.startsWith("https:", true)) {
            return fid
        }
        return FILE_SERVER_PREVIEW + "/" + fid
    }

    fun getVideoUrl(fid: String): String {
        if (fid.startsWith("http:", true) || fid.startsWith("https:", true)) {
            return fid
        }
        return FILE_SERVER_DOWNLOAD + "/" + fid
    }
}
