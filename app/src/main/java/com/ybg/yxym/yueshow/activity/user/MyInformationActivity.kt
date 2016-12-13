package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.ybg.yxym.yb.utils.AppUtil
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.InterestGridViewAdapter
import com.ybg.yxym.yueshow.adapter.MyInfoAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.listener.UploadListener
import com.ybg.yxym.yueshow.utils.BitmapUtils
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.OnoptionsUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView
import com.ybg.yxym.yueshow.view.CustomerGridView
import com.ybg.yxym.yueshow.view.gallery.MultiImageSelectorActivity
import com.ybg.yxym.yueshow.view.pickerview.OptionsPopupWindow
import com.ybg.yxym.yueshow.view.pickerview.TimePopupWindow
import kotlinx.android.synthetic.main.activity_my_information.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.util.*

/**
 * 类描述：个人资料
 */
class MyInformationActivity : BaseActivity() {

    private val REQUEST_BIAOQIAN = 0
    private val REQUEST_NICKNAME = 1
    private val REQUEST_PROFESSION = 2
    private val REQUEST_SIGN_NAME = 3
    private val REQUEST_PLACES = 4

    private lateinit var strlist: MutableList<String>
    private lateinit var adapter: MyInfoAdapter
    private var setPhoto: ImageView? = null
    private var gridViewAdapter: InterestGridViewAdapter? = null
    private var gridView: CustomerGridView? = null
    private lateinit var intlist: MutableList<String>

