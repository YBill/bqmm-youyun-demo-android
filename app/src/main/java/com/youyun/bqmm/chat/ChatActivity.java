package com.youyun.bqmm.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMM;
import com.melink.bqmmsdk.sdk.BQMMMessageHelper;
import com.melink.bqmmsdk.sdk.IBqmmSendMessageListener;
import com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard;
import com.melink.bqmmsdk.ui.keyboard.IBQMMUnicodeEmojiProvider;
import com.melink.bqmmsdk.widget.BQMMEditView;
import com.melink.bqmmsdk.widget.BQMMSendButton;
import com.youyun.bqmm.R;
import com.youyun.bqmm.chat.adapter.ChatAdapter;
import com.youyun.bqmm.chat.model.MessageEntity;
import com.youyun.bqmm.chat.mvp.ChatPresenter;
import com.youyun.bqmm.chat.mvp.ChatView;
import com.youyun.bqmm.utils.YouyunUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatView {

    private BQMMKeyboard bqmmKeyboard;
    private ListView mRealListView;
    List<MessageEntity> datas = new ArrayList<>();
    private ChatAdapter adapter;
    private View inputbox;
    private BQMMSendButton bqmmSend;
    private CheckBox bqmmKeyboardOpen;
    private BQMMEditView bqmmEditView;
    private TextView titleText;

    private boolean mPendingShowPlaceHolder;
    private InputMethodManager manager;
    private String toUid;
    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BQMM.getInstance().initConfig(getApplicationContext(), "772d564e7fee4acaa7bf2a038b9116c6", "529226aa0eb4425b85150600dd23e2f9");
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        addListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BQMM.getInstance().destory();
        presenter.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        toUid = intent.getStringExtra("touid");
        titleText.setText(toUid);

        presenter = new ChatPresenter(this, this);
        adapter = new ChatAdapter(this, datas);
        mRealListView.setAdapter(adapter);

        BQMM.getInstance().setEditView(bqmmEditView);
        BQMM.getInstance().setKeyboard(bqmmKeyboard);
        BQMM.getInstance().setSendButton(bqmmSend);
        BQMM.getInstance().load();
        UnicodeToEmoji.initPhotos(this);
        BQMM.getInstance().setUnicodeEmojiProvider(new IBQMMUnicodeEmojiProvider() {
            @Override
            public Drawable getDrawableFromCodePoint(int i) {
                return UnicodeToEmoji.EmojiImageSpan.getEmojiDrawable(i);
            }
        });
    }

    private void addListener() {
        bqmmEditView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Keyboard -> BQMM
                if (mPendingShowPlaceHolder) {
                    // 在设置mPendingShowPlaceHolder时已经调用了隐藏Keyboard的方法，直到Keyboard隐藏前都取消重绘
                    if (isSoftInputShown()) {
                        /*ViewGroup.LayoutParams params = bqmmKeyboard.getLayoutParams();
                        int distance = getSupportSoftInputHeight();
                        // 调整PlaceHolder高度
                        if (distance != params.height) {
                            params.height = distance;
                            bqmmKeyboard.setLayoutParams(params);
                        }*/
                        return false;
                    } else {
                        mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                        bqmmKeyboard.showKeyboard();
                        mPendingShowPlaceHolder = false;
                        return false;
                    }
                } else {//BQMM -> Keyboard
                    if (bqmmKeyboard.isKeyboardVisible() && isSoftInputShown()) {
                        mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                        bqmmKeyboard.hideKeyboard();
                        return false;
                    }
                }
                return true;
            }
        });
        bqmmEditView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bqmmKeyboardOpen.setChecked(false);
                return false;
            }
        });
        bqmmKeyboardOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bqmmKeyboard.isKeyboardVisible()) { // PlaceHolder -> Keyboard
                    showSoftInput(bqmmEditView);
                } else if (isSoftInputShown()) { // Keyboard -> PlaceHolder
                    mPendingShowPlaceHolder = true;
                    hideSoftInput(bqmmEditView);
                } else { // Just show PlaceHolder
                    mRealListView.setSelection(mRealListView.getAdapter().getCount() - 1);
                    bqmmKeyboard.showKeyboard();
                }
            }
        });
        mRealListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bqmmKeyboardOpen.setChecked(false);
                if (bqmmKeyboard.isKeyboardVisible()) {
                    bqmmKeyboard.hideKeyboard();
                }
                closebroad();
                return false;
            }
        });

        bqmmEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BQMM.getInstance().startShortcutPopupWindowByoffset(ChatActivity.this, s.toString(), bqmmSend, 0, DensityUtils.dip2px(ChatActivity.this, 4));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        BQMM.getInstance().setBqmmSendMsgListener(new IBqmmSendMessageListener() {
            /**
             * 单个大表情消息
             */
            @Override
            public void onSendFace(Emoji face) {
                String json = YouyunUtils.makeJsonStr(face);
                presenter.sendEmojiText(toUid, json);
            }

            /**
             * 图文混排消息
             */
            @Override
            public void onSendMixedMessage(List<Object> emojis, boolean isMixedMessage) {
                if (isMixedMessage) {
                    // 图文混排消息
                    String json = YouyunUtils.makeJsonStr(emojis);
                    presenter.sendEmojiText(toUid, json);
                } else {
                    // 纯文本消息
                    String msgString = BQMMMessageHelper.getMixedMessageString(emojis);
                    presenter.sendText(toUid, msgString);
                }
            }
        });
    }

    private void initView() {
        setToolBar();

        titleText = (TextView) findViewById(R.id.tv_title);
        inputbox = findViewById(R.id.messageToolBox);
        bqmmKeyboard = (BQMMKeyboard) findViewById(R.id.chat_msg_input_box);
        mRealListView = (ListView) findViewById(R.id.chat_listview);
        mRealListView.setSelector(android.R.color.transparent);
        bqmmSend = (BQMMSendButton) findViewById(R.id.chatbox_send);
        bqmmKeyboardOpen = (CheckBox) findViewById(R.id.chatbox_open);
        bqmmEditView = (BQMMEditView) findViewById(R.id.chatbox_message);
        bqmmEditView.requestFocus();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void sendText(boolean result, String text, long time) {
        if (result) {
            MessageEntity message = new MessageEntity();
            message.setText(text);
            message.setTimestamp(time);
            message.setMsgType(MessageEntity.CHAT_TYPE_SEND_TEXT);
            datas.add(message);
            adapter.refresh(datas);
        }
    }

    @Override
    public void sendEmoji(boolean result, String emojiJson, long time) {
        if (result) {
            try {
                MessageEntity message = new MessageEntity();
                JSONObject obj = new JSONObject(emojiJson);
                String categary = obj.optString("categary");
                if (YouyunUtils.EMOJITYPE.equals(categary)) {
                    // 图文混排
                    message.setEmojiType(YouyunUtils.EMOJITYPE);
                } else {
                    // 单表情
                    message.setEmojiType(YouyunUtils.FACETYPE);
                }
                JSONArray array = YouyunUtils.makeJSONArray(obj.getJSONArray("content"));
                message.setEmojiArray(array);
                message.setTimestamp(time);
                message.setMsgType(MessageEntity.CHAT_TYPE_SEND_EMOJI);
                datas.add(message);
                adapter.refresh(datas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveText(MessageEntity message) {
        if (message != null) {
            datas.add(message);
            adapter.refresh(datas);
        }
    }

    /**
     * 关闭软键盘或表情
     */
    private void closebroad() {
        if (bqmmKeyboard.isKeyboardVisible()) {
            bqmmKeyboard.hideKeyboard();
        } else if (isSoftInputShown()) {
            hideSoftInput(bqmmEditView);
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput(View view) {
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput(View view) {
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            Log.d("Bill", "getSoftButtonsBarHeight:" + getSoftButtonsBarHeight());
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
            Log.w("Bill", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        Log.d("Bill", "softInputHeight:" + softInputHeight);
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
