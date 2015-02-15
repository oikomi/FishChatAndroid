package com.yuchuan.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuchuan.protocol.Cmd;

import java.util.ArrayList;

import yuchuan.com.fishchat.R;

public class MsgCellAdapter extends BaseAdapter {
    private int[] colors = new int[] { 0xff3cb371, 0xffa0a0a0 };
    private Context mContext;
    private int resource;
    private LayoutInflater mInflater;
    ArrayList<MsgCellData> data;

    public MsgCellAdapter(Context context, int resource, ArrayList<MsgCellData> data) {
        this.mContext = context;
        this.resource = resource;
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(resource, null);//根据布局文件实例化view
        TextView title = (TextView) convertView.findViewById(R.id.ItemTitle);//找某个控件
        title.setText(data.get(position).getName());//给该控件设置数据(数据从集合类中来)
        TextView time = (TextView) convertView.findViewById(R.id.ItemText);//找某个控件
        time.setText(data.get(position).getMsg());//给该控件设置数据(数据从集合类中来)
        ImageView img = (ImageView) convertView.findViewById(R.id.ItemImage);
        img.setImageResource(R.drawable.touxiang);

        return convertView;
    }

    public void addItem(ArrayList<MsgCellData> data, Cmd cmd){
        data.add(new MsgCellData(R.drawable.touxiang, (String)cmd.getArg(1),
                (String)cmd.getArg(0)));

        notifyDataSetChanged();
    }
}