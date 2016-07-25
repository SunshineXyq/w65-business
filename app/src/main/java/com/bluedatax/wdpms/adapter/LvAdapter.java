package com.bluedatax.wdpms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bluedatax.wdpms.R;
import com.bluedatax.wdpms.service.MyService;
import com.bluedatax.wdpms.service.WebSoketService;
import com.bluedatax.wdpms.utils.GetTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xuyuanqiang on 7/13/16.
 */
public class LvAdapter extends BaseAdapter {
    private List<HashMap<String, String>> list;
    private Context context;


    public LvAdapter(List<HashMap<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_order_item, null);
            holder = new ViewHolder();
            holder.tvFoodName = (TextView) convertView.findViewById(R.id.tv_food_name);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tvBuyer = (TextView) convertView.findViewById(R.id.tv_buyer);
            holder.tvOrderNumber = (TextView) convertView.findViewById(R.id.tv_order_number);
            holder.btConfirm = (Button) convertView.findViewById(R.id.order_status);
            holder.btRefuse = (Button) convertView.findViewById(R.id.bt_refuse_order);
            holder.btCancel = (Button) convertView.findViewById(R.id.bt_cancel_order);
            convertView.setTag(holder);                          //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();          //取出ViewHolder对象
            holder.tvFoodName.setText(list.get(position).get("goods_name"));
            holder.tvPrice.setText(list.get(position).get("price"));
            holder.tvCount.setText(list.get(position).get("count"));
            holder.tvBuyer.setText(list.get(position).get("name"));
            holder.tvOrderNumber.setText(list.get(position).get("onum"));
            holder.btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.btConfirm.getText().equals("确认订单")) {
                        sendOrderStatus(3);
                        holder.btConfirm.setText("配送中");
                    } else if (holder.btConfirm.getText().equals("配送中")) {
                        sendOrderStatus(4);
                        holder.btConfirm.setText("确认配送成功");
                    } else if (holder.btConfirm.getText().equals("确认配送成功")) {
                        sendOrderStatus(5);
                        holder.btConfirm.setText("套餐已送达");
                    }
                }
            });

            holder.btRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendOrderStatus(6);
                }
            });
            holder.btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendOrderStatus(7);
                }
            });
        }

        return convertView;
    }

    private void sendOrderStatus(int status) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", 11);
            jsonObject.put("action", 63);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("udn", "00124b0007ac1606");
            jsonBody.put("onum", "124334");
            jsonBody.put("name", "李奶奶");
            jsonBody.put("phone", "13600001234");
            jsonBody.put("ads", "北京市");
            jsonBody.put("goods_name", "大茄子");
            jsonBody.put("price", "8.8");
            jsonBody.put("count", "2");
            jsonBody.put("status", status);
            jsonBody.put("ts", GetTime.getCurrentTime());
            jsonObject.put("body", jsonBody);

            Log.d("订单状态",jsonObject.toString());

            WebSoketService.mConnection.sendTextMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        private TextView tvFoodName;
        private TextView tvPrice;
        private TextView tvCount;
        private TextView tvBuyer;
        private TextView tvOrderNumber;
        private Button btConfirm;
        private Button btRefuse;
        private Button btCancel;
    }

}
