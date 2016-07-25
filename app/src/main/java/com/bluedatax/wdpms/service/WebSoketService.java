package com.bluedatax.wdpms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bluedatax.wdpms.utils.WebSocketClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class WebSoketService extends Service {
    public OnConnectListener onConnectListener;
    private final String ACTION_NAME = "查询账单";
    public static WebSocket mConnection;
    private Intent intent = new Intent(ACTION_NAME);
    private String duid;

    /**
     * 注册回调接口的方法，供外部调用
     * @param
     */
    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
//        duid = intent.getStringExtra("duid");
        startConnect();
        return super.onStartCommand(intent, flags, startId);
    }
    public void startConnect(){

        mConnection = new WebSocketConnection();

        try {
            mConnection.connect("ws://192.168.0.5:19600/websocket", new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    Log.d("Status","connect:ws://192.168.0.5:19600/websocket");
                    Log.d("onConnect", "Connected!");
                    String notice = "Connected";
                    Log.d("判断连接状态", notice);
                    if(onConnectListener != null ) {
                        Log.d("发送的通知", notice);
                        onConnectListener.onConnected(notice);
                    }
//                    savePrefs();
                }

                @Override
                public void onTextMessage(String message) {
                    Log.d("onMessage", String.format("Got string message! %s", message));
                    try {
                        JSONObject parseJson = new JSONObject(message);
                        System.out.println( "---------------" + parseJson.getInt("action"));
                        if (onConnectListener != null && parseJson.getInt("action") == 60) {         //初始化
                            Log.d("发送给UI的json数据:", parseJson + "");
                            onConnectListener.onJSonObject(parseJson);
                        } else if (parseJson.getInt("action") == 62) {                               //登录
                            onConnectListener.onJSonObject(parseJson);
                        } else if (onConnectListener != null && parseJson.getInt("action") == 61) {  //注册
                            onConnectListener.onJSonObject(parseJson);
                        } else if (onConnectListener != null && parseJson.getInt("action") == 64) {  //查询账单
                            intent.putExtra("bill",message);
                            Log.d("发送广播设备事件通知", message);
                            sendBroadcast(intent);
                        } else if (onConnectListener != null && parseJson.getInt("action") == 63 ) { //接收订单
                            Log.d("接收订单数据",message);
                            onConnectListener.onOrderPush(message);
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("Status","返回码" + code + "\n" + reason);
                    if (code == 2) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"网络连接错误，请重新连接!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (WebSocketException e) {

            Log.d("WebSocket", e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return new MyBinder();
    }
    public class MyBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public WebSoketService getService() {

            return WebSoketService.this;
        }
    }
}
