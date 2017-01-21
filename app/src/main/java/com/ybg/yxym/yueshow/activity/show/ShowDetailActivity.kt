package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pili.pldroid.player.AVOptions
import com.pili.pldroid.player.widget.PLVideoView
import com.tencent.mm.sdk.modelmsg.SendMessageToWX
import com.tencent.mm.sdk.modelmsg.WXMediaMessage
import com.tencent.mm.sdk.modelmsg.WXWebpageObject
import com.tencent.mm.sdk.openapi.IWXAPI
import com.ybg.yxym.yb.bean.*
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.PingItemAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.decoration.SpaceItemDecoration
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ScreenUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.BannerFrame
import com.ybg.yxym.yueshow.view.CircleImageView
import com.ybg.yxym.yueshow.view.MediaController
import kotlinx.android.synthetic.main.activity_home_show_detail.*

/**
 * Created by yangbagang on 2016/12/5.
 */
class ShowDetailActivity : BaseActivity() {

    private lateinit var show: YueShow
    //事件定义
    private val commentOnClickListener = BtnCommentOnClickListener()
    private val zanOnClickListener = BtnZanOnClickListener()
    private val transOnClickListener = BtnTransOnClickListener()
    private val photoOnClickListener = BtnPhotoOnClickListener()
    //UI组件
    private lateinit var authorAvatar: CircleImageView
    private lateinit var authorNickName: TextView
    private lateinit var authorLevel: ImageView
    private lateinit var authorMeiLi: TextView
    private lateinit var postTime: TextView
    private lateinit var authorBtn: Button
    private lateinit var showContent: TextView
    private lateinit var zanLayout: LinearLayout
    private lateinit var zanNum: TextView

    private lateinit var pingAdapter: PingItemAdapter
    private lateinit var pingRecyclerView: RecyclerView
    private lateinit var pingHeaderView: RecyclerViewHeader

    private var w = 0

    //视频播放相关
    private val mIsLiveStreaming = 0
    private var hasVideo = false

    override fun setContentViewId(): Int {
        return R.layout.activity_home_show_detail
    }

    override fun setUpView() {
        authorAvatar = findViewById(R.id.iv_user_logo) as CircleImageView
        authorNickName = findViewById(R.id.tv_user_name) as TextView
        authorLevel = findViewById(R.id.iv_level_img) as ImageView
        authorMeiLi = findViewById(R.id.tv_meilizhi) as TextView
        postTime = findViewById(R.id.tv_time) as TextView
        authorBtn = findViewById(R.id.btn_chat) as Button
        authorBtn.text = "约会"
        showContent = findViewById(R.id.tv_fu_content) as TextView
        zanLayout = findViewById(R.id.ll_user_like_list) as LinearLayout
        zanNum = findViewById(R.id.tv_like_num) as TextView
        pingRecyclerView = findViewById(R.id.recycleview) as RecyclerView
        pingHeaderView = findViewById(R.id.header) as RecyclerViewHeader

        w = ScreenUtils.getScreenWidth(mContext!!)

        setCustomTitle("查看悦秀")
    }

