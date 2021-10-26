package com.okuloj.nhacchill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends BaseFragment{

    private HomeFragment() { }

    private TextView textViewTitle;
    private ImageView imageViewPauseOrResume;
    private ImageView imageViewPre;
    private ImageView imageViewNext;
    private ImageView imageViewClear;
    private LinearLayout layoutBottom;
    private RecyclerView recyclerViewListSong;
    private HomeAdapter homeAdapter;
    private OnItemClickListener onItemClickListener;

    private boolean isPlaying;
    private Song mSong;
    private Context context;
    private boolean serviceIsStart = false;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE_SEND_DATA_TO_HOME);
            if (bundle != null) {
                mSong = (Song) bundle.get(Constants.BUNDLE_SONG);
                isPlaying = bundle.getBoolean(Constants.BUNDLE_STATUS);
                int action = bundle.getInt(Constants.BUNDLE_ACTION_MUSIC);
                handleLayoutMusic(action);
            }

            int action = intent.getIntExtra(Constants.EXTRA_ACTION_MUSIC, -2);
            if (action != -2) {
                handleLayoutMusic(action);
            }
        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        context = view.getContext();
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_SEND_DATA_TO_HOME));
        sendActionToService(Constants.ACTION_CHECK_SERVICE_IS_START);
        if (!serviceIsStart) {
            startService();
        }
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_SEND_DATA_TO_HOME));

        textViewTitle = view.findViewById(R.id.textViewTitle);
        imageViewPauseOrResume = view.findViewById(R.id.imageViewPauseOrResume);
        imageViewPre = view.findViewById(R.id.imageViewPre);
        imageViewNext = view.findViewById(R.id.imageViewNext);
        imageViewClear = view.findViewById(R.id.imageViewClear);
        layoutBottom = view.findViewById(R.id.layoutBottom);
        recyclerViewListSong = view.findViewById(R.id.recyclerViewListSong);

        layoutBottom.setOnClickListener(v -> {
            replaceFragment(DetailFragment.newInstance());
            sendActionToService(Constants.ACTION_DETAIL_SCREEN_ON_START);
        });

        onItemClickListener = position -> {
            sendActionToService(Constants.ACTION_CHECK_SERVICE_IS_START);
            if (!serviceIsStart) {
                startService();
            }
            sendSongPositionToService(position);
        };

        homeAdapter = new HomeAdapter(onItemClickListener);
        recyclerViewListSong.setAdapter(homeAdapter);

        imageViewPre.setOnClickListener(v -> {
            sendActionToService(Constants.ACTION_PREV);
        });

        imageViewPauseOrResume.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(Constants.ACTION_PAUSE);
            } else {
                sendActionToService(Constants.ACTION_RESUME);
            }
        });

        imageViewNext.setOnClickListener(v -> {
            sendActionToService(Constants.ACTION_NEXT);
        });

        imageViewClear.setOnClickListener(v -> {
            sendActionToService(Constants.ACTION_CLEAR);
            serviceIsStart = false;
        });
    }

    @Override
    protected void initData() {
        DataBase dataBase = new DataBase();
        homeAdapter.updateData(dataBase.getData());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    private void startService() {
        Intent intentService = new Intent(context, MediaService.class);
        intentService.putExtra(Constants.EXTRA_START_MUSIC, Constants.ACTION_START_SERVICE);
        context.startService(intentService);
    }

    private void handleLayoutMusic(int action) {
        switch (action) {
            case Constants.ACTION_START_SONG:
                layoutBottom.setVisibility(View.VISIBLE);
                showInforSong();
                isPlaying = true;
                setStatusButtonPlayOrPause();
                break;
            case Constants.ACTION_PAUSE:
                isPlaying = false;
                setStatusButtonPlayOrPause();
                break;
            case Constants.ACTION_RESUME:
                isPlaying = true;
                setStatusButtonPlayOrPause();
                break;
            case Constants.ACTION_CLEAR:
                layoutBottom.setVisibility(View.GONE);
                serviceIsStart = false;
                break;
            case Constants.ACTION_CHECK_SERVICE_IS_START:
                serviceIsStart = true;
                break;
        }
    }

    private void showInforSong() {
        if (mSong == null) {
            return;
        }
        textViewTitle.setText(mSong.getTitle());
    }

    private void setStatusButtonPlayOrPause() {
        if (isPlaying) {
            imageViewPauseOrResume.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            imageViewPauseOrResume.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void sendActionToService(int action) {
        Intent intentService = new Intent(Constants.ACTION_SEND_ACTION_TO_SERVICE);
        intentService.putExtra(Constants.EXTRA_ACTION_MUSIC_TO_SERVICE, action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentService);
    }

    private void sendSongPositionToService(int position) {
        Intent intentService = new Intent(Constants.ACTION_SEND_ACTION_TO_SERVICE);
        intentService.putExtra(Constants.EXTRA_SONG_POSITION, position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentService);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
}
