package com.bluedatax.wdpms;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bluedatax.wdpms.utils.ActivityCollector;

/**
 * Created by xuyuanqiang on 7/15/16.
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
