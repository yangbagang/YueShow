package com.ybg.yxym.im.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.ybg.yxym.im.R;
import com.ybg.yxym.im.controller.SendFileController;
import com.ybg.yxym.im.view.SendFileView;

public class SendFileActivity extends BaseActivity {

    private SendFileView mView;
    private SendFileController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        mView = (SendFileView) findViewById(R.id.send_file_view);
        mView.initModule();
        mController = new SendFileController(this, mView);
        mView.setOnClickListener(mController);
        mView.setOnPageChangeListener(mController);
        setCustomTitle("选择文件");
    }

    public FragmentManager getSupportFragmentManger() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
