package com.ybg.yxym.yueshow.view.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.constant.IntentExtra

import java.io.File
import java.util.ArrayList

class MultiImageSelectorActivity : FragmentActivity(), MultiImageSelectorFragment.Callback {

    private var resultList: ArrayList<String>? = ArrayList()
    private var mSubmitButton: Button? = null
    private var mDefaultCount: Int = 0
    private var mMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_default)

        val intent = intent
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9)
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI)
        val isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true)
        if (mMode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST)
        }

        val bundle = Bundle()
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount)
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mMode)
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow)
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList)

        supportFragmentManager.beginTransaction().add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment::class.java.name, bundle)).commit()

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // 完成按钮
        mSubmitButton = findViewById(R.id.commit) as Button
        if (resultList == null || resultList!!.size <= 0) {
            mSubmitButton!!.text = "完成"
            mSubmitButton!!.isEnabled = false
        } else {
            mSubmitButton!!.text = "完成(" + resultList!!.size + "/" + mDefaultCount + ")"
            mSubmitButton!!.isEnabled = true
        }
        mSubmitButton!!.setOnClickListener {
            if (resultList != null && resultList!!.size > 0) {
                // 返回已选择的图片数据
                val data = Intent()
                if (mMode == MODE_SINGLE) {
                    data.putExtra(EXTRA_RESULT, resultList!![0])
                } else {
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList)
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    override fun onSingleImageSelected(path: String) {
        val data = Intent()
        if (mMode == MODE_SINGLE) {
            data.putExtra(EXTRA_RESULT, path)
        } else {
            resultList!!.add(path)
            data.putStringArrayListExtra(EXTRA_RESULT, resultList)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onImageSelected(path: String) {
        if (!resultList!!.contains(path)) {
            resultList!!.add(path)
        }
        // 有图片之后，改变按钮状态
        if (resultList!!.size > 0) {
            mSubmitButton!!.text = "完成(" + resultList!!.size + "/" + mDefaultCount + ")"
            if (!mSubmitButton!!.isEnabled) {
                mSubmitButton!!.isEnabled = true
            }
        }
    }

    override fun onImageUnselected(path: String) {
        if (resultList!!.contains(path)) {
            resultList!!.remove(path)
            mSubmitButton!!.text = "完成(" + resultList!!.size + "/" + mDefaultCount + ")"
        } else {
            mSubmitButton!!.text = "完成(" + resultList!!.size + "/" + mDefaultCount + ")"
        }
        // 当为选择图片时候的状态
        if (resultList!!.size == 0) {
            mSubmitButton!!.text = "完成"
            mSubmitButton!!.isEnabled = false
        }
    }

    override fun onCameraShot(imageFile: File) {
        if (imageFile != null) {
            val data = Intent()
            resultList!!.add(imageFile.absolutePath)
            data.putStringArrayListExtra(EXTRA_RESULT, resultList)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    companion object {

        /**
         * 最大图片选择次数，int类型，默认9
         */
        val EXTRA_SELECT_COUNT = "max_select_count"
        /**
         * 图片选择模式，默认多选
         */
        val EXTRA_SELECT_MODE = "select_count_mode"
        /**
         * 是否显示相机，默认显示
         */
        val EXTRA_SHOW_CAMERA = "show_camera"
        /**
         * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
         */
        val EXTRA_RESULT = "select_result"
        /**
         * 默认选择集
         */
        val EXTRA_DEFAULT_SELECTED_LIST = "default_list"

        /**
         * 单选
         */
        val MODE_SINGLE = 0
        /**
         * 多选
         */
        val MODE_MULTI = 1

        fun start(context: Activity, showCamera: Boolean, maxNum: Int, mode: Int) {
            val starter = Intent(context, MultiImageSelectorActivity::class.java)
            // 是否显示拍摄图片
            starter.putExtra(EXTRA_SHOW_CAMERA, showCamera)
            // 最大可选择图片数量
            starter.putExtra(EXTRA_SELECT_COUNT, maxNum)
            // 选择模式
            starter.putExtra(EXTRA_SELECT_MODE, mode)
            context.startActivityForResult(starter, IntentExtra.RequestCode.REQUEST_CODE_GALLERY)
        }
    }
}