    private lateinit var userAvatar: CircleImageView
    private var mAvatar = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_my_information
    }

    override fun setUpView() {
        mContext = this@MyInformationActivity
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val footView = inflater.inflate(R.layout.item_my_info_foot, null)
        val headerView = inflater.inflate(R.layout.item_my_info_head, null)
        lv_info.addFooterView(footView)
        lv_info.addHeaderView(headerView)
        setPhoto = headerView.findViewById(R.id.iv_set_photo) as ImageView
        userAvatar = headerView.findViewById(R.id.iv_user_logo) as CircleImageView
        setPhoto!!.setOnClickListener {
            MultiImageSelectorActivity.start(mContext!!, true, 1,
                    MultiImageSelectorActivity.MODE_SINGLE)
        }
        gridView = footView.findViewById(R.id.gridview) as CustomerGridView
        gridView!!.setOnItemClickListener { parent, view, position, id ->
            if (position == intlist.size) {
                val starter = Intent(mContext, MyInterestActivity::class.java)
                starter.putExtra("mBiaoqian", intlist as Serializable?)
                startActivityForResult(starter, REQUEST_BIAOQIAN)
            }
        }
        setCustomTitle("编辑个人资料")
    }

    override fun init() {
        strlist = ArrayList<String>()
        adapter = MyInfoAdapter(mContext!!)
        initUserData()
        getUserData()
        adapter.setData(strlist)
        lv_info!!.adapter = adapter
        gridViewAdapter = InterestGridViewAdapter(mContext!!)
        intlist = ArrayList<String>()
        gridViewAdapter!!.setData(intlist)
        gridView!!.adapter = gridViewAdapter

        lv_info!!.setOnItemClickListener { parent, view, position, id ->
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
                OnoptionsUtils.showDateSelect(mContext!!, ll_all!!, object : TimePopupWindow.OnTimeSelectListener {
                    override fun onTimeSelect(date: Date) {
                        strlist[2] = DateUtil.format(date)
                        adapter.setData(strlist)
                        adapter.notifyDataSetChanged()
                    }
                })
            } else if (position == 6) {
                OnoptionsUtils.showheight(mContext!!, ll_all!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist[5] = "${options1}cm"
                        adapter.setData(strlist)
                        adapter.notifyDataSetChanged()
                    }
                })
            } else if (position == 7) {
                OnoptionsUtils.showWeight(mContext!!, ll_all!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist[6] = "${options1}kg"
                        adapter.setData(strlist)
                        adapter.notifyDataSetChanged()
                    }
                })
            } else if (position == 8) {
                OnoptionsUtils.showShencai(mContext!!, ll_all!!, object : OptionsPopupWindow.OnOptionsSelectListener {
                    override fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int) {
                        strlist[7] = transforCup(options1) + "" + option2 + "-" + options3 + "-" + options4
                        adapter.setData(strlist)
                        adapter.notifyDataSetChanged()
                    }
                })
            } else if (position == 9) {
                val up_place = Intent(mContext, SelectPlaceActivity::class.java)
                startActivityForResult(up_place, REQUEST_PLACES)
            }
        }
    }

    private fun initUserData() {
        strlist.add("")//"悦美号", , , , , , , , ,
        strlist.add("")//"昵称:"
        strlist.add("")//"生日:"
        strlist.add("")//"性别:"
        strlist.add("")//"职业:"
        strlist.add("cm")//"身高:"
        strlist.add("kg")//"体重:"
        strlist.add("C90-70-80")//"身材:"
        strlist.add("")//"地区:"
        strlist.add("")//"个性签名:"
    }

    /**
     * get USER Data
     */
    private fun getUserData() {
        //获取服务器数据
        loadUserBase { userBase ->
            strlist[0] = userBase.ymCode
            strlist[1] = userBase.nickName
            strlist[9] = userBase.ymMemo
            adapter.notifyDataSetChanged()
        }
        loadUserInfo { userInfo ->
            strlist[2] = userInfo.birthday
            adapter.notifyDataSetChanged()
        }
    }

    fun onClick(view: View) {
//        when (view.id) {
//            R.id.iv_back -> finish()
//        }//            case R.id.tv_dress_up:
//        //                DressUpActivity.start(mContext);
//        //                break;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_BIAOQIAN) {
            intlist.clear()
            intlist = data.getSerializableExtra("mBiaoqian") as MutableList<String>
            gridViewAdapter!!.setData(intlist)
            gridViewAdapter!!.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_NICKNAME) {
            strlist[1] = data.getStringExtra("nickname")
            adapter.setData(strlist)
            adapter.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PROFESSION) {
            strlist[4] = data.getStringExtra("profession")
            adapter.setData(strlist)
            adapter.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SIGN_NAME) {
            strlist[9] = data.getStringExtra("signname")
            adapter.setData(strlist)
            adapter.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PLACES) {
            strlist[8] = data.getStringExtra("place")
            adapter.setData(strlist)
            adapter.notifyDataSetChanged()
        } else if (requestCode == IntentExtra.RequestCode.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val path = data.getStringExtra(MultiImageSelectorActivity.EXTRA_RESULT)
            if (path == "") return
            ImageLoaderUtils.instance.loadFileBitmap(userAvatar, path!!)
            saveAvatar(path)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_save) {
            updateUserBase()
            updateUserInfo()
            updateUserLabel()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveAvatar(path: String) {

        object : AsyncTask<String, String, String>(){

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result == null) {
                    return
                }
                uploadAvatar(result)
            }

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: String?): String {
                if (p0 == null) return ""
                //取得上传目标
                var bitmap = BitmapFactory.decodeFile(path)
                //压缩尺寸
                bitmap = BitmapUtils.resizeImage(bitmap, 300, 300)
                //保存
                return BitmapUtils.saveToFile(AppConstants.IMAGE_CACHE_PATH, true, bitmap)
            }

        }.execute()
    }

    private fun uploadAvatar(path: String) {
        SendRequest.uploadFile(mContext!!, "avatar", File(path), object: UploadListener(){
            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(it) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                mAvatar = json.getString("fid")
            }

            override fun onFailure(e: Exception) {
                workInLoopThread {
                    ToastUtil.show("上传头像失败")
                }
            }

            override fun onUIProgress(progress: Progress) {

            }
        })
    }

    private fun updateUserInfo() {

    }

    private fun updateUserBase() {

    }

    private fun updateUserLabel() {

    }

    /**
     * @param i
     * *
     * @return
     */
    private fun transforCup(i: Int): String {
        var index = i
        if (index < 0) {
            index = 0
        } else if (i > 7) {
            index = 7
        }
        return OnoptionsUtils.cups[index]
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, MyInformationActivity::class.java)
            context.startActivity(starter)
        }
    }
}
