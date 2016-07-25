package com.bluedatax.wdpms.activity;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bluedatax.wdpms.R;
import com.bluedatax.wdpms.adapter.TabAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class OrderDetail extends FragmentActivity {

    private TabPageIndicator mIndicator;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        mIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
        mViewPager = (ViewPager) findViewById(R.id.id_pager);
        mAapter = new TabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAapter);
        mIndicator.setViewPager(mViewPager,0);
    }
}
