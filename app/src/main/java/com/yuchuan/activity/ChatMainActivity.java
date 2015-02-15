package com.yuchuan.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.yuchuan.protocol.Cmd;
import com.yuchuan.services.MsgService;

import java.util.ArrayList;
import java.util.List;

import yuchuan.com.fishchat.R;

public class ChatMainActivity extends FragmentActivity implements ChatMainTabFragment.OnMsgRespListener {
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;

    private BadgeView mBadgeView;

    ViewPager mViewPager;
    TextView mChatTextView;
    TextView mFriendTextView;
    TextView mContactTextView;
    TextView mMeTextView;
    LinearLayout mChatLinearLayout;


    ChatMainTabFragment tab01 = new ChatMainTabFragment();
    FriendMainTabFragment tab02 = new FriendMainTabFragment();
    ContactMainTabFragment tab03 = new ContactMainTabFragment();
    MeMainTabFragment tab04 = new MeMainTabFragment();

	private ImageView mTabline;
	private int mScreen1_4;

	private int mCurrentPageIndex;

    boolean mBound = false;
    MsgService mService;
    //private MsgResReceiver receiver;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("Msg", "onServiceConnected");
            MsgService.LocalBinder binder = (MsgService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            final Intent oldIntent = getIntent();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String msgServerAddr = oldIntent.getStringExtra(Cmd.SELECT_MSG_SERVER_FOR_CLIENT_CMD);
                    Log.i("ChatMainActivity ", msgServerAddr);
                    String[] tmp = msgServerAddr.split(":");
                    mService.doProcess(tmp[0], Integer.parseInt(tmp[1]));
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initTabLine();
		initView();

        Intent intent = new Intent(ChatMainActivity.this, MsgService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

    @Override
    public void onMsgResp(Cmd cmd) {

    }

    @Override
    protected void onStart() {
        Log.i("Msg", "onStart");
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void initTabLine()
	{
		mTabline = (ImageView) findViewById(R.id.id_iv_tabline);
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		mScreen1_4 = outMetrics.widthPixels / 4;
		LayoutParams lp = mTabline.getLayoutParams();
		lp.width = mScreen1_4;
		mTabline.setLayoutParams(lp);
	}

	private void initView()
	{
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mChatTextView = (TextView) findViewById(R.id.id_tv_chat);
        mFriendTextView = (TextView) findViewById(R.id.id_tv_friend);
        mContactTextView = (TextView) findViewById(R.id.id_tv_contact);
        mMeTextView = (TextView) findViewById(R.id.id_tv_me);
        mChatLinearLayout = (LinearLayout) findViewById(R.id.id_ll_chat);
        mDatas = new ArrayList<Fragment>();

		mDatas.add(tab01);
		mDatas.add(tab02);
		mDatas.add(tab03);
        mDatas.add(tab04);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return mDatas.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return mDatas.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				resetTextView();
				switch (position)
				{
				case 0:
					if (mBadgeView != null)
					{
						mChatLinearLayout.removeView(mBadgeView);
					}
					mBadgeView = new BadgeView(ChatMainActivity.this);
					mBadgeView.setBadgeCount(7);
					mChatLinearLayout.addView(mBadgeView);

					mChatTextView.setTextColor(Color.parseColor("#008000"));
					break;
				case 1:
					mContactTextView.setTextColor(Color.parseColor("#008000"));
					break;
                case 2:
                    mFriendTextView.setTextColor(Color.parseColor("#008000"));
                    break;
                case 3:
                    mMeTextView.setTextColor(Color.parseColor("#008000"));
                    break;
				}

				mCurrentPageIndex = position;

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPx)
			{
				Log.e("TAG", position + " , " + positionOffset + " , "
						+ positionOffsetPx);

				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabline
						.getLayoutParams();

				if (mCurrentPageIndex == 0 && position == 0)// 0->1
				{
					lp.leftMargin = (int) (positionOffset * mScreen1_4 + mCurrentPageIndex
							* mScreen1_4);
				} else if (mCurrentPageIndex == 1 && position == 0)// 1->0
				{
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + (positionOffset - 1)
							* mScreen1_4);
				} else if (mCurrentPageIndex == 1 && position == 1) // 1->2
				{
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + positionOffset
							* mScreen1_4);
				} else if (mCurrentPageIndex == 2 && position == 1) // 2->1
				{
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + ( positionOffset-1)
							* mScreen1_4);
				} else if (mCurrentPageIndex == 2 && position == 2) // 2->3
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + ( positionOffset)
                            * mScreen1_4);
                } else if (mCurrentPageIndex == 3 && position == 1) // 3->2
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + ( positionOffset-1)
                            * mScreen1_4);
                }

                mTabline.setLayoutParams(lp);

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{


			}
		});

	}

	protected void resetTextView()
	{
		mChatTextView.setTextColor(Color.BLACK);
		mFriendTextView.setTextColor(Color.BLACK);
		mContactTextView.setTextColor(Color.BLACK);
        mMeTextView.setTextColor(Color.BLACK);
	}

}
