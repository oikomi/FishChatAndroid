package com.yuchuan.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yuchuan.protocol.Cmd;

import java.util.ArrayList;

import yuchuan.com.fishchat.R;

public class ChatMainTabFragment extends Fragment {
    private ListView msgList = null;
    private MsgResReceiver receiver;
    ArrayList<MsgCellData> data;
    MsgCellAdapter adapter;
    int[] images = new int[] { R.drawable.ic_launcher };

    public interface OnMsgRespListener {
        public void onMsgResp(Cmd cmd);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.tab01, container, false);
        msgList = (ListView) view.findViewById(R.id.tab01ListView);

        data = new ArrayList<MsgCellData>();
        for (int i = 0; i < 1; i++) {
            MsgCellData itemBean = new MsgCellData(images[0], "可爱的聊天机器人",
                    "为主淫服务...lala...");
            data.add(itemBean);
        }

        adapter = new MsgCellAdapter(getActivity(),
                R.layout.activity_msg_list_cell, data);
        msgList.setAdapter(adapter);

        msgList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getActivity(), ChatActivity.class);

                //startActivity(intent);
            }
        });

        receiver = new MsgResReceiver();
        IntentFilter filter = new IntentFilter("com.yuchuan.services.MsgService");
        getActivity().registerReceiver(receiver, filter);

        return view;
	}

    public class MsgResReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MsgResReceiver", "onReceive");
            if (intent.getAction().equals("com.yuchuan.services.MsgService")) {
                Cmd cmd = (Cmd)intent.getSerializableExtra("cmd");
                Log.i("MsgResReceiver", cmd.toString());

                switch (cmd.getCmdName()) {
                    case Cmd.RESP_MESSAGE_P2P_CMD:
                        Log.i("MsgResReceiver", "add a talk");
                        adapter.addItem(data, cmd);
                        break;
                }
            }
        }
    }

}
