package com.bluedatax.wdpms.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluedatax.wdpms.R;

/**
 * Created by xuyuanqiang on 7/11/16.
 */
public class Set extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set,null);
        return view;
    }
}
