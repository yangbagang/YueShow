package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yueshow.R
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

        rv_photo_list.adapter = mImageAdapter
    }

    companion object {

        fun start(context: Context, pics: ArrayList<String>) {
            val starter = Intent(context, PostShowActivity::class.java)
            starter.putStringArrayListExtra(IntentExtra.PICTURE_LIST, pics)
            context.startActivity(starter)
        }
    }

}