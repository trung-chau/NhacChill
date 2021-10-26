package com.okuloj.nhacchill;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends Service {

    private MediaPlayer mediaPlayer;

    private Song mSong;
    private int MEDIA_POSITION = 0;
    private boolean isDetailScreenOnResume = false;
    private boolean isPlaying = false;
    private Handler handler;
    private final List<Song> songs = new ArrayList<>();

    private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(Constants.EXTRA_ACTION_MUSIC_TO_SERVICE, -2);
            handleActionMusic(action);

            int position = intent.getIntExtra(Constants.EXTRA_SONG_POSITION, -1);
            if (position != -1) {
                MEDIA_POSITION = position;
                startMusic();
            }

            int currentPosition = intent.getIntExtra(Constants.EXTRA_CURRENT_POSITION_TO_SERVICE, -1);
            if (currentPosition != -1) {
                mediaPlayer.seekTo(currentPosition);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        int action = intent.getIntExtra(Constants.EXTRA_START_MUSIC, -2);
        handleActionMusic(action);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReceiver);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case Constants.ACTION_START_SERVICE:
                startMediaService();
                break;
            case Constants.ACTION_START_SONG:
                startMusic();
                break;
            case Constants.ACTION_PAUSE:
                pauseMusic();
                break;
            case Constants.ACTION_RESUME:
                resumeMusic();
                break;
            case Constants.ACTION_CLEAR:
                clearMyService();
                break;
            case Constants.ACTION_PREV:
                prevMusic();
                break;
            case Constants.ACTION_NEXT:
                nextMusic();
                break;
            case Constants.ACTION_DETAIL_SCREEN_ON_START:
                startDetail();
                break;
            case Constants.ACTION_DETAIL_SCREEN_ON_STOP:
                closeDetail();
                break;
            case Constants.ACTION_CHECK_SERVICE_IS_START:
                sendActionToHome(Constants.ACTION_CHECK_SERVICE_IS_START);
                break;
        }
    }

    private void closeDetail() {
        isDetailScreenOnResume = false;
        handler = null;
        sendDataToHome();
        if (mediaPlayer != null) {
            sendDataToHome();
            if (mediaPlayer.isPlaying()) {
                sendActionToHome(Constants.ACTION_RESUME);
            } else {
                sendActionToHome(Constants.ACTION_PAUSE);
            }
        }
    }

    private void startMediaService() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(serviceReceiver, new IntentFilter(Constants.ACTION_SEND_ACTION_TO_SERVICE));
        if (songs.isEmpty()) {
            DataBase dataBase = new DataBase();
            songs.addAll(dataBase.getData());
        } else {
            sendActionToHome(Constants.ACTION_START_SERVICE);
            sendDataToHome();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                sendActionToHome(Constants.ACTION_RESUME);
            } else {
                sendActionToHome(Constants.ACTION_PAUSE);
            }
        }
    }

    private void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        mSong = songs.get(MEDIA_POSITION);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), mSong.getFile());
        mediaPlayer.start();
        isPlaying = true;
        sendDataToHome();
        sendNotification(mSong);
        if (isDetailScreenOnResume) {
            sendDataToDetail();
            sendActionToDetail(Constants.ACTION_START_SONG);
        }

        mediaPlayer.setOnCompletionListener(mp -> handleActionMusic(Constants.ACTION_NEXT));
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            sendActionToHome(Constants.ACTION_PAUSE);
            if (isDetailScreenOnResume) {
                sendActionToDetail(Constants.ACTION_PAUSE);
            }
            sendNotification(mSong);
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            sendActionToHome(Constants.ACTION_RESUME);
            if (isDetailScreenOnResume) {
                sendActionToDetail(Constants.ACTION_RESUME);
            }
            sendNotification(mSong);
        }
    }

    private void clearMyService() {
        sendActionToHome(Constants.ACTION_CLEAR);
        sendActionToDetail(Constants.ACTION_CLEAR);
        stopSelf();
    }

    private void prevMusic() {
        MEDIA_POSITION--;
        if (MEDIA_POSITION < 0) {
            MEDIA_POSITION = songs.size() - 1;
        }
        boolean isMediaPlaying = isPlaying;
        startMusic();
        if (!isMediaPlaying) {
            pauseMusic();
        }
    }

    private void nextMusic() {
        MEDIA_POSITION++;
        if (MEDIA_POSITION > songs.size() - 1) {
            MEDIA_POSITION = 0;
        }
        boolean isMediaPlaying = isPlaying;
        startMusic();
        if (!isMediaPlaying) {
            pauseMusic();
        }
    }

    private void startDetail() {
        isDetailScreenOnResume = true;
        sendDataToDetail();
        if (mediaPlayer.isPlaying()) {
            sendActionToDetail(Constants.ACTION_RESUME);
        }
        handler = new Handler();
        currentPositionListener();
    }

    private void currentPositionListener() {
        if (handler != null) {
            handler.postDelayed(() -> {
                if (mediaPlayer != null && isDetailScreenOnResume) {
                    sendCurrentPositionToDetail(mediaPlayer.getCurrentPosition());
                }
                if (handler != null) {
                    handler.postDelayed(this::currentPositionListener, 100);
                }
            }, 100);
        }
    }

    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.textViewTitleNotification, song.getTitle());
        remoteViews.setImageViewResource(R.id.imageViewPreNotification, R.drawable.ic_baseline_skip_previous_24);
        remoteViews.setImageViewResource(R.id.imageViewPauseNotification, R.drawable.ic_baseline_pause_24);
        remoteViews.setImageViewResource(R.id.imageViewNextNotification, R.drawable.ic_baseline_skip_next_24);
        remoteViews.setImageViewResource(R.id.imageViewCloseNotification, R.drawable.ic_baseline_close_24);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            remoteViews.setOnClickPendingIntent(R.id.imageViewPauseNotification, getPendingIntent(this, Constants.ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.imageViewPauseNotification, R.drawable.ic_baseline_pause_24);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.imageViewPauseNotification, getPendingIntent(this, Constants.ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.imageViewPauseNotification, R.drawable.ic_baseline_play_arrow_24);
        }
        
        remoteViews.setOnClickPendingIntent(R.id.imageViewNextNotification, getPendingIntent(this, Constants.ACTION_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.imageViewPreNotification, getPendingIntent(this, Constants.ACTION_PREV));

        remoteViews.setOnClickPendingIntent(R.id.imageViewCloseNotification, getPendingIntent(this, Constants.ACTION_CLEAR));

        Notification notification = new NotificationCompat.Builder(this, NotificationApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(Constants.EXTRA_ACTION_MUSIC, action);
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendDataToHome() {
        Intent intent = new Intent(Constants.ACTION_SEND_DATA_TO_HOME);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_SONG, mSong);
        bundle.putBoolean(Constants.BUNDLE_STATUS, isPlaying);
        bundle.putInt(Constants.BUNDLE_ACTION_MUSIC, Constants.ACTION_START_SONG);
        intent.putExtra(Constants.EXTRA_BUNDLE_SEND_DATA_TO_HOME, bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendActionToHome(int action) {
        Intent intent = new Intent(Constants.ACTION_SEND_DATA_TO_HOME);
        intent.putExtra(Constants.EXTRA_ACTION_MUSIC, action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendDataToDetail() {
        Intent intent = new Intent(Constants.ACTION_SEND_DATA_TO_DETAIL);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_TITLE_SONG, mSong.getTitle());
        bundle.putBoolean(Constants.BUNDLE_STATUS, mediaPlayer.isPlaying());
        bundle.putInt(Constants.BUNDLE_DURATION_SONG, mediaPlayer.getDuration());
        intent.putExtra(Constants.EXTRA_BUNDLE_SEND_DATA_TO_DETAIL, bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendCurrentPositionToDetail(int currentPosition) {
        Intent intent = new Intent(Constants.ACTION_SEND_DATA_TO_DETAIL);
        intent.putExtra(Constants.EXTRA_CURRENT_POSITION_TO_DETAIL, currentPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendActionToDetail(int action) {
        Intent intent = new Intent(Constants.ACTION_SEND_DATA_TO_DETAIL);
        intent.putExtra(Constants.EXTRA_ACTION_MUSIC, action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
