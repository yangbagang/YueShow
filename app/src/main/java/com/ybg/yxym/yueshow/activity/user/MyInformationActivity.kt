package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView

import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.InterestGridViewAdapter
import com.ybg.yxym.yueshow.adapter.MyInfoAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.utils.OnoptionsUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CustomerGridView
import com.ybg.yxym.yueshow.view.pickerview.OptionsPopupWindow
import com.ybg.yxym.yueshow.view.pickerview.TimePopupWindow

import java.io.Serializable
import java.util.ArrayList
import java.util.Date

/**
 * 类描述：个人资料
 */
class MyInformationActivity : BaseActivity() {

    private var lvInfo: ListView? = null
    private var llAll: LinearLayout? = null

    private val REQUEST_BIAOQIAN = 0
    private val REQUEST_NICKNAME = 1
    private val REQUEST_PROFESSION = 2
    private val REQUEST_SIGN_NAME = 3
    private val REQUEST_PLACES = 4

    private var strlist: MutableList<String>? = null
    private var adapter: MyInfoAdapter? = null
    private var setPhoto: ImageView? = null
    private var gridViewAdapter: InterestGridViewAdapter? = null
    private var gridView: CustomerGridView? = null
    private var intlist: MutableList<String>? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_my_information
    }

    override fun setUpView() {
        lvInfo = findViewById(R.id.lv_info) as ListView
        llAll = findViewById(R.id.ll_all) as LinearLayout

        mContext = this@MyInformationActivity
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val footView = inflater.inflate(R.layout.item_my_info_foot, null)
        val headerView = inflater.inflate(R.layout.item_my_info_head, null)
        lvInfo!!.addFooterView(footView)
        lvInfo!!.addHeaderView(headerView)
        setPhoto = headerView.findViewById(R.id.iv_set_photo) as ImageView
        setPhoto!!.setOnClickListener { ToastUtil.show("设置头像") }
        gridView = footView.findViewById(R.id.gridview) as CustomerGridView
        gridView!!.setOnItemClickListener { parent, view, position, id ->
            if (position == intlist!!.size) {
                val starter = Intent(mContext, MyInterestActivity::class.java)
                starter.putExtra("mBiaoqian", intlist as Serializable?)
                startActivityForResult(starter, REQUEST_BIAOQIAN)
            }
        }
        setCustomTitle("个人资料")
    }

    override fun init() {
        strlist = ArrayList<String>()
        adapter = MyInfoAdapter(mContext!!)
        getUserData()
        adapter!!.setData(strlist!!)
        lvInfo!!.adapter = adapter
        gridViewAdapter = InterestGridViewAdapter(mContext!!)
        intlist = ArrayList<String>()
        gridViewAdapter!!.setData(intlist!!)
        gridView!!.adapter = gridViewAdapter

        lvInfo!!.setOnItemClickListener { parent, view, position, id ->
            if (position == 2) {
                val up_msg = Intent(mContext, UpdataUserMsgActivity::class.java)
                up_msg.putExtra("type", "昵称")
                startActivityForResult(up_msg, REQUEST_NICKNAME)
            } else if (position == 5) {
                val up_msg = Intent(mContext, UpdataUserMsgActivity::class.java)
                up_msg.putExtra("type", "职业")
                startActivityForResult(up_msg, REQUEST_PROFESSION)
            } else if (position == 10) {
                val up_msg = Intent(mContext, UpdataUserMsgActivity::class.java)
                up_msg.putExtra("type", "个性签名")
                startActivityForResult(up_msg, REQUEST_SIGN_NAME)
            } else if (position == 3) {
                OnoptionsUtils.showDateSelect(mContext!!, llAll!!, object : TimePopupWindow.OnTimeSelectListener {
                    override fun onTimeSelect(date: Date) {
                        strlist!![2] = DateUtil.format(date)
                        adapter!!.setData(strlist!!)
                        adapter!!.notifyDataSetChanged()
                    }
                })
            } else if (position == 6) {
                OnoptionsUtils.showheight(mContext!!, llAll!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist!![5] = "${options1}cm"
                        adapter!!.setData(strlist!!)
                        adapter!!.notifyDataSetChanged()
                    }
                })
            } else if (position == 7) {
                OnoptionsUtils.showWeight(mContext!!, llAll!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist!![6] = "${options1}kg"
                        adapter!!.setData(strlist!!)
                        adapter!!.notifyDataSetChanged()
                    }
                })
            } else if (position == 8) {
                OnoptionsUtils.showShencai(mContext!!, llAll!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist!![7] = transforCup(options1) + "" + option2 + "-" + options3 + "-" + options4
                        adapter!!.setData(strlist!!)
                        adapter!!.notifyDataSetChanged()
                    }
                })
            } else if (position == 9) {
                val up_place = Intent(mContext, SelectPlaceActivity::class.java)
                startActivityForResult(up_place, REQUEST_PLACES)
            }
        }
    }

    /**
     * get USER Data
     */
    private fun getUserData() {
        val preferences = getSharedPreferences(AppConstants.SHARE_PREFERENCE_USER, Context.MODE_PRIVATE)
        strlist!!.add(preferences.getString(AppConstants.USER_ID, ""))
        strlist!!.add(preferences.getString(AppConstants.USER_NICKNAME, ""))
        strlist!!.add(preferences.getString(AppConstants.USER_BIRTHDAY, ""))
        val sex: String
        if ("1" == preferences.getString(AppConstants.USER_SEX, ""))
            sex = "男"
        else
            sex = "女"
        strlist!!.add(sex)
        strlist!!.add("Android工程师")
        strlist!!.add(preferences.getString(AppConstants.USER_HEIGHT, "")!! + "cm")
        strlist!!.add(preferences.getString(AppConstants.USER_WEIGHT, "" + "kg"))
        strlist!!.add("C90-70-80")
        strlist!!.add(preferences.getString(AppConstants.USER_ADDRESS, ""))
        strlist!!.add(preferences.getString(AppConstants.USER_MOTTO, ""))
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> finish()
        }//            case R.id.tv_dress_up:
        //                DressUpActivity.start(mContext);
        //                break;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_BIAOQIAN) {
            intlist!!.clear()
            intlist = data.getSerializableExtra("mBiaoqian") as MutableList<String>
            gridViewAdapter!!.setData(intlist!!)
            gridViewAdapter!!.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_NICKNAME) {
            strlist!![1] = data.getStringExtra("nickname")
            adapter!!.setData(strlist!!)
            adapter!!.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PROFESSION) {
            strlist!![4] = data.getStringExtra("profession")
            adapter!!.setData(strlist!!)
            adapter!!.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SIGN_NAME) {
            strlist!![9] = data.getStringExtra("signname")
            adapter!!.setData(strlist!!)
            adapter!!.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PLACES) {
            strlist!![8] = data.getStringExtra("place")
            adapter!!.setData(strlist!!)
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * @param i
     * *
     * @return
     */
    private fun transforCup(i: Int): String {
        var i = i
        if (i < 0) {
            i = 0
        } else if (i > 7) {
            i = 7
        }
        return OnoptionsUtils.cups[i]
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, MyInformationActivity::class.java)
            context.startActivity(starter)
        }
    }
}
