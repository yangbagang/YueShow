package com.ybg.yxym.yb.bean

/**
 * Created by yangbagang on 2017/4/1.
 */
data class FriendRequest(val id: Long, val fromUser: UserBase, val targetUser: UserBase, val
createTime: String, val reason: String, val status: Int)