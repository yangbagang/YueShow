package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.AndroidPermissonRequest
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.gallery.MultiImageSelectorActivity

/**
 * 类描述：拍摄入口
 */
class EntryActivity : BaseActivity() {

    private var rlEntryUpdataPhoto: RelativeLayout? = null
    private var rlEntryUpdataVideo: RelativeLayout? = null
    private var rlEntryPhotograph: RelativeLayout? = null
    private var rlEntryVideoLive: RelativeLayout? = null
    private var rlEntryVideoPlay: RelativeLayout? = null
    private var ivClose: ImageView? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_entry
    }

    override fun setUpView() {
        rlEntryUpdataPhoto = findViewById(R.id.rl_entry_updata_photo) as RelativeLayout
        rlEntryUpdataVideo = findViewById(R.id.rl_entry_updata_video) as RelativeLayout
        rlEntryPhotograph = findViewById(R.id.rl_entry_photograph) as RelativeLayout
        rlEntryVideoLive = findViewById(R.id.rl_entry_video_live) as RelativeLayout
        rlEntryVideoPlay = findViewById(R.id.rl_entry_video_play) as RelativeLayout
        ivClose = findViewById(R.id.iv_close) as ImageView

        AndroidPermissonRequest.verifyCameraPermissions(mContext!!)
    }

    override fun init() {
        rlEntryUpdataPhoto!!.setOnClickListener(onClickListener)
        rlEntryUpdataVideo!!.setOnClickListener(onClickListener)
        rlEntryPhotograph!!.setOnClickListener(onClickListener)
        rlEntryVideoLive!!.setOnClickListener(onClickListener)
        rlEntryVideoPlay!!.setOnClickListener(onClickListener)
        ivClose!!.setOnClickListener(onClickListener)
    }


    /**
     * 点击事件
     */
    internal var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.rl_entry_updata_photo//上传图片
            -> {
                ToastUtil.show("上传图片!")
                MultiImageSelectorActivity.start(mContext!!, true, 9, MultiImageSelectorActivity
                .MODE_MULTI)
            }
            R.id.rl_entry_updata_video//上传视频
            -> ToastUtil.show("上传视频!")
            R.id.rl_entry_photograph//拍照
            -> ToastUtil.show("拍照!")
            R.id.rl_entry_video_live//直播
            -> {
            }
            R.id.rl_entry_video_play//视频
            -> showVideoRecord()
            R.id.iv_close -> finish()
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
