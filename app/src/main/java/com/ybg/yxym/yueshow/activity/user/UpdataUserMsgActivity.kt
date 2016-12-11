package com.ybg.yxym.yueshow.activity.user

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.rengwuxian.materialedittext.MaterialEditText
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class UpdataUserMsgActivity : BaseActivity() {

    private lateinit var metMsg: MaterialEditText

    private var type: String? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_updata_user_msg
    }

    override fun setUpView() {
        metMsg = findViewById(R.id.met_msg) as MaterialEditText
        val up_msg = intent
        type = up_msg.getStringExtra("type")
        setCustomTitle(type!!)
    }

    override fun init() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val response = Intent(mContext, MyInformationActivity::class.java)
                if(type.equals("昵称")){
                    response.putExtra("nickname", metMsg.text.toString())
                    setResult(RESULT_OK, response);
                }else if(type.equals("职业")){
                    response.putExtra("profession", metMsg.text.toString())
                    setResult(RESULT_OK, response);
                }else if(type.equals("个性签名")){
                    response.putExtra("signname", metMsg.text.toString())
                    setResult(RESULT_OK, response);
                }
                finish();
                Log.d("UpdataUserMsgActivity","修改信息页面： " + metMsg.text.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
