package com.bluedatax.wdpms.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluedatax.wdpms.R;
import com.bluedatax.wdpms.adapter.TabAdapter;

/**
 * Created by xuyuanqiang on 7/19/16.
 */

@SuppressLint("ValidFragment")
public class OrderDetailFragment extends Fragment {
    private int newType;

    public OrderDetailFragment(int newType) {
        this.newType = newType;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail,null);
        TextView tip = (TextView) view.findViewById(R.id.id_tip);
        tip.setText(TabAdapter.TITLES[newType]);
        return view;
    }
}
