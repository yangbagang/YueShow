package com.ybg.yxym.yueshow.adapter

import android.content.Context
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/1/26.
 */
class UserAvatarAdapter : RecyclerBaseAdapter<UserBase> {

    private var userAvatar: CircleImageView? = null

    constructor(context: Context) : super(context) {
    }

    override val rootResource: Int
        get() = R.layout.list_item_user_avatar

    override fun getView(viewHolder: RecyclerBaseAdapter<UserBase>.BaseViewHolder, item: UserBase?, position: Int) {
        userAvatar = viewHolder.getView(R.id.userAvatar)
        if (item != null && userAvatar != null) {
            val avatar = HttpUrl.getImageUrl(item.avatar)
            ImageLoaderUtils.instance.loadBitmap(userAvatar!!, avatar)

        }
        viewHolder.setIsRecyclable(false)
    }

}