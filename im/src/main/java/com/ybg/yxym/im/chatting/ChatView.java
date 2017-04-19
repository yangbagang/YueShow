package com.ybg.yxym.im.chatting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ybg.yxym.im.activity.AtMemberActivity;
import com.ybg.yxym.im.chatting.utils.IdHelper;
import com.ybg.yxym.im.chatting.utils.SharePreferenceManager;
import com.ybg.yxym.im.constants.IMConstants;

import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;

public class ChatView extends RelativeLayout {

	private LinearLayout mBackground;
	private TableLayout mMoreMenuTl;
	private DropDownListView mChatListView;
	private RecordVoiceButton mVoiceBtn;
	public EditText mChatInputEt;
	private ImageButton mSwitchIb;
	private ImageButton mExpressionIb;
	private ImageButton mAddFileIb;
	private ImageButton mTakePhotoIb;
	private ImageButton mPickPictureIb;
	private ImageButton mLocationIb;
	private ImageButton mSendFileIb;
	private Button mSendMsgBtn;
	Context mContext;
    private OnSizeChangedListener mListener;
    private OnKeyBoardChangeListener mKeyboardListener;
    private Button mAtMeBtn;

	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;
	private boolean mHasInit;
	private boolean mHasKeybord;
	private int mHeight;
    private Conversation mConv;
    private boolean mLongClick = false;

