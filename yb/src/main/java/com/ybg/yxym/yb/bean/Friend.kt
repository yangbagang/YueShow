package com.ybg.yxym.yb.bean

import java.io.Serializable

/**
 * Created by yangbagang on 2017/4/21.
 */
class Friend : Serializable {
    var id = 0L
    var friend: UserBase? = null
    var createTime: String? = null
    var nickName = ""
    var disturbing = 1
    var inBlacklist = 0

    override fun toString(): String {
        return "Friend(friend=$friend, createTime=$createTime, nickName='$nickName', disturbing=$disturbing, inBlacklist=$inBlacklist)"
    }

}