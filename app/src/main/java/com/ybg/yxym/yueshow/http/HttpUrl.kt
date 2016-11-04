package com.ybg.yxym.yueshow.http

/**
 * 网络请求相关设置,配置请求地址及参数
 */
object HttpUrl {
    private val debug = true

    //测试服务器地址
    val API_HOST_DEBUG = "http://192.168.12.101:8080/ma"
    val ROOT_URL = API_HOST_DEBUG
    //获取验证码
    private val GET_CAPTCHA = "/system/getCaptcha"
    //验证验证码
    private val CHECK_CAPTCHA = "/system/checkCaptcha"
    //用户注册
    private val USER_REGISTER = "/userBase/register"
    //用户登录
    private val USER_LOGIN = "/userBase/login"
    //Refresh Token
    private val GET_USER_BASE = "/userBase/getUserBase"
    /*直播列表*/
    private val LIVE_LIST = "/live/v1/live/list"
    /*create live*/
    private val CREATE_LIVE = "/live/v1/live/create"
    /*switch live*/
    private val SWITCH_LIVE = "/live/v1/live/switch"
    /*comment live*/
    private val COMMENT_LIVE = "/live/v1/live/comments"
    /*like live*/
    private val LIKE_LIVE = "/live/v1/live/like"
    /*forward live*/
    private val FORWARD_LIVE = "/live/v1/live/Forward"
    /*Category list*/
    private val CATEGORY_LIST = "/live/v1/category/list"
    /*Create Topic*/
    private val CREATE_TOPIC = "/live/v1/topic/create"


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

    val createTopicUrl: String
        get() = ROOT_URL + CREATE_TOPIC

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
