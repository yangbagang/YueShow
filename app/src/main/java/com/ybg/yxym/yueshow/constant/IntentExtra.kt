package com.ybg.yxym.yueshow.constant

class IntentExtra {

    object RequestCode {
        val REQUEST_CODE_CAMERA = 1000//相机
        val REQUEST_CODE_GALLERY = 1001//相册
        val REQUEST_CODE_CROP = 1002//裁剪
        val REQUEST_CODE_EDIT = 1003//编辑
        val RECORDE_SHOW = 1004//趣拍短视频部分
        val REQUEST_CODE_REGISTER = 1005//注册完善资料
    }

    companion object {

        val EXTRA_PASSWORD = "extra_password"
        val EXTRA_MOBILE = "extra_mobile"
        val EXTRA_PHOTO_RESULT = "extra_photo_result"
        val PICTURE_LIST = "picture_list"
    }
}
