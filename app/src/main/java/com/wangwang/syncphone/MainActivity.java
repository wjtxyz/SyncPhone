package com.wangwang.syncphone;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.zeromq.ZBeacon;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mPeerListView;


    private final ZMQ.Context mZMQContext = ZMQ.context(1);

    private ZMQ.Socket mPeerDiscoverySocket = mZMQContext.socket(ZMQ.REQ);

    final byte[] DEFAULT_PREFIX = new byte[]{'s', 'y', 'n', 'c'};

    ZBeacon mZBeacon = new ZBeacon(7325, DEFAULT_PREFIX);

    List<InetAddress> mInetAddressList = new ArrayList<>();

    ArrayMap<InetAddress, byte[]> mInetAddressMap = new ArrayMap<>();

    BaseAdapter mPeerListViewAdapter = new PeerListViewAdaper(R.layout.peer_listview_item_template);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPeerListView = (ListView) findViewById(R.id.peer_list);

        mPeerListView.setAdapter(mPeerListViewAdapter);


        mZBeacon.setListener(new ZBeacon.Listener() {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                mInetAddressMap.put(sender, beacon);
            }
        });

        mZBeacon.setPrefix(DEFAULT_PREFIX);
        mZBeacon.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mZBeacon.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public class PeerListViewAdaper extends BaseAdapter {

        final int mResId;

        public PeerListViewAdaper(int resId) {
            mResId = resId;
        }

        @Override
        public int getCount() {
            return mInetAddressMap.size();
        }

        @Override
        public Object getItem(int position) {
            return mInetAddressMap.keyAt(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View v = (convertView != null) ? convertView : getLayoutInflater().inflate(mResId, parent, false);
            ((TextView) v.findViewById(R.id.peer_name)).setText(((InetAddress) getItem(position)).toString());

            return v;
        }
    }
}
