package com.bluedatax.wdpms.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ConnectActivity extends Activity {


    public String DEVICE_ID;
    public static final String TAG = "ConnectServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityControler.addActivity(this);


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        DEVICE_ID = tm.getDeviceId();
        Log.d(TAG, DEVICE_ID);

        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd"));

        WebSocketClient client = new WebSocketClient(URI.create("ws://192.168.0.9:19000?connect&prod=w65" +
                "&auid=89b5129f7d5f447562b632d724e4c7a0&duid=0118390-4b11-929a-1421-88d108874ba69&apc=2"), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.d(TAG, "Connected!");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
            }

            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message! %s", new String(data)));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {

                Log.e(TAG, "Error!", error);
            }
        }, extraHeaders);

        client.connect();


    }

//    @Override
//    protected void onDestroy () {
//        super.onDestroy();
//        ActivityControler.removeActivity(this);
//    }

}
