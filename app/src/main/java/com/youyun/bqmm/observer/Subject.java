package com.youyun.bqmm.observer;

import com.youyun.bqmm.chat.model.MessageEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/9/8.
 */
public enum Subject {

    INSTANCE;

    private List<Observer.MessageObserver> messageListObservers = new ArrayList<>();

    public void addMessageObserver(Observer.MessageObserver observer) {
        if (!messageListObservers.contains(observer))
            messageListObservers.add(observer);
    }

    public void removeMessageObserver(Observer.MessageObserver observer) {
        if (messageListObservers.contains(observer))
            messageListObservers.remove(observer);
    }

    public void notifyMessageObservers(MessageEntity messageEntity) {
        for (Observer.MessageObserver observer : messageListObservers) {
            observer.update(messageEntity);
        }
    }
}
