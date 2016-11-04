package com.ybg.yxym.yueshow.constant

import com.ybg.yxym.yueshow.utils.EnvironmentStateUtils
import com.ybg.yxym.yueshow.utils.FileUtils

object AppConstants {

    val PREF_VIDEO_EXIST_USER = "Qupai_has_video_exist_in_user_list_pref"
    val DEBUG = true
    // 外部存储设备的根路径
    private val ExternalStorageRootPath = EnvironmentStateUtils.externalStorageDirectory.path

    private val BasePath = ExternalStorageRootPath + "/yuemei/"
    // 文件存放路径
    val FILE_CACHE_PATH = BasePath + "filecache/"
    //缓存的图片
    val IMAGE_CACHE_PATH = BasePath + "imagecache/"
    val VIDEO_SAVE_PATH = BasePath + "video/"
    // 保存图片
    val IMAGE_SAVE_PATH = BasePath + "photos/"

    // 下载存储地址
    val DOWNLOAD_PATH = BasePath + "download/"

    /**
     * 默认时长
     */
    val DEFAULT_DURATION_LIMIT = 8
    /**
     * 默认码率
     */
    val DEFAULT_BITRATE = 2000 * 1000
    /**
     * 默认Video保存路径，请开发者自行指定
     */
    val VIDEOPATH = FileUtils.newOutgoingFilePath()
    /**
     * 默认缩略图保存路径，请开发者自行指定
     */
    val THUMBPATH = VIDEOPATH + ".jpg"
    /**
     * 水印本地路径，文件必须为rgba格式的PNG图片
     */
    val WATER_MARK_PATH = "assets://Qupai/watermark/qupai-logo.png"

    ///////////////////////////////////////////////////////////////////////////
    // PlatformConfig info
    ///////////////////////////////////////////////////////////////////////////
    val APP_KEY_QQ = "lvwD0KS2zLGh8egG"
    val APP_ID_QQ = "1105297498"

    val APP_ID_WECHAT = "wx12ba0f3eab8edc7f"
    val APP_SECRET_WECHAT = "0c4bffa1902b866dc699c8d41ff9b30d"

    val APP_DEFAULT_USER_PHOTO = "http://yxym-user.oss-cn-shenzhen.aliyuncs.com/avatar/default.png"

    val SHARE_PREFERENCE_USER = "sp_user"
    val USER_ID = "user_id" //用户ID
    val USER_NAME = "user_name"//用户名
    val USER_NICKNAME = "nickname"//昵称
    val USER_PASSWORD = "password"//password
    val USER_AVATAR = "avatar"//头像
    val USER_MOTTO = "motto"//个性签名
    val USER_SEX = "sex"//性别
    val USER_BIRTHDAY = "birthday"//生日
    val USER_ADDRESS = "address"//地区
    val USER_HEIGHT = "height"//身高
    val USER_WEIGHT = "weight"//体重
    val USER_WEALTHY = "wealthy"//财富
    val USER_EXTRA = "extra"
    val USER_TOKEN = "token"
    val USER_REFRESH_TOKEN = "refresh_token"
    val USER_TLS_TOKEN = "tls_token"//腾讯主播TLStoke
    val USER_IMRY_TOKEN = "imry_token"//融云Token

}
