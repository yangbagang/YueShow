package com.ybg.yxym.yb.bean

import java.io.Serializable

/**
 * Created by yangbagang on 2016/10/27.
 */
class UserBase : Serializable {
    var id = 0L
    var ymCode = "0"//悦美号，唯一，非空，自动生成，不可修改。
    var ymUser = ""//悦美账号，通常是手机号
    var nickName = "未填写"//呢称，可空，非唯一。
    var avatar = ""//头像
    var avatarBG = ""//头像背景
    var ymMemo = ""//宣言
    var flag = 1//扩展用字段，为1可以关注，0不能关注。
    var ml = 0//扩展用字段，记录美秀美力值。

    override fun toString(): String {
        return "UserBase{" +
                "ymCode=" + ymCode +
                ", ymUser='" + ymUser + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", avatarBG='" + avatarBG + '\'' +
                ", ymMemo='" + ymMemo + '\'' +
                '}'
    }

    companion object {

        private val serialVersionUID = 7898988229535273928L
    }
}
