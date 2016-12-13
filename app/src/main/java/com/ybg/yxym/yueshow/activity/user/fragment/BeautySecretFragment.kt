package com.ybg.yxym.yueshow.activity.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

class BeautySecretFragment : BaseFragment() {

    private var ivMeiliSecret: ImageView? = null
    private var rlMeilizhiSecret: RelativeLayout? = null

    override fun init() {

    }

    override fun setContentViewId(): Int {
        return R.layout.fragment_meili_secret
    }

    override fun setUpView() {
        ivMeiliSecret = mRootView!!.findViewById(R.id.iv_meili_secret) as ImageView
        rlMeilizhiSecret = mRootView!!.findViewById(R.id.rl_meilizhi_secret) as RelativeLayout
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        var inst: BeautySecretFragment? = null
        fun getInstance(): BeautySecretFragment {
            if (inst == null) {
                inst = BeautySecretFragment()
            }
            return inst!!
        }
    }
}
