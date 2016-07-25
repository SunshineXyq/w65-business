package com.bluedatax.wdpms.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
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
import com.bluedatax.wdpms.utils.ActivityCollector;
import com.bluedatax.wdpms.utils.GetAppVersion;
import com.bluedatax.wdpms.utils.GetTime;
import com.bluedatax.wdpms.utils.MD5Utils;

import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LocationListener {

    private Button btLogin;
    private TextView tvRegister;
    private TextView tvTemporaryPsw;
    private TextView tvForgetPsw;
    private WebSoketService myService;
    private EditText etUsename;
    private EditText etPassword;

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
            startService(in);
            myService.setOnConnectListener(new OnConnectListener() {

                @Override
                public void onJSonObject(JSONObject json) {
                    try {
                        if (json.getInt("action") == 60) {
                            parseInitJSONObject(json);
                        } else if (json.getInt("action") == 62) {
                            parseLoginJSONObject(json);
                        }
                    } catch (Exception e) {
                    }
                }

                public void onError(Exception msg) {

                    System.out.println(msg);
                }

                public void onConnected(String notice) {
                    Log.d("传递过来的notice", notice);
                    System.out.println(notice);
                    if (notice.equals("Connected")) {
                        initServer();
                    } else if (notice.equals("")) {
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onOrderPush(String json) {
                }

            });
        }
    };
    private Intent in;
    private String CurrentTime;
    private TelephonyManager tm;
    private String name;
    private String sver;
    private String model;
    private String aver;
    private String DEVICE_ID;
    private StringBuffer sb;
    private SharedPreferences mSharedPreference;
    private LocationManager mLocationManager;
    private String geo;
    private String lat;
    private String lng;
    private SharedPreferences.Editor mEditor;
    private String LocationData;
    private String duid;
    public static String usename;
    private String password;
    private String md5Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getPostion();

        CurrentTime = GetTime.getCurrentTime();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        name = android.os.Build.MANUFACTURER;          //name
        Log.d("loginActivity", name);
        sver = android.os.Build.VERSION.RELEASE;       //sver
        Log.d("loginActivity", sver);
        model = Build.MODEL;                           //device model 型号
        Log.d("loginActivity", model);
        GetAppVersion mGetAppVersion = new GetAppVersion(LoginActivity.this);
        aver = mGetAppVersion.getVersion();            //aver
        Log.d("loginActivity", aver);
        DEVICE_ID = tm.getDeviceId();
        Log.d("loginActivity", DEVICE_ID);
        String MD5DeviceID = MD5Utils.encode(DEVICE_ID);
        Log.d("MD5设备号码", MD5DeviceID);
        sb = new StringBuffer(MD5DeviceID);
        sb.insert(6, "-");
        sb.insert(11, "-");
        sb.insert(16, "-");
        sb.insert(21, "-");
        duid = sb.toString();
        Log.d("MD5设备号码", sb.toString());
        in = new Intent(LoginActivity.this, WebSoketService.class);
        bindService(in, conn, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        btLogin = (Button) findViewById(R.id.bt_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvTemporaryPsw = (TextView) findViewById(R.id.tv_temporary_psw);
        tvForgetPsw = (TextView) findViewById(R.id.tv_forget_psw);
        etUsename = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);

        btLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvTemporaryPsw.setOnClickListener(this);
        tvForgetPsw.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录
            case R.id.bt_login:
                usename = etUsename.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                md5Password = MD5Utils.encode(password);
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
                requestLogin();
                break;
            //注册
            case R.id.tv_register:
                Intent intentReg = new Intent(LoginActivity.this, FastRegistration.class);
                startActivity(intentReg);
                break;
            //临时密码
            case R.id.tv_temporary_psw:
                break;
            //忘记密码
            case R.id.tv_forget_psw:
                break;
        }
    }

    /**
     * 请求登录发送数据
     */

    private void requestLogin() {
        String currentTime = GetTime.getCurrentTime();
        try {
            JSONObject jsonGeo = new JSONObject();
            jsonGeo.put("lat", lat);
            jsonGeo.put("lng", lng);
            Log.d("json经纬度", jsonGeo.toString());
            JSONObject jsonObject = new JSONObject(jsonGeo.toString());

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("upn", usename);
            jsonBody.put("pwd", md5Password);
            jsonBody.put("ts", currentTime);
            jsonBody.put("geo", jsonObject);

            JSONObject jsonReq = new JSONObject();
            jsonReq.put("action", 62);
            jsonReq.put("msg", 11);
            jsonReq.put("body", jsonBody);

            Log.d("登录",jsonReq.toString());

            WebSoketService.mConnection.sendTextMessage(jsonReq.toString());

        } catch (Exception e) {
        }
    }

    /**
     * 解析初始化返回的JSON
     *
     * @param json 初始化时返回的json
     */
    private void parseInitJSONObject(JSONObject json) {
        try {
//            initToken = json.getLong("token");
//            Log.d("初始化的token", initToken + "");
            JSONObject body = json.getJSONObject("body");
            Log.d("解析后的body数据", body + "");
//            String htp = body.getString("http");
//            Log.d("解析后的htp数据", htp + "");
//            String spn = body.getString("spn");
//            Log.d("解析后的spn数据", spn + "");
            String laver = body.getString("laver");
            Log.d("初始化的laver", laver);
//            String upgurl = body.getString("upgurl");
//            Log.d("解析后的upgurl数据", upgurl + "");
//            String appurl = body.getString("appurl");
//            Log.d("解析后的appurl数据", appurl + "");
//            String sturl = body.getString("sturl");
//            Log.d("解析后的sturl数据", sturl + "");
            String ts = body.getString("ts");
            Log.d("初始化的ts",ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析登录返回的JSON
     *
     * @param json 登录返回的json串
     */

    private void parseLoginJSONObject(JSONObject json) {
        try {
//            loginToken = json.getLong("token");
//            Log.d("解析登录返回的token数据",loginToken+"");
            JSONObject body = json.getJSONObject("body");
            Log.d("解析后的body数据", body + "");
//            int id = body.getInt("id");
//            Log.d("id",id+"");
//            String laver = body.getString("laver");
//            Log.d("解析后的laver数据", laver);
            int status = body.getInt("status");
            Log.d("status", status + "");
            final String stastr = body.getString("stastr");
            Log.d("解析后的stastr数据", stastr);
//            String fub = body.getString("fub");
//            Log.d("解析后的fub数据", fub);
//            String ts = body.getString("ts");
//            Log.d("解析后的ts数据", ts);
            if (status == 1) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Handler handler = new Handler(getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,stastr,Toast.LENGTH_LONG).show();
                    }
                });
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        SharedPrefsUtil.putLong(mContext, "loginToken",loginToken);
    }

    /**
     * 初始化发送的数据
     */

    private void initServer() {
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
            jsonBody.put("duid", duid);
            jsonBody.put("name", name);
            jsonBody.put("model", model);
            jsonBody.put("sver", sver);
            jsonBody.put("aver", aver);
            jsonBody.put("type", "1");
            jsonBody.put("ts", CurrentTime);
            jsonBody.put("geo", jsonObject);

            JSONObject jsonReq = new JSONObject();
            jsonReq.put("msg", 10);
            jsonReq.put("action",60);
            jsonReq.put("body", jsonBody);

            Log.d("初始化",jsonReq.toString());

            WebSoketService.mConnection.sendTextMessage(jsonReq.toString());

        } catch (Exception e) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ActivityCollector.finishAll();
            WebSoketService.mConnection.disconnect();
        }
        return false;
    }

    private void getPostion() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);      //获取系统定位管理器
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationData = "Enabled:" + LocationManager.GPS_PROVIDER;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Log.i("123", "location:" + location.toString());
        geo = location.getLatitude() + "#" + location.getLongitude();
        lat = location.getLatitude() + "";
        lng = location.getLongitude() + "";

        SharedPreferences mShare1 = getSharedPreferences("count", MODE_PRIVATE);
        mEditor = mShare1.edit();
        mEditor.putString("lat", lat);
        mEditor.putString("lng", lng);
        Log.d("存入本地的经纬度", lat + "#" + lng);
        mEditor.commit();

        Log.d("获取手机经纬度", geo);
        Log.d("lat", lat);
        Log.d("lng", lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        String strText = String.format("provider is %s, status = %d", provider, status);
        Log.i("", strText);

        LocationData = strText;
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Log.i("", provider);

        LocationData = "Enabled:" + provider;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Log.i("", provider);

        LocationData = "Disabled:" + provider;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(in);
        unbindService(conn);
//        MyService.client.disconnect();
    }

}
