package com.ybg.yxym.yb.bean

import java.io.Serializable
import java.util.Date

/**
 * Created by yangbagang on 2016/10/27.
 */

class YueShow : Serializable {
    var id: Long? = null
    var thumbnail = ""//缩略图
    var title = ""//说明
    var createTime: String? = null//发布时间
    var pingNum: Int = 0//评论次数
    var zanNum: Int = 0//赞次数
    var shareNum: Int = 0//分享次数
    var type: Int = 2//1直播，2是图片，3是视频
    var fileNum: Int = 1//附带文件数量
    var user: UserBase? = null

    override fun toString(): String {
        return "YueShow{" +
                "thumbnail='" + thumbnail + '\'' +
                ", title='" + title + '\'' +
                ", createTime=" + createTime +
                ", pingNum=" + pingNum +
                ", zanNum=" + zanNum +
                ", shareNum=" + shareNum +
                '}'
    }

    companion object {

        private val serialVersionUID = -1080554695148177189L
    }
}