	public ChatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}


	public void initModule(float density, int densityDpi) {
        mChatListView = (DropDownListView) findViewById(IdHelper.getViewID(mContext, "jmui_chat_list"));
        mVoiceBtn = (RecordVoiceButton) findViewById(IdHelper.getViewID(mContext, "jmui_voice_btn"));
        mChatInputEt = (EditText) findViewById(IdHelper.getViewID(mContext, "jmui_chat_input_et"));
        mSwitchIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_switch_voice_ib"));
        mAddFileIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_add_file_btn"));
        mTakePhotoIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_pick_from_camera_btn"));
        mPickPictureIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_pick_from_local_btn"));
        mSendMsgBtn = (Button) findViewById(IdHelper.getViewID(mContext, "jmui_send_msg_btn"));
        mBackground = (LinearLayout) findViewById(IdHelper.getViewID(mContext, "jmui_chat_background"));
        mMoreMenuTl = (TableLayout) findViewById(IdHelper.getViewID(mContext, "jmui_more_menu_tl"));
        mExpressionIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_expression_btn"));
        mLocationIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_send_location_btn"));
        mSendFileIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_send_file_btn"));
        mAtMeBtn = (Button) findViewById(IdHelper.getViewID(mContext, "jmui_at_me_btn"));

        mBackground.requestFocus();
		mChatInputEt.addTextChangedListener(watcher);
        mChatInputEt.setOnFocusChangeListener(listener);
        mChatInputEt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mChatInputEt.setSingleLine(false);
        mChatInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dismissMoreMenu();
                    Log.i("ChatView", "dismissMoreMenu()----------");
                }
                return false;
            }
        });
        mChatInputEt.setMaxLines(4);
        setMoreMenuHeight();
	}

    public void setMoreMenuHeight() {
        int softKeyboardHeight = SharePreferenceManager.getCachedKeyboardHeight();
        if(softKeyboardHeight > 0){
            mMoreMenuTl.setLayoutParams(new LinearLayout
                    .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, softKeyboardHeight));
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mListener != null){
            mListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mHasInit) {
            mHasInit = true;
            mHeight = b;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            }
        } else {
            mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            mHeight = mHeight < b ? b : mHeight;
        }
        if (mHasInit && mHeight > b) {
            mHasKeybord = true;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
            }
        }
        if (mHasInit && mHasKeybord && mHeight == b) {
            mHasKeybord = false;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
            }
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        this.mListener = listener;
    }

    public void setInputText(String text) {
        mChatInputEt.setText(text);
    }

    public void setAtText(String name) {
        mLongClick = true;
        String input = getChatInput() + name + " ";
        mChatInputEt.setText(input);
        mChatInputEt.setSelection(mChatInputEt.getText().length());
    }

    public void setConversation(Conversation conv) {
        this.mConv = conv;
    }

    public void showAtMeButton() {
        mAtMeBtn.setVisibility(VISIBLE);
    }

    public void setToPosition(int position) {
        mChatListView.smoothScrollToPosition(position);
        mAtMeBtn.setVisibility(GONE);
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    public interface OnKeyBoardChangeListener {
        void onKeyBoardStateChange(int state);
    }

    public void setOnKbdStateListener(OnKeyBoardChangeListener listener) {
        mKeyboardListener = listener;
    }

    private TextWatcher watcher = new TextWatcher() {
		private CharSequence temp = "";
		@Override
		public void afterTextChanged(Editable arg0) {
			if (temp.length() > 0) {
				mAddFileIb.setVisibility(View.GONE);
				mSendMsgBtn.setVisibility(View.VISIBLE);
                mLongClick = false;
			} else {
				mAddFileIb.setVisibility(View.VISIBLE);
				mSendMsgBtn.setVisibility(View.GONE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int count, int after) {
			temp = s;
            if (s.length() > 0 && after >= 1 && s.subSequence(start, start + 1).charAt(0) == '@' && !mLongClick) {
                if (null != mConv && mConv.getType() == ConversationType.group) {
                    mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
                    Intent intent = new Intent(mContext, AtMemberActivity.class);
                    intent.putExtra(IMConstants.GROUP_ID, Long.parseLong(mConv.getTargetId()));
                    ((Activity) mContext).startActivityForResult(intent, IMConstants.REQUEST_CODE_AT_MEMBER);
                }
            }
		}

	};

    public void focusToInput(boolean inputFocus) {
        if (inputFocus) {
            mChatInputEt.requestFocus();
            Log.i("ChatView", "show softInput");
        } else {
            mAddFileIb.requestFocusFromTouch();
        }
    }

    OnFocusChangeListener listener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Log.i("ChatView", "Input focus");
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissMoreMenu();
                    }
                });
            }
        }
    };

	public void setListeners(OnClickListener onClickListener) {
        mChatInputEt.setOnClickListener(onClickListener);
		mSendMsgBtn.setOnClickListener(onClickListener);
		mSwitchIb.setOnClickListener(onClickListener);
		mVoiceBtn.setOnClickListener(onClickListener);
        mExpressionIb.setOnClickListener(onClickListener);
		mAddFileIb.setOnClickListener(onClickListener);
		mTakePhotoIb.setOnClickListener(onClickListener);
		mPickPictureIb.setOnClickListener(onClickListener);
		mLocationIb.setOnClickListener(onClickListener);
		mSendFileIb.setOnClickListener(onClickListener);
        mAtMeBtn.setOnClickListener(onClickListener);
	}

    public void setOnTouchListener(OnTouchListener listener) {
        mChatListView.setOnTouchListener(listener);
        mChatInputEt.setOnTouchListener(listener);
    }

	public void setChatListAdapter(MsgListAdapter adapter) {
		mChatListView.setAdapter(adapter);
	}

    public void setListItemClickListener(AdapterView.OnItemClickListener listener) {
        mChatListView.setOnItemClickListener(listener);
    }

	//如果是文字输入
	public void isKeyBoard() {
		mSwitchIb.setBackgroundResource(IdHelper.getDrawable(mContext, "jmui_voice"));
		mChatInputEt.setVisibility(View.VISIBLE);
		mVoiceBtn.setVisibility(View.GONE);
		mExpressionIb.setVisibility(View.GONE);
        if (mChatInputEt.getText().length() > 0) {
            mSendMsgBtn.setVisibility(View.VISIBLE);
            mAddFileIb.setVisibility(View.GONE);
        }else {
            mSendMsgBtn.setVisibility(View.GONE);
            mAddFileIb.setVisibility(View.VISIBLE);
        }
	}

	//语音输入
	public void notKeyBoard(MsgListAdapter adapter, ChatView chatView) {
		mChatInputEt.setVisibility(View.GONE);
		mSwitchIb.setBackgroundResource(IdHelper.getDrawable(mContext, "jmui_keyboard"));
		mVoiceBtn.setVisibility(View.VISIBLE);
		mVoiceBtn.initConv(mConv, adapter, chatView);
		mExpressionIb.setVisibility(View.GONE);
        mSendMsgBtn.setVisibility(View.GONE);
        mAddFileIb.setVisibility(View.VISIBLE);
	}

	public String getChatInput() {
		return mChatInputEt.getText().toString();
	}

	public void clearInput() {
		mChatInputEt.setText("");
	}

	public void setToBottom() {
        mChatListView.clearFocus();
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
            }
        });
    }

    public EditText getInputView() {
        return mChatInputEt;
    }

    public TableLayout getMoreMenu() {
        return mMoreMenuTl;
    }

	public void showMoreMenu() {
		mMoreMenuTl.setVisibility(View.VISIBLE);
	}

    public void invisibleMoreMenu() {
        mMoreMenuTl.setVisibility(INVISIBLE);
    }

	public void dismissMoreMenu() {
		mMoreMenuTl.setVisibility(View.GONE);
	}

    public void dismissRecordDialog() {
     mVoiceBtn.dismissDialog();
    }

    public void releaseRecorder() {
        mVoiceBtn.releaseRecorder();
    }

	public DropDownListView getListView() {
		return mChatListView;
	}
}
