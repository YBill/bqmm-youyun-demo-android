package com.youyun.bqmm.observer;

import com.youyun.bqmm.chat.model.MessageEntity;

/**
 * Created by Bill on 2016/9/8.
 */
public class Observer {

    public interface MessageObserver {
        void update(MessageEntity message);
    }

}
