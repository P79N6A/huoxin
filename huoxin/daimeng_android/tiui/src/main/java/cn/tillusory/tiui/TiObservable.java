package cn.tillusory.tiui;

import java.util.Observable;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiObservable extends Observable {

    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    @Override
    public synchronized void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }

}
