package com.bluedatax.wdpms.service;

import org.json.JSONObject;

/**
 * Created by xuyuanqiang on 7/9/16.
 */
public interface OnConnectListener {
    void onJSonObject(JSONObject json);

    void onError(Exception msg);

    void onConnected(String notice);

    void onOrderPush(String json);
}

