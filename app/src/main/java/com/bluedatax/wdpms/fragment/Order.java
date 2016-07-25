package com.bluedatax.wdpms.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bluedatax.wdpms.R;
import com.bluedatax.wdpms.activity.LoginActivity;
import com.bluedatax.wdpms.activity.OrderDetail;
import com.bluedatax.wdpms.adapter.LvAdapter;
import com.bluedatax.wdpms.service.MyService;
import com.bluedatax.wdpms.service.WebSoketService;
import com.bluedatax.wdpms.utils.GetTime;
import com.bluedatax.wdpms.view.MyListView;
import com.bluedatax.wdpms.view.RefreshableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xuyuanqiang on 7/11/16.
 */
public class Order extends Fragment {

    private ListView lvOrder;
    private ArrayList<String> listItem;
    private RefreshableView refreshableView;
    private List<HashMap<String, String>> list;
    private MyListView lv;
    private LvAdapter adapter;
    private final String ACTION_NAME = "查询账单";
    private String currentTime;
    private HashMap<String, String> map;
    private QueryOrderReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null);
        currentTime = GetTime.getCurrentTime();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NAME);
        receiver = new QueryOrderReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
        queryOrder();

        lv = (MyListView) view.findViewById(R.id.lv);
        list = new ArrayList<HashMap<String, String>>();

        lv.setonRefreshListener(new MyListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        adapter.notifyDataSetChanged();
                        lv.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String order = getActivity().getIntent().getStringExtra("order");
        if (order != null) {
            parseJSONObject(order);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("您点击了第",position+""+id);
                Intent intent = new Intent(getActivity(), OrderDetail.class);
                startActivity(intent);
            }
        });
    }

    private void parseJSONObject(String order) {
        try {
            JSONObject jsonObject = new JSONObject(order);
            JSONObject jsonBody = jsonObject.getJSONObject("body");
            Log.d("订单数组", jsonBody.toString());

            map = new HashMap<String, String>();
            String phone = jsonBody.getString("phone");
            map.put("phone", phone);
            String price = jsonBody.getString("price");
            map.put("price", price);
            String count = jsonBody.getString("count");
            map.put("count", count);
            String status = jsonBody.getString("status");
            map.put("status", status);
            String name = jsonBody.getString("name");
            map.put("name", name);
            String goods_name = jsonBody.getString("goods_name");
            map.put("goods_name", goods_name);
            String ads = jsonBody.getString("ads");
            map.put("ads", ads);
            String onum = jsonBody.getString("onum");
            map.put("onum", onum);
            list.add(map);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            adapter = new LvAdapter(list, getActivity());
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void queryOrder() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", 11);
            jsonObject.put("action", 64);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("upn", LoginActivity.usename);
            jsonBody.put("start_date", GetTime.getOtherDay());
            jsonBody.put("end_date", GetTime.getCurrentDay());
            jsonBody.put("ts", currentTime);
            jsonObject.put("body", jsonBody);
            WebSoketService.mConnection.sendTextMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class QueryOrderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String bill = intent.getStringExtra("bill");
            parseBillJSONObject(bill);
            Log.d("bill", bill);
        }
    }

    private void parseBillJSONObject(String bill) {
        try {
            JSONObject jsonObject = new JSONObject(bill);
            JSONArray jsonArrayBill = jsonObject.getJSONObject("body").getJSONArray("order");
            Log.d("订单数组", jsonArrayBill.toString());
            for (int i = 0; i < jsonArrayBill.length(); i++) {
                map = new HashMap<String, String>();
                JSONObject jsonOrder = jsonArrayBill.getJSONObject(i);
                String phone = jsonOrder.getString("phone");
                map.put("phone", phone);
                String price = jsonOrder.getString("price");
                map.put("price", price);
                String count = jsonOrder.getString("count");
                map.put("count", count);
                String status = jsonOrder.getString("status");
                map.put("status", status);
                String name = jsonOrder.getString("name");
                map.put("name", name);
                String goods_name = jsonOrder.getString("goods_name");
                map.put("goods_name", goods_name);
                String ads = jsonOrder.getString("ads");
                map.put("ads", ads);
                String onum = jsonOrder.getString("onum");
                map.put("onum", onum);
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            adapter = new LvAdapter(list, getActivity());
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}