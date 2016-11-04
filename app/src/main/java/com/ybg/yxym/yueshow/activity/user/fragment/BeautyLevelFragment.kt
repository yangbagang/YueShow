package com.ybg.yxym.yueshow.activity.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

import java.util.ArrayList

class BeautyLevelFragment : BaseFragment() {

    private val stringList = ArrayList<String>()
    private val adapter: ArrayAdapter<*>? = null

    override fun setContentViewId(): Int {
        return R.layout.fragment_meilizhi_level
    }

    override fun setUpView() {

    }

    override fun init() {

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        var instance: BeautyLevelFragment? = null
            get() {
                if (instance == null) {
                    instance = BeautyLevelFragment()
                }
                return instance
            }
    }

}
