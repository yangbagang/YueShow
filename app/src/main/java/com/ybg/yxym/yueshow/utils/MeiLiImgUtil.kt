package com.ybg.yxym.yueshow.utils

import com.ybg.yxym.yueshow.R

/**
 * Created by yangbagang on 2017/1/18.
 */
object MeiLiImgUtil {

    fun getImgId(level: Int): Int {
        when(level) {
            1 -> return R.mipmap.lv1
            2 -> return R.mipmap.lv2
            3 -> return R.mipmap.lv3
            4 -> return R.mipmap.lv4
            5 -> return R.mipmap.lv5
            6 -> return R.mipmap.lv6
            7 -> return R.mipmap.lv7
            8 -> return R.mipmap.lv8
            9 -> return R.mipmap.lv9
            10 -> return R.mipmap.lv10
            11 -> return R.mipmap.lv11
            12 -> return R.mipmap.lv12
            13 -> return R.mipmap.lv13
            14 -> return R.mipmap.lv14
            15 -> return R.mipmap.lv15
            else -> return R.mipmap.lv1
        }
    }

}