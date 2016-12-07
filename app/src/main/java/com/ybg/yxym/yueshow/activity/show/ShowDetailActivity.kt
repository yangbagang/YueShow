package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.ShowFile
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.utils.ScreenUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.BannerFrame
import com.ybg.yxym.yueshow.view.CircleImageView
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

    private var w = 0

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

        w = ScreenUtils.getScreenWidth(mContext!!)
//        val toolBar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
//        toolBar.title = "查看悦秀"
//        setSupportActionBar(toolBar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolBar.setNavigationOnClickListener {
//            onBackPressed()
//        }
        setCustomTitle("查看悦秀")
    }

    override fun init() {
        if (intent != null) {
            val showItem = intent.getSerializableExtra("show")
            if (showItem is YueShow) {
                show = showItem
                //开始展示美秀
                displayRuiShow()
            } else {
                //
            }
        } else {
            //
        }
    }

    private fun displayRuiShow() {
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
    }

    private fun loadShowFiles() {
        SendRequest.getShowFiles(mContext!!, show.id!!, object : OkCallback<String>
        (OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val files = mGson!!.fromJson<List<ShowFile>>(jsonBean.data, object :
                            TypeToken<List<ShowFile>>(){}.type)
                    runOnUiThread {
                        val params = LinearLayout.LayoutParams(w, (w * 0.75).toInt())
                        if (show.type == 1) {
                            ll_photo_video.removeAllViews()
                            val picFrame = BannerFrame(mContext!!)
                            picFrame.layoutParams = params
                            ll_photo_video.addView(picFrame)
                            val pics = files.map { it.file }
                            picFrame.setImageResources(pics)
                            picFrame.startPlay()
                            println("ll_photo_video.width=${ll_photo_video.width}")
                            println("picFrame.width=${picFrame.width}")
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

    }

    private fun loadPingList() {

    }

    /**
     * 评论点击事件
     */
    private inner class BtnCommentOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("评论 :")
        }
    }

    private inner class BtnZanOnClickListener() : View.OnClickListener {

        override fun onClick(v: View) {
            if (!mApplication.hasLogin()) {
                ToastUtil.show("请登录后再点赞")
            } else {
                SendRequest.zanLive(mContext!!, mApplication.token, show.id!!,
                        object : OkCallback<String>(OkStringParser()) {
                            override fun onSuccess(code: Int, response: String) {
                                val resultBean = JSonResultBean.fromJSON(response)
                                if (resultBean != null && resultBean.isSuccess) {
                                    zanNum.text = "${show.zanNum + 1}"
                                    iv_like.isClickable = false
                                }
                            }

                            override fun onFailure(e: Throwable) {
                                ToastUtil.show("点赞失败")
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

    companion object {

        fun start(context: Context, show: YueShow) {
            val starter = Intent(context, ShowDetailActivity::class.java)
            starter.putExtra("show", show)
            context.startActivity(starter)
        }
    }
}