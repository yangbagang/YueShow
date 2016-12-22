package com.ybg.yxym.yb.bean

import java.io.Serializable

/**
 * Created by yangbagang on 2016/10/28.
 */

class SystemLabel : Serializable {

    var labelName: String = ""
    var catalog: String = ""
    var isSelected: Boolean = false

    override fun toString(): String {
        return "SystemLabel{" +
                "labelName='" + labelName + '\'' +
                ", catalog='" + catalog + '\'' +
                ", isSelected=" + isSelected +
                '}'
    }

    companion object {

        private val serialVersionUID = 2617169362016636495L
    }
}
