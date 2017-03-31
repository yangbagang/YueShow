package com.ybg.yxym.yb.bean

/**
 * Created by yangbagang on 2017/3/31.
 */
data class UserDate(val userBase: UserBase, val fromUser: UserBase, val createTime: String,
                    val dateDate: String, val type: String, val content: String, val flag: Int)