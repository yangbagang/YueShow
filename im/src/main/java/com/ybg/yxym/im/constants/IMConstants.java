package com.ybg.yxym.im.constants;

import android.os.Environment;

/**
 * Created by yangbagang on 2017/4/16.
 */

public class IMConstants {

    // 外部存储设备的根路径
    private static final String ExternalStorageRootPath = Environment.getExternalStorageDirectory().getPath();

    private static final String BasePath = ExternalStorageRootPath + "/ym_im/";

    //文件存放路径
    public static final String FILE_CACHE_PATH = BasePath + "filecache/";
    //视频存放路径
    public static final String VIDEO_CACHE_PATH = BasePath + "videoCache/";
    //缓存的图片
    public static final String IMAGE_CACHE_PATH = BasePath + "imagecache/";
    //缓存的音频
    public static final String VOICE_CACHE_PATH = BasePath + "voicecache/";

    public static final String CONV_TITLE = "convTitle";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String TARGET_ID = "targetId";
    public static final String AVATAR = "avatar";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String NOTENAME = "notename";
    public static final String GENDER = "gender";
    public static final String REGION = "region";
    public static final String SIGNATURE = "signature";
    public static final String STATUS = "status";
    public static final String POSITION = "position";
    public static final String MsgIDs = "msgIDs";
    public static final String DRAFT = "draft";
    public static final String DELETE_MODE = "deleteMode";
    public static final String MEMBERS_COUNT = "membersCount";

}
