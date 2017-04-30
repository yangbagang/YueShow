package com.ybg.yxym.im.extra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ybg.yxym.im.chatting.ChatActivity;
import com.ybg.yxym.im.constants.IMConstants;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by yangbagang on 2017/4/17.
 */

public class UserInfoExtra {

    private static UserInfoExtra instance = null;

    private UserExtraOperation userExtraOperation = null;

    private Activity giftActivity = null;

    private UserInfoExtra() {
    }

    public static UserInfoExtra getInstance() {
        if (instance == null) {
            instance = new UserInfoExtra();
        }
        return instance;
    }

    public void clickOnAvatar(String userId) {
        if (userExtraOperation != null) {
            userExtraOperation.onAvatarClick(userId);
        }
    }

    public void login(String userid, String password) {
        JMessageClient.login(userid, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (userExtraOperation != null) {
                    userExtraOperation.onLoginCallback(i, s);
                }
            }
        });
    }

    public void viewGroupInfo(Long groupId) {
        if (userExtraOperation != null) {
            userExtraOperation.viewGroupInfo(groupId);
        }
    }

    public Activity getGiftActivity() {
        return giftActivity;
    }

    public void setGiftActivity(Activity giftActivity) {
        this.giftActivity = giftActivity;
    }

    public void logout() {
        JMessageClient.logout();
    }

    public void sendGiftMsg(String userId, String giftName, String giftImgId) {
        Conversation conversation = Conversation.createSingleConversation(userId);
        Map<String, String> giftMap = new HashMap<String, String>();
        giftMap.put("type", "gift");
        giftMap.put("giftName", giftName);
        giftMap.put("giftImgId", giftImgId);
        Message message = conversation.createSendCustomMessage(giftMap);
        JMessageClient.sendMessage(message);
    }

    public void openUserChatWin(Context context, String userId, String nickName) {
        Intent intentChat = new Intent(context, ChatActivity.class);
        intentChat.putExtra(IMConstants.CONV_TITLE, nickName);
        intentChat.putExtra(IMConstants.TARGET_ID, userId);
        intentChat.putExtra(IMConstants.TARGET_APP_KEY, IMConstants.JPUSH_APPKEY);
        context.startActivity(intentChat);
    }

    public boolean hasInit() {
        return userExtraOperation != null;
    }

    public void init(UserExtraOperation userExtraOperation) {
        this.userExtraOperation = userExtraOperation;
    }

    public interface UserExtraOperation {
        void onAvatarClick(String userId);

        void onLoginCallback(int status, String desc);

        void viewGroupInfo(Long groupId);
    }

}
