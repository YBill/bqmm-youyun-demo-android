package com.youyun.bqmm.receive;

import android.content.Context;
import android.util.Log;

import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.NotifyCenter;
import com.ioyouyun.wchat.message.TextMessage;
import com.ioyouyun.wchat.message.WeimiNotice;
import com.youyun.bqmm.chat.model.MessageEntity;
import com.youyun.bqmm.observer.Subject;
import com.youyun.bqmm.utils.YouyunUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/9/8.
 */
public class ReceiveRunnable implements Runnable {

    private Context context;

    public ReceiveRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        WeimiNotice weimiNotice = null;
        while (true) {
            try {
                weimiNotice = NotifyCenter.clientNotifyChannel.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NoticeType type = weimiNotice.getNoticeType();
            Log.v("Bill", "type:" + type);

            if (NoticeType.textmessage == type) {
                textMessageMethod(weimiNotice);
            }else if(NoticeType.emotion == type){
                emojiMessageMethod(weimiNotice);
            }

        }
    }

    private void emojiMessageMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        MessageEntity message = new MessageEntity();
        JSONObject obj = null;
        try {
            obj = new JSONObject(textMessage.text);
            String categary = obj.optString("categary");
            if(YouyunUtils.EMOJITYPE.equals(categary)){
                // 图文混排
                message.setEmojiType(YouyunUtils.EMOJITYPE);
            }else{
                // 单表情
                message.setEmojiType(YouyunUtils.FACETYPE);
            }
            JSONArray array = YouyunUtils.makeJSONArray(obj.getJSONArray("content"));
            message.setEmojiArray(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setTimestamp(textMessage.time);
        message.setMsgType(MessageEntity.CHAT_TYPE_RECV_EMOJI);
        Subject.INSTANCE.notifyMessageObservers(message);
    }

    private void textMessageMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        MessageEntity message = new MessageEntity();
        message.setText(textMessage.text);
        message.setTimestamp(textMessage.time);
        message.setMsgType(MessageEntity.CHAT_TYPE_RECV_TEXT);
        Subject.INSTANCE.notifyMessageObservers(message);
    }

}
