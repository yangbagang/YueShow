package com.ybg.yxym.yb.utils

import com.ybg.yxym.yb.bean.UserBase

/**
 * Created by yangbagang on 2017/3/30.
 */

class UserBaseComparator : java.util.Comparator<UserBase> {

    override fun compare(u1: UserBase, u2: UserBase): Int {
        // 获取ascii值
        val lhs_ascii = u1.getFirstPY().toUpperCase()[0].toInt()
        val rhs_ascii = u2.getFirstPY().toUpperCase()[0].toInt()
        // 判断若不是字母，则排在字母之后
        if (lhs_ascii < 65 || lhs_ascii > 90)
            return 1
        else if (rhs_ascii < 65 || rhs_ascii > 90)
            return -1
        else
            return u1.getFirstPY().compareTo(u2.getFirstPY())
    }

}
