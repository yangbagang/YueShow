package com.ybg.yxym.yueshow.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.volokh.danylo.video_player_manager.ui.VideoPlayerView
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView

class FriendShowAdapter(protected var mContext: Activity) : BaseAdapter() {
    private var width = 0
    /**
     * 屏幕的宽度
     */
    private var inflater: LayoutInflater? = null
    protected var TAG = "HotShowAdapter"
    var mList: List<YueShow>? = null
    private var user_id: String? = null//用户id
    private var token: String? = null//令牌

    init {
        getSharePref()
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.width = wm.defaultDisplay.width
    }

    /**
     * @param list
     */
    fun setDataList(list: List<YueShow>) {
        this.mList = list
    }

    override fun getCount(): Int {
        return if (mList != null && mList!!.size > 0) mList!!.size else 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return mList!![position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolder: ViewHolder? = null
        var careOnClickListener: BtnCareOnClickListener? = null
        var commentOnClickListener: BtnCommentOnClickListener? = null
        var pariseOnClickListener: BtnPariseOnClickListener? = null
        var transOnClickListener: BtnTransOnClickListener? = null
        var photoOnClickListener: BtnPhotoOnClickListener? = null
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.item_home_hot_show, parent, false)
            viewHolder = ViewHolder()
            //视频 直播 图片
            viewHolder.rl_video = convertView!!.findViewById(R.id.rl_video) as RelativeLayout
            viewHolder.iv_video_cover = convertView.findViewById(R.id.iv_video_cover) as ImageView
            viewHolder.iv_video_paly = convertView.findViewById(R.id.iv_video_play) as ImageView
            viewHolder.videoPlayerView = convertView.findViewById(R.id.v_player) as VideoPlayerView
            viewHolder.rl_live = convertView.findViewById(R.id.rl_live) as RelativeLayout
            viewHolder.iv_live_cover = convertView.findViewById(R.id.iv_live_cover) as ImageView
            viewHolder.iv_live_play = convertView.findViewById(R.id.iv_video_play) as ImageView
            //viewHolder.ll_picture = convertView.findViewById(R.id.ll_picture) as LinearLayout
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
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        careOnClickListener = BtnCareOnClickListener(position)
        commentOnClickListener = BtnCommentOnClickListener(position)
        pariseOnClickListener = BtnPariseOnClickListener(position)
        transOnClickListener = BtnTransOnClickListener(position)
        photoOnClickListener = BtnPhotoOnClickListener(position)
        /** 1直播，2是图片，3是视频  */
        if (mList!![position].type == 3) {
            viewHolder.rl_live!!.visibility = View.GONE
            viewHolder.rl_video!!.visibility = View.VISIBLE
            viewHolder.ll_picture!!.visibility = View.GONE
            viewHolder.ll_photo_live_flag!!.visibility = View.GONE
            /**冠军 */
            viewHolder.iv_video_cover!!.layoutParams = relativeLayoutParms
            viewHolder.iv_video_cover!!.setImageResource(R.mipmap.ic_default_cover)
            if (mList!![position].thumbnail != null) {
                if (!TextUtils.isEmpty(mList!![position].thumbnail)) {
                    /**设置 tag 防止图片错位 */
                    viewHolder.iv_video_cover!!.tag = mList!![position].thumbnail
                    if (viewHolder.iv_video_cover!!.tag != null && viewHolder.iv_video_cover!!.tag == mList!![position].thumbnail) {
                        Picasso.with(mContext).load(mList!![position].thumbnail).resize(800, 800).centerCrop().into(viewHolder.iv_video_cover)
                    }
                }
            }
        } else if (mList!![position].type == 2) {
            viewHolder.rl_live!!.visibility = View.GONE
            viewHolder.rl_video!!.visibility = View.GONE
            viewHolder.ll_picture!!.visibility = View.VISIBLE
            viewHolder.ll_photo_live_flag!!.visibility = View.VISIBLE
            /**冠军 */
            viewHolder.ll_photo_live_flag!!.setBackgroundResource(R.drawable.shape_gray_bg)
            viewHolder.iv_photo_live_flag!!.setImageResource(R.mipmap.ic_entry_photo_logo)
            if (mList!![position].thumbnail != null) {
                val imgNum = mList!![position].fileNum
                viewHolder.rl_live!!.visibility = View.GONE
                viewHolder.rl_video!!.visibility = View.GONE
                viewHolder.ll_picture!!.visibility = View.VISIBLE
                if (imgNum == 1) {
                    viewHolder.tv_photo_live_flag!!.text = "1"
                    viewHolder.ll_picture!!.removeAllViews()
                    val imageView = ImageView(mContext)
                    imageView.layoutParams = getLinearLayoutParms(0, 0, 0, 0, 1, 1)
                    viewHolder.ll_picture!!.addView(imageView)
                    /**设置 tag 防止图片错位 */
                    val img_url_0 = mList!![position].thumbnail
                    imageView.setImageResource(R.mipmap.ic_default_cover)
                    imageView.tag = img_url_0
                    if (imageView.tag != null && imageView.tag == img_url_0) {
                        Picasso.with(mContext).load(img_url_0).resize(600, 600).centerCrop().into(imageView)
                    }
                } else if (imgNum == 2) {
                    viewHolder.tv_photo_live_flag!!.text = "2"
                    viewHolder.ll_picture!!.removeAllViews()
                    val imageView_1 = ImageView(mContext)
                    imageView_1.layoutParams = getLinearLayoutParms(0, 0, 0, 3, 1, 2)
                    viewHolder.ll_picture!!.addView(imageView_1)
                    /**设置 tag 防止图片错位 */
                    val img_url_1 = mList!![position].thumbnail
                    imageView_1.setImageResource(R.mipmap.ic_default_cover)
                    imageView_1.tag = img_url_1
                    if (imageView_1.tag != null && imageView_1.tag == img_url_1) {
                        Picasso.with(mContext).load(img_url_1).resize(300, 300).centerCrop().into(imageView_1)
                    }
                    val imageView_2 = ImageView(mContext)
                    imageView_2.layoutParams = getLinearLayoutParms(0, 0, 3, 0, 1, 2)
                    viewHolder.ll_picture!!.addView(imageView_2)
                    /**设置 tag 防止图片错位 */
                    val img_url_2 = mList!![position].thumbnail
                    imageView_2.setImageResource(R.mipmap.ic_default_cover)
                    imageView_2.tag = img_url_2
                    if (imageView_2.tag != null && imageView_2.tag == img_url_2) {
                        Picasso.with(mContext).load(img_url_2).resize(300, 300).centerCrop().into(imageView_2)
                    }
                } else {
                    viewHolder.tv_photo_live_flag!!.text = "3"
                    viewHolder.ll_picture!!.removeAllViews()
                    val imageView_1 = ImageView(mContext)
                    imageView_1.layoutParams = getLinearLayoutParms(0, 0, 0, 3, 2, 3)
                    viewHolder.ll_picture!!.addView(imageView_1)
                    /**设置 tag 防止图片错位 */
                    val img_url_1 = mList!![position].thumbnail
                    imageView_1.setImageResource(R.mipmap.ic_default_cover)
                    imageView_1.tag = img_url_1
                    if (imageView_1.tag != null && imageView_1.tag == img_url_1) {
                        Picasso.with(mContext).load(img_url_1).resize(400, 400).centerCrop().into(imageView_1)
                    }
                    val ll_v = LinearLayout(mContext)
                    ll_v.orientation = LinearLayout.VERTICAL
                    val imageView_2 = ImageView(mContext)
                    imageView_2.layoutParams = getLinearLayoutParms(0, 3, 3, 0, 1, 3)
                    ll_v.addView(imageView_2)
                    /**设置 tag 防止图片错位 */
                    val img_url_2 = mList!![position].thumbnail
                    imageView_2.setImageResource(R.mipmap.ic_default_cover)
                    imageView_2.tag = img_url_2
                    if (imageView_2.tag != null && imageView_2.tag == img_url_2) {
                        Picasso.with(mContext).load(img_url_2).resize(200, 200).centerCrop().into(imageView_2)
                    }
                    val imageView_3 = ImageView(mContext)
                    imageView_3.layoutParams = getLinearLayoutParms(3, 0, 3, 0, 1, 3)
                    ll_v.addView(imageView_3)
                    /**设置 tag 防止图片错位 */
                    val img_url_3 = mList!![position].thumbnail
                    imageView_3.setImageResource(R.mipmap.ic_default_cover)
                    imageView_3.tag = img_url_3
                    if (imageView_3.tag != null && imageView_3.tag == img_url_3) {
                        Picasso.with(mContext).load(img_url_3).resize(200, 200).centerCrop().into(imageView_3)
                    }
                    viewHolder.ll_picture!!.addView(ll_v)
                }
            }
        } else if (mList!![position].type == 1) {
            viewHolder.rl_live!!.visibility = View.VISIBLE
            viewHolder.rl_video!!.visibility = View.GONE
            viewHolder.ll_picture!!.visibility = View.GONE
            viewHolder.ll_photo_live_flag!!.visibility = View.VISIBLE
            /**冠军 */
            viewHolder.ll_photo_live_flag!!.setBackgroundResource(R.drawable.shape_red_bg)
            viewHolder.iv_photo_live_flag!!.setImageResource(R.mipmap.ic_entry_live_logo)
            viewHolder.tv_photo_live_flag!!.text = "直播"
            viewHolder.iv_live_cover!!.layoutParams = relativeLayoutParms
            //设置默认图片
            viewHolder.iv_live_cover!!.setImageResource(R.mipmap.ic_default_cover)
            if (mList!![position].thumbnail != null) {
                if (!TextUtils.isEmpty(mList!![position].thumbnail)) {
                    /**设置 tag 防止图片错位 */
                    val img_url_live = mList!![position].thumbnail
                    viewHolder.iv_live_cover!!.tag = img_url_live
                    if (viewHolder.iv_live_cover!!.tag != null && viewHolder.iv_live_cover!!.tag == img_url_live) {
                        Picasso.with(mContext).load(img_url_live).resize(600, 600).centerCrop().into(viewHolder.iv_live_cover)
                    }
                }
            }
        }
        /**用户信息 */
        viewHolder.iv_user_photo!!.setOnClickListener(photoOnClickListener)
        if (TextUtils.isEmpty(mList!![position].user!!.nickName)) {
            viewHolder.tv_username!!.text = "" + mList!![position].user!!.ymCode
        } else {
            viewHolder.tv_username!!.text = mList!![position].user!!.nickName
        }
        if (TextUtils.isEmpty(mList!![position].user!!.avatar)) {
            Picasso.with(mContext).load(AppConstants.APP_DEFAULT_USER_PHOTO).resize(100, 100).centerCrop().into(viewHolder.iv_user_photo)
        } else {
            Picasso.with(mContext).load(mList!![position].user!!.avatar).resize(100, 100).centerCrop().into(viewHolder.iv_user_photo)
        }

        viewHolder.btn_care!!.setOnClickListener(careOnClickListener)

        if (mList!![position].user!!.flag == 1) {
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
            /**已经关注之后不能点击 */
        }
        if (!TextUtils.isEmpty(mList!![position].createTime)) {
            viewHolder.tv_time!!.text = DateUtil.getTimeInterval(mList!![position].createTime!!)
        }
        /**用户发布内容 */
        val str = mList!![position].title
        viewHolder.tv_content!!.text = str
        /**用户操作 */
        viewHolder.iv_comment!!.setOnClickListener(commentOnClickListener)
        viewHolder.iv_parise!!.setOnClickListener(pariseOnClickListener)
        viewHolder.iv_transmit!!.setOnClickListener(transOnClickListener)
        viewHolder.tv_comment!!.setText(mList!![position].pingNum)
        viewHolder.tv_parise!!.setText(mList!![position].zanNum)
        viewHolder.tv_transmit!!.setText(mList!![position].shareNum)
        return convertView
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
        internal var ll_picture: LinearLayout? = null//图片布局
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
    private inner class BtnCareOnClickListener(internal var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            ToastUtil.show("关注 :" + mPosition)
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

    private inner class BtnPariseOnClickListener(internal var mPosition: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            if (TextUtils.isEmpty(user_id)) {
                ToastUtil.show("请登录后再点赞")
            } else {
                SendRequest.like(mContext, user_id!!, token!!, mList!![mPosition].id, "1", object : OkCallback<String>(OkStringParser()) {
                    override fun onSuccess(code: Int, response: String) {
                        LogUtil.d("点赞返回信息" + response)
                    }

                    override fun onFailure(e: Throwable) {
                        LogUtil.e("点赞失败返回信息" + e.toString())
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
     * *
     * @param bottomMargin 底部距离
     * *
     * @param leftMargin   左边距离
     * *
     * @param rightMargin  右边距离
     * *
     * @param multiple     倍数
     * *
     * @param divide       除数
     * *
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

    private fun getSharePref() {
        val preferences = mContext.getSharedPreferences(AppConstants.SHARE_PREFERENCE_USER, Context.MODE_PRIVATE)
        user_id = preferences.getString(AppConstants.USER_ID, "")
        token = preferences.getString(AppConstants.USER_REFRESH_TOKEN, "")
    }

}