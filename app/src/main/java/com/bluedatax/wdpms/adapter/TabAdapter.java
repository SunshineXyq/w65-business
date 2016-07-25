package com.bluedatax.wdpms.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bluedatax.wdpms.fragment.OrderDetailFragment;

/**
 * Created by xuyuanqiang on 7/19/16.
 */
public class TabAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = new String[] {"订单状态","订单详情"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        OrderDetailFragment fragment = new OrderDetailFragment(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TITLES[position % TITLES.length];
    }

}
