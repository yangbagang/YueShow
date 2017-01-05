package com.ybg.yxym.yueshow.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.google.gson.GsonBuilder
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.show.ShowDetailActivity
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2016/11/25.
 */
class HomeShowAdapter(private var mContext: Activity) : BaseAdapter() {

    private var width = 0//屏幕的宽度
    private var inflater: LayoutInflater? = null
    private var TAG = "HomeShowAdapter"
    var mList: List<YueShow>? = null

    init {
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.width = wm.defaultDisplay.width
    }

    fun setDataList(list: List<YueShow>) {
        this.mList = list
    }

    override fun getCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) mList!!.size else 0
    }

    override fun getItemId(position: Int): Long {
        return mList!![position].id!!
    }

    override fun getItem(position: Int): Any {
        return mList!![position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        //初始化定义
        var convertView = view
        var viewHolder: ViewHolder? = null
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.item_home_show, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        /** 1图片2视频3直播  */
        if (mList!![position].type == 3) {
            initLiveView(viewHolder, position)
        } else if (mList!![position].type == 2) {
            initVideoView(viewHolder, position)
        } else if (mList!![position].type == 1) {
            initPicView(viewHolder, position)
        }

        //事件定义
        val commentOnClickListener = BtnCommentOnClickListener(position)
        val zanOnClickListener = BtnZanOnClickListener(viewHolder, position)
        val transOnClickListener = BtnTransOnClickListener(position)
        val photoOnClickListener = BtnPhotoOnClickListener(position)

        //填充用户信息
        viewHolder.iv_user_photo!!.setOnClickListener(photoOnClickListener)

        getAuthorInfo(mList!![position].id!!, viewHolder)
        //填充美秀信息
        if (!TextUtils.isEmpty(mList!![position].createTime)) {
            viewHolder.tv_time!!.text = DateUtil.getTimeInterval(mList!![position].createTime!!)
        }
        /**用户发布内容 */
        val str = mList!![position].title
        viewHolder.tv_content!!.text = str
        /**用户操作 */
        viewHolder.iv_comment!!.setOnClickListener(commentOnClickListener)
        viewHolder.iv_parise!!.setOnClickListener(zanOnClickListener)
        viewHolder.iv_transmit!!.setOnClickListener(transOnClickListener)
        viewHolder.tv_comment!!.text = "${mList!![position].pingNum}"
        viewHolder.tv_parise!!.text = "${mList!![position].zanNum}"
        viewHolder.tv_transmit!!.text = "${mList!![position].shareNum}"
        return convertView!!
    }

    private fun getAuthorInfo(showId: Long, viewHolder: ViewHolder) {
        SendRequest.getAuthorInfo(mContext, showId, ShowApplication.instance!!.token, object :
                OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val mGson = GsonBuilder().serializeNulls().create()
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //成功
                    val userBase = mGson.fromJson(jsonBean.data, UserBase::class.java)
                    loadInfo(viewHolder, userBase)
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

    private fun loadInfo(viewHolder: ViewHolder, userBase: UserBase) {
        /**用户信息 */
        viewHolder.tv_meilizhi!!.text = String.format("美力值 + %d", userBase.ml)
        if (TextUtils.isEmpty(userBase.nickName)) {
            viewHolder.tv_username!!.text = userBase.ymCode
        } else {
            viewHolder.tv_username!!.text = userBase.nickName
        }
        if (TextUtils.isEmpty(userBase.avatar)) {
            Picasso.with(mContext).load(AppConstants.APP_DEFAULT_USER_PHOTO).resize(100, 100).centerCrop()
                    .into(viewHolder.iv_user_photo)
        } else {
            Picasso.with(mContext).load(HttpUrl.getImageUrl(userBase.avatar)).resize(100, 100).centerCrop()
                    .into(viewHolder.iv_user_photo)
        }

        if (userBase.flag == 1) {
            viewHolder.btn_care!!.setBackgroundResource(R.drawable.shape_bg_green_edge)
            val img_focus = mContext.resources.getDrawable(R.mipmap.ic_has_focus)
            // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            img_focus.setBounds(0, 0, img_focus.minimumWidth, img_focus.minimumHeight)
            viewHolder.btn_care!!.setCompoundDrawables(img_focus, null, null, null) //设置左图标
            viewHolder.btn_care!!.text = "已关注"
            viewHolder.btn_care!!.setTextColor(0Xff7dcf2c.toInt())
            viewHolder.btn_care!!.isEnabled = false
            /**已经关注之后不能点击 */
        } else {
            viewHolder.btn_care!!.setBackgroundResource(R.drawable.shape_btn_login)
            val img_add = mContext.resources.getDrawable(R.mipmap.ic_add_focus)
            img_add.setBounds(0, 0, img_add.minimumWidth, img_add.minimumHeight)
            viewHolder.btn_care!!.setCompoundDrawables(img_add, null, null, null) //设置左图标
            viewHolder.btn_care!!.text = "关注"
            viewHolder.btn_care!!.setTextColor(0xffffffff.toInt())
            viewHolder.btn_care!!.isEnabled = true
            /**未关注可能点击 */
            val careOnClickListener = BtnCareOnClickListener(viewHolder, userBase.id)
            viewHolder.btn_care!!.setOnClickListener(careOnClickListener)
        }
    }

    private fun initViewHolder(viewHolder: ViewHolder, convertView: View) {
        //视频 直播 图片
        viewHolder.rl_video = convertView.findViewById(R.id.rl_video) as RelativeLayout
        viewHolder.iv_video_cover = convertView.findViewById(R.id.iv_video_cover) as ImageView
        viewHolder.iv_video_paly = convertView.findViewById(R.id.iv_video_play) as ImageView
        viewHolder.videoPlayerView = convertView.findViewById(R.id.v_player) as VideoPlayerView
        viewHolder.rl_live = convertView.findViewById(R.id.rl_live) as RelativeLayout
        viewHolder.iv_live_cover = convertView.findViewById(R.id.iv_live_cover) as ImageView
        viewHolder.iv_live_play = convertView.findViewById(R.id.iv_video_play) as ImageView
        viewHolder.iv_picture = convertView.findViewById(R.id.iv_picture) as ImageView
        viewHolder.tv_photo_live_flag = convertView.findViewById(R.id.tv_photo_live_flag) as TextView
        viewHolder.iv_photo_live_flag = convertView.findViewById(R.id.iv_photo_live_flag) as ImageView
        viewHolder.ll_photo_live_flag = convertView.findViewById(R.id.ll_photo_live_flag) as LinearLayout
        //用户信息
        viewHolder.iv_user_photo = convertView.findViewById(R.id.iv_user_logo) as CircleImageView
        viewHolder.tv_username = convertView.findViewById(R.id.tv_user_name) as TextView
        viewHolder.iv_level_img = convertView.findViewById(R.id.iv_level_img) as ImageView
        viewHolder.tv_meilizhi = convertView.findViewById(R.id.tv_meilizhi) as TextView
        viewHolder.tv_time = convertView.findViewById(R.id.tv_time) as TextView
        viewHolder.btn_care = convertView.findViewById(R.id.btn_care) as Button
        //用户发布文字
        viewHolder.tv_content = convertView.findViewById(R.id.tv_show_user_feel) as TextView
        viewHolder.tv_detail = convertView.findViewById(R.id.tv_show_more) as TextView
        //用户操作
        viewHolder.tv_comment = convertView.findViewById(R.id.tv_show_comment) as TextView
        viewHolder.tv_parise = convertView.findViewById(R.id.tv_show_praise) as TextView
        viewHolder.tv_transmit = convertView.findViewById(R.id.tv_show_transmit) as TextView
        viewHolder.iv_comment = convertView.findViewById(R.id.iv_show_comment) as ImageView
        viewHolder.iv_parise = convertView.findViewById(R.id.iv_show_praise) as ImageView
        viewHolder.iv_transmit = convertView.findViewById(R.id.iv_show_transmit) as ImageView
        convertView.tag = viewHolder
    }

    private fun initLiveView(viewHolder: ViewHolder, position: Int) {
        viewHolder.rl_live!!.visibility = View.VISIBLE
        viewHolder.rl_video!!.visibility = View.GONE
        viewHolder.iv_picture!!.visibility = View.GONE
        viewHolder.ll_photo_live_flag!!.visibility = View.VISIBLE
        /**冠军 */
        viewHolder.ll_photo_live_flag!!.setBackgroundResource(R.drawable.shape_red_bg)
        viewHolder.iv_photo_live_flag!!.setImageResource(R.mipmap.ic_entry_live_logo)
        viewHolder.tv_photo_live_flag!!.text = "直播"
        viewHolder.iv_live_cover!!.layoutParams = relativeLayoutParms
        //设置默认图片
        viewHolder.iv_live_cover!!.setImageResource(R.mipmap.ic_default_cover)
        if (mList!![position].thumbnail != "") {
            /**设置 tag 防止图片错位 */
            val img_url_live = HttpUrl.getImageUrl(mList!![position].thumbnail)
            viewHolder.iv_live_cover!!.tag = img_url_live
            if (viewHolder.iv_live_cover!!.tag != null && viewHolder.iv_live_cover!!.tag == img_url_live) {
                Picasso.with(mContext).load(img_url_live).resize(width, (width * 0.75).toInt()).centerCrop()
                        .into(viewHolder.iv_live_cover)
            }
        }
    }

    private fun initPicView(viewHolder: ViewHolder, position: Int) {
        viewHolder.rl_live!!.visibility = View.GONE
        viewHolder.rl_video!!.visibility = View.GONE
        viewHolder.iv_picture!!.visibility = View.VISIBLE
        viewHolder.ll_photo_live_flag!!.visibility = View.VISIBLE
        /**冠军 */
        viewHolder.ll_photo_live_flag!!.setBackgroundResource(R.drawable.shape_gray_bg)
        viewHolder.iv_photo_live_flag!!.setImageResource(R.mipmap.ic_entry_photo_logo)
        if (mList!![position].thumbnail != "") {
            val imgNum = mList!![position].fileNum
            viewHolder.rl_live!!.visibility = View.GONE
            viewHolder.rl_video!!.visibility = View.GONE
            viewHolder.iv_picture!!.visibility = View.VISIBLE
            viewHolder.tv_photo_live_flag!!.text = "$imgNum"
            /**设置 tag 防止图片错位 */
            val img_url_0 = HttpUrl.getImageUrl(mList!![position].thumbnail)
            viewHolder.iv_picture!!.tag = img_url_0
            if (viewHolder.iv_picture!!.tag != null && viewHolder.iv_picture!!.tag == img_url_0) {
                Picasso.with(mContext).load(img_url_0).resize(width, (width * 0.75).toInt()).centerCrop()
                        .into(viewHolder.iv_picture!!)
                viewHolder.iv_picture!!.setOnClickListener {
                    ShowDetailActivity.start(mContext, mList!![position])
                }
            }
        }
    }

    private fun initVideoView(viewHolder: ViewHolder, position: Int) {
        viewHolder.rl_live!!.visibility = View.GONE
        viewHolder.rl_video!!.visibility = View.VISIBLE
        viewHolder.iv_picture!!.visibility = View.GONE
        viewHolder.ll_photo_live_flag!!.visibility = View.GONE
        /**冠军 */
        viewHolder.iv_video_cover!!.layoutParams = relativeLayoutParms
        viewHolder.iv_video_cover!!.setImageResource(R.mipmap.ic_default_cover)
        if (mList!![position].thumbnail != "") {
            /**设置 tag 防止图片错位 */
            val img_url_0 = HttpUrl.getImageUrl(mList!![position].thumbnail)
            viewHolder.iv_video_cover!!.tag = img_url_0
            if (viewHolder.iv_video_cover!!.tag != null && viewHolder.iv_video_cover!!.tag == img_url_0) {
                Picasso.with(mContext).load(img_url_0).resize(width, (width * 0.75).toInt()).centerCrop()
                        .into(viewHolder.iv_video_cover)
                viewHolder.iv_video_cover!!.setOnClickListener {
                    ShowDetailActivity.start(mContext, mList!![position])
                }
            }
        }
    }

    /**
     * Viewholder
     */
    inner class ViewHolder {
        internal var rl_video: RelativeLayout? = null//视频播放布局
        internal var iv_video_cover: ImageView? = null//视频封面
        internal var iv_video_paly: ImageView? = null//视频播放按键
        internal var videoPlayerView: VideoPlayerView? = null//视频播放
        internal var rl_live: RelativeLayout? = null//直播布局
        internal var iv_live_cover: ImageView? = null//直播封面
        internal var iv_live_play: ImageView? = null//播放按键
        internal var iv_picture: ImageView? = null//图片布局
        internal var tv_photo_live_flag: TextView? = null//图片或者直播标志
        internal var iv_photo_live_flag: ImageView? = null//图片或者直播标志
        internal var ll_photo_live_flag: LinearLayout? = null//图片或者直播标志
        internal var iv_user_photo: CircleImageView? = null//用户头像
        internal var tv_username: TextView? = null//用户昵称
        internal var iv_level_img: ImageView? = null//用户等级图片
        internal var tv_meilizhi: TextView? = null//用户美力值
        internal var tv_time: TextView? = null//用户发布时间
        internal var btn_care: Button? = null//关注
        internal var tv_content: TextView? = null//发布文字内容
        internal var tv_detail: TextView? = null//显示更多
        internal var tv_comment: TextView? = null//评论数
        internal var tv_parise: TextView? = null//点赞数
        internal var tv_transmit: TextView? = null//转发数
        internal var iv_comment: ImageView? = null//评论图标
        internal var iv_parise: ImageView? = null//点赞图标
        internal var iv_transmit: ImageView? = null//转发图标
    }

    /**
     * 关注点击事件
     */
    private inner class BtnCareOnClickListener(var viewHolder: ViewHolder, var userId: Long) : View.OnClickListener {

        override fun onClick(v: View) {
            if (!ShowApplication.instance!!.hasLogin()) {
                ToastUtil.show("请登录后再关注")
            } else {
                SendRequest.followUser(mContext, ShowApplication.instance!!.token, userId,
                        object : OkCallback<String>(OkStringParser()) {
                            override fun onSuccess(code: Int, response: String) {
                                val resultBean = JSonResultBean.fromJSON(response)
                                if (resultBean != null && resultBean.isSuccess) {
                                    viewHolder.btn_care!!.setBackgroundResource(R.drawable.shape_bg_green_edge)
                                    val img_focus = mContext.resources.getDrawable(R.mipmap.ic_has_focus)
                                    // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                    img_focus.setBounds(0, 0, img_focus.minimumWidth, img_focus.minimumHeight)
                                    viewHolder.btn_care!!.setCompoundDrawables(img_focus, null, null, null) //设置左图标
                                    viewHolder.btn_care!!.text = "已关注"
                                    viewHolder.btn_care!!.setTextColor(0Xff7dcf2c.toInt())
                                    viewHolder.btn_care!!.isEnabled = false
                                }
                            }

                            override fun onFailure(e: Throwable) {
                                ToastUtil.show("关注失败")
                            }
                        })
            }
        }
    }


    /**
     * 评论点击事件
     */
    private inner class BtnCommentOnClickListener(internal var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("评论 :" + mPosition)
        }
    }

    private inner class BtnZanOnClickListener(var viewHolder: ViewHolder, var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            if (!ShowApplication.instance!!.hasLogin()) {
                ToastUtil.show("请登录后再点赞")
            } else {
                SendRequest.zanLive(mContext, ShowApplication.instance!!.token, getItemId(mPosition),
                        object : OkCallback<String>(OkStringParser()) {
                            override fun onSuccess(code: Int, response: String) {
                                val resultBean = JSonResultBean.fromJSON(response)
                                if (resultBean != null && resultBean.isSuccess) {
                                    viewHolder.tv_parise!!.text = "${mList!![mPosition].zanNum + 1}"
                                    viewHolder.iv_parise!!.isClickable = false
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
    private inner class BtnTransOnClickListener(internal var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("转发 :" + mPosition)
        }
    }

    /**
     * 头像点击事件
     */
    private inner class BtnPhotoOnClickListener(internal var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("头像 :" + mPosition)
        }
    }

    /**
     * @param topMargin    顶部距离
     * @param bottomMargin 底部距离
     * @param leftMargin   左边距离
     * @param rightMargin  右边距离
     * @param multiple     倍数
     * @param divide       除数
     *
     * @return
     */
    private fun getLinearLayoutParms(topMargin: Int, bottomMargin: Int, leftMargin: Int, rightMargin: Int,
                                     multiple: Int, divide: Int): LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.width = width * multiple / divide - (rightMargin + leftMargin)
        params.height = width * multiple / divide - (bottomMargin + topMargin)
        params.bottomMargin = bottomMargin
        params.topMargin = topMargin
        params.rightMargin = rightMargin
        params.leftMargin = leftMargin
        return params
    }

    /**
     * @return 设置图片长宽 = 屏幕宽度
     */
    private val relativeLayoutParms: RelativeLayout.LayoutParams
        get() {
            val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.width = width
            params.height = width
            return params
        }
}