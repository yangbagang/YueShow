package com.ybg.yxym.yueshow.activity.live

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ybg.yxym.yb.bean.LiveMsg
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.gift.GiftListActivity
import com.ybg.yxym.yueshow.adapter.LiveMsgAdapter
import com.ybg.yxym.yueshow.adapter.UserAvatarAdapter
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.decoration.SpaceItemDecoration
import com.ybg.yxym.yueshow.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by yangbagang on 2017/1/26.
 */
abstract class LivingBaseActivity : Activity() {

    lateinit var chatList: ListView
    var msgList: MutableList<LiveMsg> = ArrayList<LiveMsg>()
    lateinit var msgAdapter: LiveMsgAdapter

    lateinit var userRecycleList: RecyclerView
    lateinit var userAvatarAdapter: UserAvatarAdapter
    var userList: MutableList<UserBase> = ArrayList<UserBase>()

    lateinit var liveToolBar: RelativeLayout
    lateinit var chatImage: ImageView
    lateinit var shareImage: ImageView
    lateinit var giftImage: ImageView
    lateinit var liveMsgTool: LinearLayout
    lateinit var msgText: EditText
    lateinit var sendMsg: Button

    protected var mApplication: ShowApplication = ShowApplication.instance!!
    protected var mContext: Activity? = null
    protected var mGson: Gson? = null

    protected abstract fun setContentViewId(): Int

    protected abstract fun setUpView()

    protected abstract fun init()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setContentViewId())
        mContext = this
        mGson = GsonBuilder().serializeNulls().create()
        setContent()
        val lp = mContext!!.window.attributes
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        mContext!!.window.attributes = lp
    }

    private fun setContent() {
        setUpView()
        init()
    }

    fun initLiveBase() {
        initMsgList()
        initUserList()
        initBottomBar()
    }

    /**
     * 初始化消息列表
     */
    private fun initMsgList() {
        chatList = findViewById(R.id.chatList) as ListView
        msgAdapter = LiveMsgAdapter(mContext!!)
        msgAdapter.setDataList(msgList)
        chatList.adapter = msgAdapter
    }

    /**
     * 初始化用户列表
     */
    private fun initUserList() {
        userRecycleList = findViewById(R.id.userRecycleList) as RecyclerView
        userAvatarAdapter = UserAvatarAdapter(mContext!!)
        userAvatarAdapter.setDataList(userList)
        userRecycleList.adapter = userAvatarAdapter
        val layoutManager = LinearLayoutManager.HORIZONTAL
        userRecycleList.layoutManager = LinearLayoutManager(mContext!!, layoutManager, false)
        userRecycleList.itemAnimator = DefaultItemAnimator()
        userRecycleList.addItemDecoration(SpaceItemDecoration(12))
    }

    /**
     * 初始化底部工具栏
     */
    private fun initBottomBar() {
        liveToolBar = findViewById(R.id.liveToolBar) as RelativeLayout
        chatImage = findViewById(R.id.chatImage) as ImageView
        shareImage = findViewById(R.id.shareImage) as ImageView
        giftImage = findViewById(R.id.giftImage) as ImageView
        liveMsgTool = findViewById(R.id.liveMsgTool) as LinearLayout
        msgText = findViewById(R.id.msgText) as EditText
        sendMsg = findViewById(R.id.sendMsg) as Button

        chatImage.setOnClickListener {
            liveToolBar.visibility = View.GONE
            liveMsgTool.visibility = View.VISIBLE
        }

        shareImage.setOnClickListener {
            //分享
        }

        giftImage.setOnClickListener {
            //送礼
            openGiftWin()
        }

        sendMsg.setOnClickListener {
            //发送消息
            val msg = msgText.text.toString()
            if (TextUtils.isEmpty(msg)) {
                ToastUtil.show("消息内容不能为空。")
            } else {
                sendLiveMsg(msg, 0, {
                    runOnUiThread {
                        msgText.setText("")
                        liveToolBar.visibility = View.VISIBLE
                        liveMsgTool.visibility = View.GONE
                    }
                })
            }
        }
    }

    fun addMsg(liveMsg: LiveMsg) {
        msgList.add(liveMsg)
        msgAdapter.notifyDataSetChanged()
    }

    fun userEnter(userBase: UserBase) {
        removeUser(userBase)
        userList.add(userBase)
        userAvatarAdapter.setDataList(userList)
        userAvatarAdapter.notifyDataSetChanged()
    }

    fun userLeave(userBase: UserBase) {
        removeUser(userBase)
        userAvatarAdapter.setDataList(userList)
        userAvatarAdapter.notifyDataSetChanged()
    }

    private fun removeUser(userBase: UserBase) {
        val iterator = userList.iterator()
        while (iterator.hasNext()) {
            val user = iterator.next()
            if (user.id == userBase.id) {
                iterator.remove()
            }
        }
    }

    open fun openGiftWin(){}

    abstract fun sendLiveMsg(msg: String, flag: Int, call: () -> Unit)
}