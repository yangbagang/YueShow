package com.ybg.yxym.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ybg.yxym.im.R;
import com.ybg.yxym.im.adapter.AtMemberAdapter;
import com.ybg.yxym.im.constants.IMConstants;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class AtMemberActivity extends BaseActivity {

    private List<UserInfo> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_member);
        ListView listView = (ListView) findViewById(R.id.at_member_list_view);

        setCustomTitle(getString(R.string.select_member_to_reply));

        long groupId = getIntent().getLongExtra(IMConstants.GROUP_ID, 0);
        if (0 != groupId) {
            Conversation conv = JMessageClient.getGroupConversation(groupId);
            GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
            mList = groupInfo.getGroupMembers();
            mList.remove(JMessageClient.getMyInfo());
            AtMemberAdapter adapter = new AtMemberAdapter(this, mList);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserInfo userInfo = mList.get(position);
                Intent intent = new Intent();
                intent.putExtra(IMConstants.NAME, userInfo.getNickname());
                intent.putExtra(IMConstants.TARGET_ID, userInfo.getUserName());
                intent.putExtra(IMConstants.TARGET_APP_KEY, userInfo.getAppKey());
                setResult(IMConstants.RESULT_CODE_AT_MEMBER, intent);
                finish();
            }
        });
    }


}
