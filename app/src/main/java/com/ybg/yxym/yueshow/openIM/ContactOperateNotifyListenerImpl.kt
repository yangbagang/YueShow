package com.ybg.yxym.yueshow.openIM

import com.alibaba.mobileim.contact.IYWContact
import com.alibaba.mobileim.contact.IYWContactOperateNotifyListener
import com.alibaba.mobileim.utility.IMNotificationUtils
import com.ybg.yxym.yueshow.app.ShowApplication

class ContactOperateNotifyListenerImpl : IYWContactOperateNotifyListener {

    /**
     * 用户请求加你为好友
     * 该回调在UI线程回调 ，请勿做太重的操作

     * @param contact 用户的信息
     * *
     * @param message 附带的备注
     */
    override fun onVerifyAddRequest(contact: IYWContact, message: String) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, contact.showName + "请求加你为好友")

        //                 //增加未读数的显示
        //                 YWConversation conversation = mIMKit.getConversationService().getCustomConversationByConversationId(SYSTEM_FRIEND_REQ_CONVERSATION);
        //                 if ( conversation!= null) {
        //                     YWCustomConversationUpdateModel model = new YWCustomConversationUpdateModel();
        //                     model.setIdentity(SYSTEM_FRIEND_REQ_CONVERSATION);
        //                     model.setLastestTime(new Date().getTime());
        //                     model.setUnreadCount(conversation.getUnreadCount() + 1);
        //                     if(conversation.getConversationBody() instanceof YWCustomConversationBody){
        //                         model.setExtraData(((YWCustomConversationBody)conversation.getConversationBody()).getExtraData());
        //                     }
        //                     if(mConversationService!=null)
        //                     mConversationService.updateOrCreateCustomConversation(model);
        //                 }

    }

    /**
     * 用户接受了你的好友请求
     * 该回调在UI线程回调 ，请勿做太重的操作

     * @param contact 用户的信息
     */
    override fun onAcceptVerifyRequest(contact: IYWContact) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, contact.showName + "接受了你的好友请求")
    }

    /**
     * 用户拒绝了你的好友请求
     * 该回调在UI线程回调 ，请勿做太重的操作
     * @param  contact 用户的信息
     */
    override fun onDenyVerifyRequest(contact: IYWContact) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, contact.showName + "拒绝了你的好友请求")
    }

    /**
     * 云旺服务端（或其它终端）进行了好友添加操作
     * 该回调在UI线程回调 ，请勿做太重的操作

     * @param contact 用户的信息
     */
    override fun onSyncAddOKNotify(contact: IYWContact) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, "云旺服务端（或其它终端）进行了好友添加操作对" + contact.showName)

    }

    /**
     * 用户从好友名单删除了您
     * 该回调在UI线程回调 ，请勿做太重的操作

     * @param contact 用户的信息
     */
    override fun onDeleteOKNotify(contact: IYWContact) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, contact.showName + "从好友名单删除了您")
    }

    override fun onNotifyAddOK(contact: IYWContact) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, contact.showName + "添加您为好友了")
    }

}
