package com.ybg.yxym.yueshow.http

/**
 * 网络请求相关设置,配置请求地址及参数
 */
object HttpUrl {
    private val debug = true

    //测试服务器地址
    val API_HOST_DEBUG = "http://192.168.12.99:8080/ma"
    val API_HOST_PRODUCT = "https://139.224.186.241:8443/ma"
    val FILE_SERVER_UPLOAD = "http://120.76.74.2/file/file/upload"
    val FILE_SERVER_PREVIEW = "http://120.76.74.2/file/file/preview"

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
    //用户个性化信息
    private val GET_USER_INFO = "/userInfo/getUserInfo"
    //用户个性化信息
    private val COMPLETE_USER_INFO = "/userBase/completeData"
    //第三方用户登录
    private val UM_USER_LOGIN = "/userBase/umLogin"
    /*美秀列表*/
    private val LIVE_LIST = "/ruiShow/list"
    /*新建美秀*/
    private val CREATE_LIVE = "/ruiShow/create"
    private val APPEND_FILES = "/showFile/addFiles"
    private val APPEND_EVENTS = "/ruiEvent/appendEvent"
    /*switch live*/
    private val SWITCH_LIVE = "/live/v1/live/switch"
    /*comment live*/
    private val COMMENT_LIVE = "/live/v1/live/comments"
    /*like live*/
    private val LIKE_LIVE = "/live/v1/live/like"
    /*forward live*/
    private val FORWARD_LIVE = "/live/v1/live/Forward"
    /*Category list*/
    private val CATEGORY_LIST = "/ruiBar/list"
    /*Create Topic*/
    private val TOPIC_LIST = "/ruiEvent/list"


    /**用户搜索列表 */
    private val USER_LIST = "/account/v1/user/search"
    /**首页 hot */
    private val HOME_HOT = "/live/v1/home/hot"
    /**首页 最鲜 */
    private val HOME_NEW = "/live/v1/home/new"
    /**首页 友秀圈 */
    private val HOME_FRIEND = "/live/v1/home/friend"

    /**好友列表 */
    private val FRIEND_LIST = "/account/v1/friend"
    /**取装扮背景列表] */
    private val DRESSESS_LIST = "/account/v1/dresses"
    /**获取用户信息] */
    private val USER_INFO = "/account/v1/user/get"
    /**点赞 */
    private val LIKE = "/live/v1/live/like"

    private val USER_RONGYUN_TOKEN = "/account/v1/user/get_rc_token"

    private val USER_COMMENT_LIST = "/live/v1/live/comments"

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

    val userInfoUrl: String
        get() = ROOT_URL + GET_USER_INFO

    val userCompleteUrl: String
        get() = ROOT_URL + COMPLETE_USER_INFO

    val umLoginUrl: String
        get() = ROOT_URL + UM_USER_LOGIN

    val liveListUrl: String
        get() = ROOT_URL + LIVE_LIST


    /**
     * @return 搜索用户
     */
    val userList: String
        get() = ROOT_URL + USER_LIST

    /**
     * @return 首页热门
     */
    val homeHot: String
        get() = ROOT_URL + HOME_HOT

    /**
     * @return 首页最鲜
     */
    val homeNew: String
        get() = ROOT_URL + HOME_NEW

    /**
     * @return 首页 友秀圈
     */
    val homeFriend: String
        get() = ROOT_URL + HOME_FRIEND

    /**
     * @return 好友列表 url
     */
    val friendList: String
        get() = ROOT_URL + FRIEND_LIST

    /**
     * @return 获取装扮背景列表
     */
    val dressesList: String
        get() = ROOT_URL + DRESSESS_LIST

    /**
     * @return 点赞URL
     */
    val like: String
        get() = ROOT_URL + LIKE


    /**
     * @return 获取用户信息
     */
    val userInfo: String
        get() = ROOT_URL + USER_INFO

    /**
     * @return 获取融云token
     */
    val rongyunToken: String
        get() = ROOT_URL + USER_RONGYUN_TOKEN

    val commentList: String
        get() = ROOT_URL + USER_COMMENT_LIST


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

    val forwardLiveUrl: String
        get() = ROOT_URL + FORWARD_LIVE

    val likeLiveUrl: String
        get() = ROOT_URL + LIKE_LIVE

    val commentLiveUrl: String
        get() = ROOT_URL + COMMENT_LIVE

    val switchLiveUrl: String
        get() = ROOT_URL + SWITCH_LIVE
}
