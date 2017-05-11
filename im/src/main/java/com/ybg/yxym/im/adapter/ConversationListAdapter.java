package com.ybg.yxym.im.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ybg.yxym.im.R;
import com.ybg.yxym.im.app.IMApplication;
import com.ybg.yxym.im.chatting.CircleImageView;
import com.ybg.yxym.im.chatting.utils.HandleResponseCode;
import com.ybg.yxym.im.chatting.utils.TimeFormat;
import com.ybg.yxym.im.constants.IMConstants;
import com.ybg.yxym.im.tools.AvatarUtil;
import com.ybg.yxym.im.tools.SortConversationList;
import com.ybg.yxym.im.tools.ViewHolder;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationListAdapter extends BaseAdapter {

    List<Conversation> mDatas;
    private Activity mContext;
    private Map<String, String> mDraftMap = new HashMap<String, String>();
    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3003;
    private SparseBooleanArray mArray = new SparseBooleanArray();
    private HashMap<Conversation, Integer> mAtConvMap = new HashMap<Conversation, Integer>();

    public ConversationListAdapter(Activity context, List<Conversation> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    /**
     * 收到消息后将会话置顶
     *
     * @param conv 要置顶的会话
     */
    public void setToTop(Conversation conv) {
        for (Conversation conversation : mDatas) {
            if (conv.getId().equals(conversation.getId())) {
                mDatas.remove(conversation);
                mDatas.add(0, conv);
                mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
                mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                return;
            }
        }
        //如果是新的会话
        mDatas.add(0, conv);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    public void sortConvList() {
        SortConversationList sortConvList = new SortConversationList();
        Collections.sort(mDatas, sortConvList);
        notifyDataSetChanged();
    }

    public void addNewConversation(Conversation conv) {
        mDatas.add(0, conv);
        notifyDataSetChanged();
    }

    public void addAndSort(Conversation conv) {
        mDatas.add(conv);
        SortConversationList sortConvList = new SortConversationList();
        Collections.sort(mDatas, sortConvList);
        notifyDataSetChanged();
    }

    public void deleteConversation(Conversation conversation) {
        mDatas.remove(conversation);
        notifyDataSetChanged();
    }

    public void putDraftToMap(Conversation conv, String draft) {
        mDraftMap.put(conv.getId(), draft);
    }

    public void delDraftFromMap(Conversation conv) {
        mArray.delete(mDatas.indexOf(conv));
        mAtConvMap.remove(conv);
        mDraftMap.remove(conv.getId());
        notifyDataSetChanged();
    }

    public String getDraft(String convId) {
        return mDraftMap.get(convId);
    }

    public boolean includeAtMsg(Conversation conv) {
        if (mAtConvMap.size() > 0) {
            Iterator<Map.Entry<Conversation, Integer>> iterator = mAtConvMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Conversation, Integer> entry = iterator.next();
                if (conv == entry.getKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getAtMsgId(Conversation conv) {
        return mAtConvMap.get(conv);
    }

    public void putAtConv(Conversation conv, int msgId) {
        mAtConvMap.put(conv, msgId);
    }


    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Conversation getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Conversation convItem = mDatas.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_conversation, null);
        }
        final CircleImageView headIcon = ViewHolder.get(convertView, R.id.msg_item_head_icon);
        TextView convName = ViewHolder.get(convertView, R.id.conv_item_name);
        TextView content = ViewHolder.get(convertView, R.id.msg_item_content);
        TextView datetime = ViewHolder.get(convertView, R.id.msg_item_date);
        TextView newMsgNumber = ViewHolder.get(convertView, R.id.new_msg_number);
        String draft = mDraftMap.get(convItem.getId());
        //如果该会话草稿为空，显示最后一条消息
        if (TextUtils.isEmpty(draft)) {
            Message lastMsg = convItem.getLatestMessage();
            if (lastMsg != null) {
                TimeFormat timeFormat = new TimeFormat(mContext, lastMsg.getCreateTime());
                datetime.setText(timeFormat.getTime());
                String contentStr;
                // 按照最后一条消息的消息类型进行处理
                switch (lastMsg.getContentType()) {
                    case image:
                        contentStr = mContext.getString(R.string.type_picture);
                        break;
                    case voice:
                        contentStr = mContext.getString(R.string.type_voice);
                        break;
                    case location:
                        contentStr = mContext.getString(R.string.type_location);
                        break;
                    case file:
                        contentStr = mContext.getString(R.string.type_file);
                        break;
                    case video:
                        contentStr = mContext.getString(R.string.type_video);
                        break;
                    case eventNotification:
                        contentStr = mContext.getString(R.string.group_notification);
                        break;
                    case custom:
                        CustomContent customContent = (CustomContent) lastMsg.getContent();
                        Boolean isBlackListHint = customContent.getBooleanValue("blackList");
                        Boolean notFriendFlag = customContent.getBooleanValue("notFriend");
                        if (isBlackListHint != null && isBlackListHint) {
                            contentStr = mContext.getString(R.string.jmui_server_803008);
                        }/* else if (notFriendFlag != null && notFriendFlag) {
                            contentStr = mContext.getString(R.string.send_target_is_not_friend);
                        } */ else {
                            String type = customContent.getStringValue("type");
                            if ("gift".equals(type)) {
                                contentStr = mContext.getString(R.string.type_gift);
                            } else {
                                contentStr = mContext.getString(R.string.type_custom);
                            }
                        }
                        break;
                    default:
                        contentStr = ((TextContent) lastMsg.getContent()).getText();
                }
                MessageContent msgContent = lastMsg.getContent();
                Boolean isRead = msgContent.getBooleanExtra("isRead");
                if (lastMsg.isAtMe()) {
                    if (null != isRead && isRead) {
                        mArray.delete(position);
                        mAtConvMap.remove(convItem);
                    } else {
                        mArray.put(position, true);
                    }
                }

                if (mArray.get(position) && IMApplication.getInstance().isNeedAtMsg()) {
                    //有人@我 文字提示
                    contentStr = mContext.getString(R.string.somebody_at_me) + contentStr;
                    SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setText(builder);
                } else {
                    content.setText(contentStr);
                }
            } else {
                TimeFormat timeFormat = new TimeFormat(mContext, convItem.getLastMsgDate());
                datetime.setText(timeFormat.getTime());
                content.setText("");
            }
        } else {
            draft = mContext.getString(R.string.draft) + draft;
            SpannableStringBuilder builder = new SpannableStringBuilder(draft);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setText(builder);
        }

        // 如果是单聊
        if (convItem.getType().equals(ConversationType.single)) {
            convName.setText(convItem.getTitle());
            UserInfo userInfo = (UserInfo) convItem.getTargetInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserName())) {
                AvatarUtil.getAvatarId(userInfo.getUserName(), new AvatarUtil.GetAvatarCallback() {
                    @Override
                    public void displayAvatar(final String avatarId) {
                        mContext.runOnUiThread(new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                Picasso.with(mContext).load(IMConstants.FILE_SERVER_PREVIEW + avatarId).into(headIcon);
                            }
                        });
                    }
                });
            } else {
                headIcon.setImageResource(R.mipmap.jmui_head_icon);
            }
        } else {
            headIcon.setImageResource(R.mipmap.group);
            convName.setText(convItem.getTitle());
            Log.d("ConversationListAdapter", "Conversation title: " + convItem.getTitle());
        }

        // 更新Message的数量,
        if (convItem.getUnReadMsgCnt() > 0) {
            newMsgNumber.setVisibility(View.VISIBLE);
            if (convItem.getUnReadMsgCnt() < 100) {
                newMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
            } else {
                newMsgNumber.setText(mContext.getString(R.string.hundreds_of_unread_msgs));
            }
        } else {
            newMsgNumber.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class UIHandler extends Handler {

        private final WeakReference<ConversationListAdapter> mAdapter;

        public UIHandler(ConversationListAdapter adapter) {
            mAdapter = new WeakReference<ConversationListAdapter>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ConversationListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
}
