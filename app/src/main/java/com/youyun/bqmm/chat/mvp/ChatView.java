package com.youyun.bqmm.chat.mvp;

import com.youyun.bqmm.chat.model.MessageEntity;

/**
 * Created by 卫彪 on 2016/9/8.
 */
public interface ChatView {

    /**
     * 发送文本回调
     *
     * @param result
     * @param text
     * @param time
     */
    void sendText(boolean result, String text, long time);

    void sendEmoji(boolean result, String emojiJson, long time);

    /**
     * 收到消息
     *
     * @param message
     */
    void receiveText(MessageEntity message);
}
