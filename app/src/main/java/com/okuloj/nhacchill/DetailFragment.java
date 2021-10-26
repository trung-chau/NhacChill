package com.okuloj.nhacchill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;

public class DetailFragment extends BaseFragment {

    private DetailFragment() {
    }

    private TextView textViewDetailTitle;
    private ImageView imageViewCdPlayer;
    private TextView textViewTimeSong;
    private SeekBar seekBarSong;
    private TextView textViewTimeTotal;
    private ImageView imageViewContainPre;
    private ImageView imageViewContainPause;
    private ImageView imageViewContainNext;
    private ImageView imageViewDetailPauseOrResume;
    private Context context;
    private Boolean isPlaying;

    private final BroadcastReceiver detailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE_SEND_DATA_TO_DETAIL);
            if (bundle != null) {
                String title = bundle.getString(Constants.BUNDLE_TITLE_SONG);
                isPlaying = bundle.getBoolean(Constants.BUNDLE_STATUS);
                int duration = bundle.getInt(Constants.BUNDLE_DURATION_SONG);
                setDetailView(title, isPlaying, duration);
            }

            int currentPosition = intent.getIntExtra(Constants.EXTRA_CURRENT_POSITION_TO_DETAIL, -1);
            if (currentPosition != -1) {
                setProgressSeekBar(currentPosition);
            }

            int action = intent.getIntExtra(Constants.EXTRA_ACTION_MUSIC, -2);
            if (action != -2) {
                handleLayoutDetail(action);
            }
        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_detail;
    }

    @Override
    protected void initView(View view) {
        context = view.getContext();
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(detailReceiver, new IntentFilter(Constants.ACTION_SEND_DATA_TO_DETAIL));

        textViewDetailTitle = view.findViewById(R.id.textViewDetailTitle);
        imageViewCdPlayer = view.findViewById(R.id.imageViewCdPlayer);
        textViewTimeSong = view.findViewById(R.id.textViewTimeSong);
        seekBarSong = view.findViewById(R.id.seekBarSong);
        textViewTimeTotal = view.findViewById(R.id.textViewTimeTotal);
        imageViewContainPre = view.findViewById(R.id.imageViewContainPre);
        imageViewContainPause = view.findViewById(R.id.imageViewContainPause);
        imageViewContainNext = view.findViewById(R.id.imageViewContainNext);
        imageViewDetailPauseOrResume = view.findViewById(R.id.imageViewDetailPauseOrResume);

        imageViewContainPre.setOnClickListener(v -> {
            sendActionToService(Constants.ACTION_PREV);
        });

        imageViewContainPause.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(Constants.ACTION_PAUSE);
            } else {
                sendActionToService(Constants.ACTION_RESUME);
            }
        });

        imageViewContainNext.setOnClickListener(v -> {
            sendActionToService(Constants.ACTION_NEXT);
        });

        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentPosition = seekBar.getProgress();

                Intent intentService = new Intent(Constants.ACTION_SEND_ACTION_TO_SERVICE);
                intentService.putExtra(Constants.EXTRA_CURRENT_POSITION_TO_SERVICE, currentPosition);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentService);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onStop() {
        super.onStop();
        sendActionToService(Constants.ACTION_DETAIL_SCREEN_ON_STOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(detailReceiver);
    }

    private void handleLayoutDetail(int action) {
        switch (action) {
            case Constants.ACTION_RESUME:
                isPlaying = true;
                setStatusButtonPlayOrPause(true);
                break;
            case Constants.ACTION_PAUSE:
                isPlaying = false;
                setStatusButtonPlayOrPause(false);
                break;
            case Constants.ACTION_CLEAR:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }


    private void setProgressSeekBar(int currentPosition) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        textViewTimeSong.setText(simpleDateFormat.format(currentPosition));
        seekBarSong.setProgress(currentPosition);
    }

    private void setDetailView(String title, Boolean isPlaying, int duration) {
        textViewDetailTitle.setText(title);
        seekBarSong.setMax(duration);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        textViewTimeTotal.setText(simpleDateFormat.format(duration));

        if (isPlaying) {
            imageViewDetailPauseOrResume.setImageResource(R.drawable.ic_baseline_pause_24);
        }
    }

    private void setStatusButtonPlayOrPause(boolean status) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.disc_rotate);
        if (status) {
            imageViewDetailPauseOrResume.setImageResource(R.drawable.ic_baseline_pause_24);
            imageViewCdPlayer.setAnimation(animation);
        } else {
            imageViewDetailPauseOrResume.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            imageViewCdPlayer.clearAnimation();
        }
    }

    private void sendActionToService(int action) {
        Intent intentService = new Intent(Constants.ACTION_SEND_ACTION_TO_SERVICE);
        intentService.putExtra(Constants.EXTRA_ACTION_MUSIC_TO_SERVICE, action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentService);
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }
}
