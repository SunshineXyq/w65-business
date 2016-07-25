package com.bluedatax.wdpms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bluedatax.wdpms.fragment.Homepage;
import com.bluedatax.wdpms.fragment.Order;
import com.bluedatax.wdpms.fragment.Set;
import com.bluedatax.wdpms.service.MyService;
import com.bluedatax.wdpms.service.OnConnectListener;
import com.bluedatax.wdpms.service.WebSoketService;
import com.bluedatax.wdpms.utils.ActivityCollector;

import org.json.JSONObject;


public class MainActivity extends BaseActivity implements View.OnClickListener{

    private RadioButton rbHomepage;
    private RadioButton rbOrder;
    private RadioButton rbSet;
    private FragmentManager fragmentManager;
    private RadioGroup mRadioGroup;
    private Homepage homepage;
    private Order order;
    private Set set;
    private WebSoketService mService;


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mService = ((WebSoketService.MyBinder) service).getService();
            mService.setOnConnectListener(new OnConnectListener() {

                @Override
                public void onJSonObject(JSONObject json) {
                }
                @Override
                public void onError(Exception msg) {
                }
                @Override
                public void onConnected(String notice) {
                }
                @Override
                public void onOrderPush(String json) {
                    Log.d("MainActivity",json);
                    startNotification(json);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * 开启消息通知
     */
    @SuppressLint("NewApi")
    private void startNotification(String bill) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(MainActivity.this,Order.class);
        intent.putExtra("order",bill);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notify = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("您有新的订单,请注意查收")
                .setContentTitle("订单")
                .setContentInfo("李奶奶订单")
                .setContentText("点击确认")
                .setContentIntent(pendingIntent).build();
        notify.defaults = Notification.DEFAULT_VIBRATE;      //通知发出振动
        notify.defaults = Notification.DEFAULT_SOUND;        //通知发出默认声音
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(0,notify);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
        Intent intent = new Intent(MainActivity.this, WebSoketService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        rbHomepage = (RadioButton) findViewById(R.id.rb_homepage);
        rbOrder = (RadioButton) findViewById(R.id.rb_order);
        rbSet = (RadioButton) findViewById(R.id.rb_set);

        rbHomepage.setOnClickListener(this);
        rbOrder.setOnClickListener(this);
        rbSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_homepage:
                setTabSelection(0);
                break;
            case R.id.rb_order:
                setTabSelection(1);
                break;
            case R.id.rb_set:
                setTabSelection(2);
                break;

            default:
                break;
        }
    }

    private void setTabSelection(int index) {
        //重置按钮
        resetBtn();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                Drawable homepagePress = getResources().getDrawable(R.mipmap.homepage_press);
                homepagePress.setBounds(0,0,50,50);
                rbHomepage.setCompoundDrawables(null,homepagePress,null,null);
                if (homepage == null) {
                    homepage = new Homepage();
                    transaction.add(R.id.framelayout_main,homepage);
                    mRadioGroup.check(R.id.rb_homepage);
                    System.out.println("显示首页");
                } else {
                    transaction.show(homepage);
                }
                break;
            case 1:
                Drawable orderPress = getResources().getDrawable(R.mipmap.order_press);
                orderPress.setBounds(0,0,50,50);
                rbOrder.setCompoundDrawables(null,orderPress,null,null);
                if (order == null) {
                    order = new Order();
                    transaction.add(R.id.framelayout_main,order);
                } else {
                    transaction.show(order);
                }
                break;
            case 2:
                Drawable setPress = getResources().getDrawable(R.mipmap.mine_press);
                setPress.setBounds(0,0,50,50);
                rbSet.setCompoundDrawables(null,setPress,null,null);
                if (set == null) {
                    set = new Set();
                    transaction.add(R.id.framelayout_main,set);
                } else {
                    transaction.show(set);
                }
                break;
          default:
              break;
        }
        transaction.commit();
    }

    private void resetBtn() {
        Drawable homePage = getResources().getDrawable(R.mipmap.homepage_normal);
        Drawable order = getResources().getDrawable(R.mipmap.order_normal);
        Drawable set = getResources().getDrawable(R.mipmap.mine_normal);
        homePage.setBounds(0,0,50,50);
        order.setBounds(0,0,50,50);
        set.setBounds(0, 0, 50, 50);
        rbHomepage.setCompoundDrawables(null,homePage,null,null);
        rbOrder.setCompoundDrawables(null,order,null,null);
        rbSet.setCompoundDrawables(null,set,null,null);


    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homepage != null) {
            transaction.hide(homepage);
        }
        if (order != null) {
            transaction.hide(order);
        }
        if (set != null) {
            transaction.hide(set);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            //需要处理
//            Login.mTimer.cancel();
            WebSoketService.mConnection.disconnect();
            ActivityCollector.finishAll();
            unbindService(conn);
        }
        return false;

    }
}
