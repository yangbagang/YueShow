package com.ybg.yxym.im.extra;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by yangbagang on 2017/4/17.
 */

public class UserInfoExtra {

    private static UserInfoExtra instance = null;

    private UserExtraOperation userExtraOperation = null;

    private UserInfoExtra(){}

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

    public void logout() {
        JMessageClient.logout();
    }

    public void sendMsg(String userId) {
        Conversation.createSingleConversation(userId);
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
