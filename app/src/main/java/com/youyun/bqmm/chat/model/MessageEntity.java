package com.youyun.bqmm.chat.model;

import org.json.JSONArray;

/**
 * Created by 卫彪 on 2016/9/8.
 */
public class MessageEntity {

    public static final int CHAT_TYPE_RECV_TEXT = 0; // 接收文本
    public static final int CHAT_TYPE_SEND_TEXT = 1; // 发送文本
    public static final int CHAT_TYPE_RECV_EMOJI = 2; // 接收表情
    public static final int CHAT_TYPE_SEND_EMOJI = 3; // 发送表情

    private String text; // 消息内容
    private long timestamp; // 消息时间,13位
    private int msgType; // 消息类型，取值为上面static final定义的常量
    private String emojiType; // 单个表情：facetype 图文混排：emojitype
    private JSONArray emojiArray; // 表情json
    private String emojiName; // 表情名称

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getEmojiType() {
        return emojiType;
    }

    public void setEmojiType(String emojiType) {
        this.emojiType = emojiType;
    }

    public JSONArray getEmojiArray() {
        return emojiArray;
    }

    public void setEmojiArray(JSONArray emojiArray) {
        this.emojiArray = emojiArray;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public void setEmojiName(String emojiName) {
        this.emojiName = emojiName;
    }
}