    override fun init() {
        if (intent != null) {
            val showItem = intent.getSerializableExtra("show")
            if (showItem is YueShow) {
                show = showItem
                //开始展示美秀
                displayRuiShow()
                //click event
                iv_comment.setOnClickListener(commentOnClickListener)
                iv_like.setOnClickListener(zanOnClickListener)
                iv_transmit.setOnClickListener(transOnClickListener)
                //初始化适配器
                pingAdapter = PingItemAdapter()
                pingRecyclerView.adapter = pingAdapter
                val layoutManager = LinearLayoutManager.VERTICAL
                pingRecyclerView.layoutManager = LinearLayoutManager(mContext!!, layoutManager, false)
                pingRecyclerView.itemAnimator = DefaultItemAnimator()
                pingRecyclerView.addItemDecoration(SpaceItemDecoration(2))
                pingHeaderView.attachTo(pingRecyclerView)

                et_comment_content.setOnFocusChangeListener { view, b ->
                    if (b) {
                        iv_comment.visibility = View.VISIBLE
                        iv_like.visibility = View.GONE
                        iv_transmit.visibility = View.GONE
                        iv_gift.visibility = View.GONE
                    } else {
                        iv_comment.visibility = View.GONE
                        iv_like.visibility = View.VISIBLE
                        iv_transmit.visibility = View.VISIBLE
                        iv_gift.visibility = View.VISIBLE
                    }
                }
            } else {
                //
            }
        } else {
            //
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasVideo) {
            v_player.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (hasVideo) {
            v_player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasVideo) {
            v_player.stopPlayback()
        }
    }

    private fun displayRuiShow() {
        getDetailInfo()
        /** 1图片2视频3直播  */
        if (show.type == 3) {
            initLiveView()
        } else if (show.type == 2) {
            initVideoView()
        } else if (show.type == 1) {
            initPicView()
        }

        //填充用户信息
        authorAvatar.setOnClickListener(photoOnClickListener)

        getAuthorInfo()
        //填充美秀信息
        if (!TextUtils.isEmpty(show.createTime)) {
            postTime.text = DateUtil.getTimeInterval(show.createTime!!)
        }
        /**用户发布内容 */
        showContent.text = show.title
        /**点赞用户 */
        zanNum.text = "${show.zanNum}"
        loadShowFiles()
        loadZanUserList()
        loadPingList()
    }

    private fun getAuthorInfo() {
        SendRequest.getAuthorInfo(mContext!!, show.id!!, mApplication.token, object :
                OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val mGson = GsonBuilder().serializeNulls().create()
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //成功
                    val userBase = mGson.fromJson(jsonBean.data, UserBase::class.java)
                    loadInfo(userBase)
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取用户信息失败。")
            }
        })
    }

    private fun loadInfo(userBase: UserBase) {
        /**用户信息 */
        authorMeiLi.text = String.format("美力值 + %d", userBase.ml)
        if (TextUtils.isEmpty(userBase.nickName)) {
            authorNickName.text = userBase.ymCode
        } else {
            authorNickName.text = userBase.nickName
        }
        if (TextUtils.isEmpty(userBase.avatar)) {
            Picasso.with(mContext).load(AppConstants.APP_DEFAULT_USER_PHOTO).resize(100, 100).centerCrop()
                    .into(authorAvatar)
        } else {
            Picasso.with(mContext).load(HttpUrl.getImageUrl(userBase.avatar)).resize(100, 100).centerCrop()
                    .into(authorAvatar)
        }
    }

    private fun initLiveView() {
        rl_video.visibility = View.VISIBLE
        ll_photo_video.visibility = View.GONE
    }

    private fun initPicView() {
        rl_video.visibility = View.GONE
        ll_photo_video.visibility = View.VISIBLE
    }

    private fun initVideoView() {
        rl_video.visibility = View.VISIBLE
        ll_photo_video.visibility = View.GONE
        ImageLoaderUtils.instance.loadBitmap(iv_video_cover, HttpUrl.getImageUrl(show.thumbnail))
        v_player.setCoverView(iv_video_cover)
        v_player.setBufferingIndicator(loadingView)
        loadingView.visibility = View.VISIBLE

        setOptions(0)

        val mMediaController = MediaController(this)
        v_player.setMediaController(mMediaController)
        v_player.displayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT
    }

    private fun loadShowFiles() {
        SendRequest.getShowFiles(mContext!!, show.id!!, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val files = mGson!!.fromJson<List<ShowFile>>(jsonBean.data, object :
                            TypeToken<List<ShowFile>>() {}.type)
                    val params = LinearLayout.LayoutParams(w, (w * 0.75).toInt())
                    if (show.type == 1) {
                        runOnUiThread {
                            ll_photo_video.removeAllViews()
                            val picFrame = BannerFrame(mContext!!)
                            picFrame.layoutParams = params
                            ll_photo_video.addView(picFrame)
                            val pics = files.map { it.file }
                            picFrame.setImageResources(pics)
                            picFrame.startPlay()
                        }
                    } else if (show.type == 2) {
                        if (files.isNotEmpty()) {
                            val first = files.first()
                            v_player.setVideoPath(HttpUrl.getVideoUrl(first.file))
                            hasVideo = true
                            v_player.start()
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    private fun loadZanUserList() {
        SendRequest.getShowZanList(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val zanUserList = mGson!!.fromJson<List<ShowZan>>(jsonBean.data,
                            object : TypeToken<List<ShowZan>>() {}.type)
                    zanNum.text = "${zanUserList.size}"
                    val limitNum = Math.min(6, zanUserList.size)
                    zanLayout.removeAllViews()
                    if (limitNum == 0) {
                        // 还没有点赞用户，跳过。
                        return
                    }
                    for (i in 0..limitNum - 1) {
                        val imageView = CircleImageView(mContext!!)
                        zanLayout.addView(imageView)
                        Picasso.with(mContext).load(HttpUrl.getImageUrl(zanUserList[i].avatar))
                                .resize(96, 96).centerCrop().into(imageView)
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取点赞用户失败")
                e.printStackTrace()
            }
        })
    }

    private fun loadPingList() {
        SendRequest.getShowPingList(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val pingList = mGson!!.fromJson<List<ShowPing>>(jsonBean.data,
                            object : TypeToken<List<ShowPing>>() {}.type)
                    pingAdapter.setData(pingList)
                    pingAdapter.notifyDataSetChanged()
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取评论失败")
                e.printStackTrace()
            }
        })
    }

    private fun getDetailInfo() {
        SendRequest.viewLive(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //TODO
                } else {
                    jsonBean?.let {
                        println(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                e.printStackTrace()
            }

        })
    }

    private fun setOptions(codecType: Int) {
        val options = AVOptions()

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024)
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming)
        if (mIsLiveStreaming === 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1)
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType)

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0)

        v_player.setAVOptions(options)
    }

    /**
     * 评论点击事件
     */
    private inner class BtnCommentOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            if (!mApplication.hasLogin()) {
                //未登录不能评论
                ToastUtil.show("你还没有登录，请登录后再发表评论。")
                return
            }
            iv_comment.isClickable = false
            val pingContent = et_comment_content.text.toString()
            hideKeyboard()//关闭键盘
            SendRequest.pingLive(mContext!!, mApplication.token, show.id!!, pingContent,
                    object : OkCallback<String>(OkStringParser()) {
                        override fun onSuccess(code: Int, response: String) {
                            val jsonBean = JSonResultBean.fromJSON(response)
                            if (jsonBean != null && jsonBean.isSuccess) {
                                loadPingList()
                                et_comment_content.text.clear()
                                ToastUtil.show("发表评论完成")
                                iv_comment.visibility = View.GONE
                                iv_like.visibility = View.VISIBLE
                                iv_transmit.visibility = View.VISIBLE
                                iv_gift.visibility = View.VISIBLE
                            } else {
                                jsonBean?.let {
                                    ToastUtil.show(jsonBean.message)
                                }
                            }
                            iv_comment.isClickable = true
                        }

                        override fun onFailure(e: Throwable) {
                            e.printStackTrace()
                            ToastUtil.show("评论失败")
                            iv_comment.isClickable = true
                        }
                    })
        }
    }

    private inner class BtnZanOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            if (!mApplication.hasLogin()) {
                ToastUtil.show("请登录后再点赞")
            } else {
                iv_like.isClickable = false
                SendRequest.zanLive(mContext!!, mApplication.token, show.id!!,
                        object : OkCallback<String>(OkStringParser()) {
                            override fun onSuccess(code: Int, response: String) {
                                val resultBean = JSonResultBean.fromJSON(response)
                                if (resultBean != null && resultBean.isSuccess) {
                                    zanNum.text = resultBean.data
                                    iv_like.isClickable = false
                                    loadZanUserList()
                                }
                            }

                            override fun onFailure(e: Throwable) {
                                ToastUtil.show("点赞失败")
                                iv_like.isClickable = true
                            }

                        })
            }
        }
    }

    /**
     * 转发点击事件
     */
    private inner class BtnTransOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("转发 :")
        }
    }

    /**
     * 头像点击事件
     */
    private inner class BtnPhotoOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("头像 :")
        }
    }

    /**
     * 微信分享

     * @param view
     */
    private fun shareWeixin(view: View) {
        wechatShare(0)
    }

    /**
     * 分享到朋友圈

     * @param view
     */
    private fun shareFriend(view: View) {
        wechatShare(1)
    }

    private fun wechatShare(flag: Int) {
        // int flag = 0;//0分享到微信好友,1分享到微信朋友圈
        val webpage = WXWebpageObject()
        webpage.webpageUrl = ""
        val msg = WXMediaMessage(webpage)
        msg.title = "！"
        msg.description = ""
        val thumb = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        msg.setThumbImage(thumb)

        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = if (flag == 0)
            SendMessageToWX.Req.WXSceneSession
        else
            SendMessageToWX.Req.WXSceneTimeline
        wxApi!!.sendReq(req)
    }

    // 微信分享
    private var wxApi: IWXAPI? = null

    companion object {

        fun start(context: Context, show: YueShow) {
            val starter = Intent(context, ShowDetailActivity::class.java)
            starter.putExtra("show", show)
            context.startActivity(starter)
        }
    }
}