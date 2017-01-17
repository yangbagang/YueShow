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
import com.tencent.mm.sdk.modelmsg.SendMessageToWX
import com.tencent.mm.sdk.modelmsg.WXMediaMessage
import com.tencent.mm.sdk.modelmsg.WXWebpageObject
import com.tencent.mm.sdk.openapi.IWXAPI
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.MetaData
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener
import com.ybg.yxym.yb.bean.*
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.PingItemAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.decoration.SpaceItemDecoration
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.OkHttpProxy
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.DownloadListener
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.utils.FileUtils
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ScreenUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.BannerFrame
import com.ybg.yxym.yueshow.view.CircleImageView
import kotlinx.android.synthetic.main.activity_home_show_detail.*
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.IOException

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
    private var videoAddress: String? = null
    private var mVideoPlayerManager: VideoPlayerManager<MetaData>? = null

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
            } else {
                //
            }
        } else {
            //
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
        mVideoPlayerManager = SingleVideoPlayerManager(PlayerItemChangeListener { })
        v_player.addMediaPlayerListener(object : SimpleMainThreadMediaPlayerListener() {
            override fun onVideoCompletionMainThread() {
                super.onVideoCompletionMainThread()
                iv_video_cover.visibility = View.VISIBLE
                iv_video_play.visibility = View.VISIBLE
                v_player.visibility = View.INVISIBLE
            }

            override fun onVideoStoppedMainThread() {
                super.onVideoStoppedMainThread()
                iv_video_cover.visibility = View.INVISIBLE
                iv_video_play.visibility = View.VISIBLE
                v_player.visibility = View.VISIBLE
            }

            override fun onVideoPreparedMainThread() {
                super.onVideoPreparedMainThread()
                iv_video_cover.visibility = View.INVISIBLE
                iv_video_play.visibility = View.INVISIBLE
                v_player.visibility = View.VISIBLE
            }
        })
        iv_video_play.setOnClickListener {
            playVideo()
        }
    }

    private fun loadShowFiles() {
        SendRequest.getShowFiles(mContext!!, show.id!!, object : OkCallback<String>
        (OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val files = mGson!!.fromJson<List<ShowFile>>(jsonBean.data, object :
                            TypeToken<List<ShowFile>>(){}.type)
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
                            loadVideo(HttpUrl.getVideoUrl(first.file))
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
        SendRequest.getShowZanList(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val zanUserList = mGson!!.fromJson<List<ShowZan>>(jsonBean.data,
                            object : TypeToken<List<ShowZan>>(){}.type)
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
        SendRequest.getShowPingList(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val pingList = mGson!!.fromJson<List<ShowPing>>(jsonBean.data,
                            object : TypeToken<List<ShowPing>>(){}.type)
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
        SendRequest.viewLive(mContext!!, show.id!!, object : OkCallback<String>(OkStringParser()){
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

    private fun loadVideo(videoUrl: String) {
        val videoName = FileUtils.getResourceName(videoUrl) ?: return

        val videoCache = AppConstants.VIDEO_CACHE_PATH
        val videoDir = File(videoCache)
        if (!videoDir.exists()) {
            videoDir.mkdirs()
        }

        val videoFile = File(videoCache + videoName)
        if (videoFile.exists()) {
            videoAddress = videoFile.absolutePath
            playVideo()
        }

        iv_video_cover.visibility = View.INVISIBLE
        iv_video_play.visibility = View.INVISIBLE
        tv_video_progress.visibility = View.VISIBLE
        OkHttpProxy.download(videoUrl, object : DownloadListener(videoCache, videoName){
            override fun onResponse(call: Call?, response: Response?) {
                response?.let {
                    super.onResponse(response)
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.let {
                    onFailure(e)
                }
            }

            override fun onSuccess(file: File) {
                videoAddress = file.absolutePath
                tv_video_progress.visibility = View.INVISIBLE
                playVideo()
            }

            override fun onFailure(e: Exception) {
                ToastUtil.show("视频缓存失败")
            }

            override fun onUIProgress(progress: Progress) {
                runOnUiThread {
                    val p = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                    tv_video_progress.text = String.format("正在缓冲%d", p)
                }
            }

        })
    }

    private fun playVideo() {
        if (mVideoPlayerManager != null) {
            if (videoAddress != null) {
                mVideoPlayerManager!!.playNewVideo(null, v_player, videoAddress)
            }
        }
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
                    object : OkCallback<String>(OkStringParser()){
                        override fun onSuccess(code: Int, response: String) {
                            val jsonBean = JSonResultBean.fromJSON(response)
                            if (jsonBean != null && jsonBean.isSuccess) {
                                loadPingList()
                                et_comment_content.text.clear()
                                ToastUtil.show("评论完成")
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