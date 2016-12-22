package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.GridView

import com.ybg.yxym.yb.bean.SystemLabel
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.LabelAdapter

import java.io.Serializable
import java.util.ArrayList

/**
 * 类描述：我的标签
 */
class MyInterestActivity : BaseActivity() {

    private var gridview: GridView? = null

    private val taste = arrayOf("影视", "小说", "综艺", "动漫", "旅游", "摄影", "美食", "运动", "音乐", "跳舞", "画画", "园艺", "美妆", "穿搭", "逛街", "购物", "烹饪", "DIY", "宠物", "居家", "追星", "星座", "游戏", "养生")

    private var gridViewAdapter: LabelAdapter? = null
    /**这是已经选择过的标签 */
    private var myBiaoqian: MutableList<String>? = null
    /**所有的标签 */
    private var biaoqianList: MutableList<SystemLabel>? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_my_interest
    }

    override fun setUpView() {
        gridview = findViewById(R.id.gridview) as GridView

        mContext = this@MyInterestActivity
        gridViewAdapter = LabelAdapter(mContext!!)
        myBiaoqian = ArrayList<String>()
        biaoqianList = ArrayList<SystemLabel>()
        //val intent = intent
        myBiaoqian = intent.getSerializableExtra("mBiaoqian") as MutableList<String>

        setCustomTitle("我的标签")
    }

    override fun init() {
        initTaste()
        gridViewAdapter!!.setData(biaoqianList!!)
        gridview!!.adapter = gridViewAdapter
        gridview!!.setOnItemClickListener { parent, view, position, id ->
            selectBiaoqian(position)
        }
    }

    /**
     * @param position 选择标签
     */
    private fun selectBiaoqian(position: Int) {
        if (checkIsExist(taste[position])) {//如果选择的标签已经存在
            for (i in myBiaoqian!!.indices) {
                if (myBiaoqian!![i] == taste[position]) {
                    myBiaoqian!!.removeAt(i)
                    initTaste()
                    gridViewAdapter!!.setData(biaoqianList!!)
                    gridViewAdapter!!.notifyDataSetChanged()
                    break
                }
            }
        } else {
            myBiaoqian!!.add(taste[position])
            initTaste()
            gridViewAdapter!!.setData(biaoqianList!!)
            gridViewAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * @param str 检测是否已经选择过这个标签
     * *
     * *
     * @return
     */
    private fun checkIsExist(str: String): Boolean {
        var isExist = false
        for (n in myBiaoqian!!.indices) {
            if (myBiaoqian!![n] == str) {
                isExist = true
                break
            } else {
                isExist = false
            }
        }
        return isExist
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val response = Intent(mContext, MyInformationActivity::class.java)
                response.putExtra("mBiaoqian", myBiaoqian as Serializable?)
                setResult(Activity.RESULT_OK, response)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 检查数据，初始化标签
     */
    private fun initTaste() {
        var isHave = false
        biaoqianList!!.clear()
        for (i in taste.indices) {
            val systemLabel = SystemLabel()
            for (n in myBiaoqian!!.indices) {
                if (myBiaoqian!![n] == taste[i]) {
                    isHave = true
                    break
                } else {
                    isHave = false
                }
            }
            systemLabel.isSelected = isHave
            systemLabel.labelName = taste[i]
            biaoqianList!!.add(systemLabel)
        }
    }

    companion object {

        fun start(context: Context, list: List<String>) {
            val starter = Intent(context, MyInterestActivity::class.java)
            starter.putExtra("mBiaoqian", list as Serializable)
            context.startActivity(starter)
        }
    }
}
