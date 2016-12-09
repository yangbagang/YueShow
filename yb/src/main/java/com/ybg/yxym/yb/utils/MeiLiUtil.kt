package com.ybg.yxym.yb.utils

/**
 * Created by yangbagang on 2016/12/9.
 */
object MeiLiUtil {

    val names = arrayOf("如花新人", "如花新人", "如花新人", "闪亮新星", "闪亮新星", "闪亮新星",
            "人气偶像", "人气偶像", "人气偶像", "秀美大师", "秀美大师", "秀美大师", "超级大神", "超级大神", "超级大神")

    val LV1 = 0
    val LV2 = 1100
    val LV3 = 3100
    val LV4 = 9100
    val LV5 = 13100
    val LV6 = 35200
    val LV7 = 135000
    val LV8 = 288000
    val LV9 = 580000
    val LV10 = 588000
    val LV11 = 980000
    val LV12 = 1380000
    val LV13 = 58800000
    val LV14 = 68000000
    val LV15 = 128000000

    fun getLevelNum(score: Int): Int {
        if (score >= LV15) return 15
        if (score >= LV14) return 14
        if (score >= LV13) return 13
        if (score >= LV12) return 12
        if (score >= LV11) return 11
        if (score >= LV10) return 10
        if (score >= LV9) return 9
        if (score >= LV8) return 8
        if (score >= LV7) return 7
        if (score >= LV6) return 6
        if (score >= LV5) return 5
        if (score >= LV4) return 4
        if (score >= LV3) return 3
        if (score >= LV2) return 2
        return 1
    }

    fun getLevelName(levelNum: Int): String {
        if (levelNum !in 0..names.size) return "未知"
        return names[levelNum]
    }

}