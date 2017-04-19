package com.ybg.yxym.im.extra;

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

    public void init(UserExtraOperation userExtraOperation) {
        this.userExtraOperation = userExtraOperation;
    }

    public interface UserExtraOperation {
        void onAvatarClick(String userId);
    }
}
