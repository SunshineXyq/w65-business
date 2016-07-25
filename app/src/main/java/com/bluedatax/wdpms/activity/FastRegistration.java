package com.bluedatax.wdpms.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluedatax.wdpms.BaseActivity;
import com.bluedatax.wdpms.MainActivity;
import com.bluedatax.wdpms.R;
import com.bluedatax.wdpms.service.MyService;
import com.bluedatax.wdpms.service.OnConnectListener;
import com.bluedatax.wdpms.service.WebSoketService;
import com.bluedatax.wdpms.utils.GetTime;
import com.bluedatax.wdpms.utils.MD5Utils;

import org.json.JSONObject;

public class FastRegistration extends BaseActivity implements View.OnClickListener{

    private EditText etPhone;
    private EditText etPassword;
    private Button btRegister;
    private SharedPreferences mSharedPreference;
    private String phoneNumber;
    private String registerPwd;
    private String currentTime;
    private String md5Password;
    private Button btnRegBack;
    private TextView tvClose;
    private WebSoketService myService;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //返回一个MsgService对象
            myService = ((WebSoketService.MyBinder) service).getService();
            System.out.println("绑定服务成功");
            //注册回调接口来接收变化

            myService.setOnConnectListener(new OnConnectListener() {

                @Override
                public void onJSonObject(JSONObject json) {
                    try {
                        if (json.getInt("msg") == 11) {
                            parseRegJSONObject(json);
                        }
                    } catch (Exception e) {
                    }
                }

                public void onError(Exception msg) {

                    System.out.println(msg);
                }

                public void onConnected(String notice) {

                }

                @Override
                public void onOrderPush(String json) {

                }

            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_registration);
        Intent intent = new Intent(this, WebSoketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        currentTime = GetTime.getCurrentTime();
        initView();

    }

    private void initView() {
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_pwd);
        btRegister = (Button) findViewById(R.id.bt_register);
        btnRegBack = (Button) findViewById(R.id.btRegBack);
        tvClose = (TextView) findViewById(R.id.tv_close);

        btRegister.setOnClickListener(this);
        btnRegBack.setOnClickListener(this);
        tvClose.setOnClickListener(this);
    }

    /**
     * 请求服务器注册账号
     */

    private void registerAccount() {
        mSharedPreference = getSharedPreferences("count", MODE_PRIVATE);
        String lat = mSharedPreference.getString("lat", "");
        String lng = mSharedPreference.getString("lng", "");
        try {
            JSONObject jsonGeo = new JSONObject();
            jsonGeo.put("lat", lat);
            jsonGeo.put("lng", lng);
            Log.d("json经纬度", jsonGeo.toString());
            JSONObject jsonObject = new JSONObject(jsonGeo.toString());

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("upn", phoneNumber);
            jsonBody.put("pwd", md5Password);
            jsonBody.put("ts", currentTime);
            jsonBody.put("geo", jsonObject);

            JSONObject jsonReq = new JSONObject();
            jsonReq.put("action", 61);
            jsonReq.put("msg", 11);
            jsonReq.put("body", jsonBody);

            Log.d("注册",jsonReq.toString());

            WebSoketService.mConnection.sendTextMessage(jsonReq.toString());

        } catch (Exception e) {
        }
    }

    /**
     * 解析注册时返回的json
     *
     * @param json 服务器返回的json
     */

    private void parseRegJSONObject(JSONObject json) {
        try {
            JSONObject body = json.getJSONObject("body");
            Log.d("解析后的body数据", body + "");
//            String upn = body.getString("upn");
//            Log.d("解析后的upn数据", upn);
//            String pwd = body.getString("pwd");
//            Log.d("解析后的pwd数据", pwd);
//            String geo = body.getString("geo");
//            Log.d("解析后的geo数据", geo);
            int status = body.getInt("status");
            Log.d("解析后的status数据", status + "");
            final String stastr = body.getString("stastr");
            Log.d("解析后的stastr数据", stastr);
//            String name = body.getString("name");
//            Log.d("解析后的name数据", name);
//            String auid = body.getString("auid");
//            Log.d("auid", auid);
//            ast = body.getInt("ast");
//            Log.d("解析后的ast数据", ast + "");
//            String fub = body.getString("fub");
//            Log.d("解析后的fub数据", fub);
//            String ts = body.getString("ts");
//            Log.d("解析后的tm数据", ts);
            if (status == 1) {
                System.out.println("finally" + status);
                finish();
            } else {
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    public void run(){

                        Toast.makeText(getApplicationContext(), stastr, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                phoneNumber = etPhone.getText().toString().trim();
                registerPwd = etPassword.getText().toString().trim();
                md5Password = MD5Utils.encode(registerPwd);
                registerAccount();
                break;
            case R.id.btRegBack:
                finish();
                break;
            case R.id.tv_close:
                finish();
                break;

            default:
                break;
        }
    }
}