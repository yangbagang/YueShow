package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.live.StartLiveActivity
import com.ybg.yxym.yueshow.activity.photo.CameraActivity
import com.ybg.yxym.yueshow.activity.photo.PhotoProcessActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.utils.AndroidPermissonRequest
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.gallery.MultiImageSelectorActivity
import kotlinx.android.synthetic.main.activity_entry.*
import java.util.*

/**
 * 类描述：拍摄入口
 */
class EntryActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_entry
    }

    override fun setUpView() {
        AndroidPermissonRequest.verifyCameraPermissions(mContext!!)
        setCustomTitle("选择类型")
    }

    override fun init() {
        rl_entry_updata_photo.setOnClickListener(onClickListener)
        rl_entry_updata_video.setOnClickListener(onClickListener)
        rl_entry_photograph.setOnClickListener(onClickListener)
        rl_entry_video_live.setOnClickListener(onClickListener)
        rl_entry_video_play.setOnClickListener(onClickListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentExtra.RequestCode.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data == null) return
            val list = data.getStringArrayListExtra(MultiImageSelectorActivity
                    .EXTRA_RESULT)
            if (list.isEmpty()) {
                ToastUtil.show("你最少需要选择一张图片")
                return
            }
            PhotoProcessActivity.start(mContext!!, list)
            finish()
        }
    }

    /**
     * 点击事件
     */
    private var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        if (!mApplication.hasLogin()) {
            ToastUtil.show("你还没有登录，请先登录。")
        } else {
            when (v.id) {
                R.id.rl_entry_updata_photo//上传图片
                -> {
                    //选择己有的图片上传
                    MultiImageSelectorActivity.start(mContext!!, true, 9, MultiImageSelectorActivity
                            .MODE_MULTI)
                }
                R.id.rl_entry_updata_video//上传视频
                -> ToastUtil.show("上传视频!")
                R.id.rl_entry_photograph//拍照
                -> {
                    CameraActivity.start(mContext!!)
                    finish()
                }
                R.id.rl_entry_video_live//直播
                -> {
                    StartLiveActivity.start(mContext!!)
                }
                R.id.rl_entry_video_play//视频
                -> showVideoRecord()
            }
        }//CameraActivity.start(mContext);
        //                    SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        //                    String user_id = pref.getString("user_id", "");
        //                    String user_name = pref.getString("userPhone", "");
        //                    RongIMConnect.connect(EntryActivity.this, user_id, user_name);
        //StartLiveActivity.start(mContext);
        //finish();
    }

    private fun showVideoRecord() {
        //        QupaiService qupaiService = AlibabaSDK.getService(QupaiService.class);
        //        if (qupaiService == null) {
        //            ToastUtils.show("插件没有初始化,请稍后重试!");
        //            return;
        //        }
        //        //引导，只显示一次，这里用SharedPreferences记录
        //        final AppGlobalSetting sp = new AppGlobalSetting(getApplicationContext());
        //        Boolean isGuideShow = sp.getBooleanGlobalItem(AppConfig.PREF_VIDEO_EXIST_USER, true);
        //        qupaiService.showRecordPage(mContext, IntentExtra.RequestCode.RECORDE_SHOW, isGuideShow, new FailureCallback() {
        //            @Override
        //            public void onFailure(int i, String s) {
        //                ToastUtils.show("onFailure:" + s + "CODE" + i);
        //            }
        //        });
        //        sp.saveGlobalConfigItem(AppConfig.PREF_VIDEO_EXIST_USER, false);
    }

    companion object {


        fun start(context: Context) {
            val starter = Intent(context, EntryActivity::class.java)
            context.startActivity(starter)
        }
    }
}
