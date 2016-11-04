package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView

import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.PlaceAdapter

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

/**
 * 类描述：选择地区
 */
class SelectPlaceActivity : BaseActivity() {

    private var lv: ListView? = null

    private var TYPE_PROVINCE = true
    private var prolist: MutableList<String>? = null
    private var citylist: MutableList<List<String>>? = null
    private var adapter: PlaceAdapter? = null
    private var place: String? = null//返回的数据
    private var province = ""
    private var provinceTay: Int = 0

    override fun setContentViewId(): Int {
        return R.layout.activity_select_place
    }

    override fun setUpView() {
        lv = findViewById(R.id.lv) as ListView

        mContext = this@SelectPlaceActivity
        setCustomTitle("选择省份")
    }

    override fun init() {
        prolist = ArrayList<String>()
        citylist = ArrayList<List<String>>()
        adapter = PlaceAdapter(mContext!!)
        getChinaData(readAssertResource(mContext!!, "China_province_city.txt"))
        adapter!!.setData(prolist!!)
        lv!!.adapter = adapter
        lv!!.onItemClickListener = onItemClickListener
    }


    /**
     * item 点击
     */
    private val onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (TYPE_PROVINCE) {
            adapter!!.setData(citylist!![position])
            adapter!!.notifyDataSetChanged()
            //tvTitle.setText("选择城市");
            province = prolist!![position]
            provinceTay = position
            TYPE_PROVINCE = false
        } else {
            if (province == citylist!![provinceTay][position]) {
                place = "中国" + province
            } else {
                place = "中国" + province + citylist!![provinceTay][position]
            }
            val response = Intent(mContext, MyInformationActivity::class.java)
            response.putExtra("place", place)
            setResult(Activity.RESULT_OK, response)
            finish()
        }
    }

    fun onClick() {
        if (TYPE_PROVINCE) {
            finish()
        } else {
            //            tvTitle.setText("选择省份");
            //            adapter.setData(prolist);
            //            adapter.notifyDataSetChanged();
            TYPE_PROVINCE = true
        }
    }


    /**
     * @param str
     */
    private fun getChinaData(str: String) {
        LogUtil.d(str)
        try {
            val jsonArray = JSONArray(str)
            for (i in 0..jsonArray.length() - 1) {
                val jsonObject = JSONObject(jsonArray.get(i).toString())
                prolist!!.add(jsonObject.getString("name"))
                val array = JSONArray(jsonObject.getString("city"))
                val stringList = ArrayList<String>()
                for (j in 0..array.length() - 1) {
                    val obj = JSONObject(array.get(j).toString())
                    stringList.add(obj.getString("name"))
                }
                citylist!!.add(i, stringList)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        LogUtil.d("${citylist!!.size}---")
    }

    companion object {


        /**
         * @param context
         * *
         * @param strAssertFileName
         * *
         * @return
         */
        fun readAssertResource(context: Context, strAssertFileName: String): String {
            val assetManager = context.assets
            var strResponse = ""
            try {
                val ims = assetManager.open(strAssertFileName)
                strResponse = getStringFromInputStream(ims)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return strResponse
        }

        /**
         * @param a_is
         * *
         * @return
         */
        private fun getStringFromInputStream(a_is: InputStream): String {
            var br: BufferedReader? = null
            val sb = StringBuilder()
            var line: String
            try {
                br = BufferedReader(InputStreamReader(a_is))
                line = br.readLine()
                while (line != null) {
                    sb.append(line)
                    line = br.readLine()
                }
            } catch (e: IOException) {
            } finally {
                if (br != null) {
                    try {
                        br.close()
                    } catch (e: IOException) {
                    }

                }
            }
            return sb.toString()
        }
    }

}
