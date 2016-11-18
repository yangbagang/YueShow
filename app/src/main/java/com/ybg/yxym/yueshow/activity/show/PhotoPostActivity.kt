package com.ybg.yxym.yueshow.activity.show

import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.adapter.RecyclerBaseAdapter
import com.ybg.yxym.yueshow.adapter.SelectedImageAdapter
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_post.*
import java.util.*

/**
 * Created by yangbagang on 2016/11/17.
 */
class PhotoPostActivity : PostShowActivity() {

    private lateinit var mImageAdapter: SelectedImageAdapter
    private var mPics: MutableList<String> = ArrayList<String>()

    override fun setContentViewId(): Int {
        return R.layout.activity_photo_post
    }

    override fun setUpView() {

    }

    override fun init() {
        initSelectedImageBar()

        if (intent != null) {
            //拍照入口
            val mPath = String.format("file://%s", intent.getStringExtra(IntentExtra
                    .EXTRA_PHOTO_RESULT))
            if (mPath != "") {
                mPics.add(mPath)
            }
            //文件选择入口
            val pics = intent.getStringArrayListExtra(IntentExtra.PICTURE_LIST)
            if (pics.isNotEmpty()) {
                mPics.clear()
                mPics.addAll(pics)
                mImageAdapter.notifyDataSetChanged()
            }
        }

        setCustomTitle(getString(R.string.post))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_finish) {
            //下一步
            ToastUtil.show("完成。。。")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //初始化图片选择框
    private fun initSelectedImageBar() {
        rv_photo_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        mImageAdapter = SelectedImageAdapter(mContext!!)
        mImageAdapter.setDataList(mPics)
//        mImageAdapter.setOnItemClickListener(object : RecyclerBaseAdapter.OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                mPath = mPics[position]
//                setCurrentImage()
//            }
//        })

        rv_photo_list.adapter = mImageAdapter
    }
}