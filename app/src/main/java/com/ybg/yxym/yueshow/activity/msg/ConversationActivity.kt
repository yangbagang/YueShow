package com.ybg.yxym.yueshow.activity.msg

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation

class ConversationActivity : BaseActivity(), RongIM.LocationProvider, RongIM.ConversationBehaviorListener {

    private var mTargetId: String? = null
    private var mTargetIds: String? = null
    private var mConversationType: Conversation.ConversationType? = null
    private var onSendMessageListener: RongIM.OnSendMessageListener? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_conversation
    }

    override fun setUpView() {

    }

    override fun init() {

    }

}
