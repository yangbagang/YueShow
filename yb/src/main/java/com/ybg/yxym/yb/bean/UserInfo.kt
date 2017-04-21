package com.ybg.yxym.yb.bean

import java.io.Serializable
import java.util.Date

/**
 * Created by yangbagang on 2016/10/27.
 */

class UserInfo : Serializable {
    var id = 0L
    var birthday: String = ""//生日
    var sex: Int = 1//性别1男0女
    var position = "未选择"//职位
    var bodyHigh: Int = 0//身高，单位cm
    var bodyWeight: Int = 0//体重，单位kg
    var cupSize = ""//罩杯
    var bust: Int = 0//胸围bust 单位cm
    var waist: Int = 0//腰围waist 单位cm
    var hips: Int = 0//臀围hips 单位cm
    var province = ""//省份
    var city = ""//市

    override fun toString(): String {
        return "UserInfo{" +
                "birthday=" + birthday +
                ", sex=" + sex +
                ", position='" + position + '\'' +
                ", bodyHigh=" + bodyHigh +
                ", bodyWeight=" + bodyWeight +
                ", cupSize='" + cupSize + '\'' +
                ", bust=" + bust +
                ", waist=" + waist +
                ", hips=" + hips +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                '}'
    }

    companion object {

        private val serialVersionUID = -5719117491648774918L
    }
}
