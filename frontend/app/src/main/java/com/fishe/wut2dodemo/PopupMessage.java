package com.fishe.wut2dodemo;

/**
 * Created by zhiyuan on 4/12/16.
 */
public class PopupMessage {
    private static PopupMessage ourInstance = new PopupMessage();

    public static PopupMessage getInstance() {
        return ourInstance;
    }

    private PopupMessage() {
    }


}
