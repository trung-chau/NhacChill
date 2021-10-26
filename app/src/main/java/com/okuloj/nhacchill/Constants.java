package com.okuloj.nhacchill;

public final class Constants {

    private Constants() {}

    public static final int ACTION_CLEAR = -1;
    public static final int ACTION_PAUSE = 0;
    public static final int ACTION_RESUME = 1;
    public static final int ACTION_PREV = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_START_SONG = 4;
    public static final int ACTION_START_SERVICE = 5;
    public static final int ACTION_DETAIL_SCREEN_ON_START = 6;
    public static final int ACTION_DETAIL_SCREEN_ON_STOP = 7;
    public static final int ACTION_CHECK_SERVICE_IS_START = 8;

    public static final String ACTION_SEND_DATA_TO_HOME = "action_send_data_to_home";
    public static final String ACTION_SEND_DATA_TO_DETAIL = "action_send_data_to_detail";
    public static final String ACTION_SEND_ACTION_TO_SERVICE = "action_send_action_to_service";

    public static final String EXTRA_BUNDLE_SEND_DATA_TO_HOME = "extra_bundle_send_data_to_home";
    public static final String EXTRA_BUNDLE_SEND_DATA_TO_DETAIL = "extra_bundle_send_data_to_detail";
    public static final String EXTRA_ACTION_MUSIC = "extra_action_music";
    public static final String EXTRA_CURRENT_POSITION_TO_DETAIL = "extra_current_position_to_detail";
    public static final String EXTRA_CURRENT_POSITION_TO_SERVICE = "extra_current_position_to_service";
    public static final String EXTRA_ACTION_MUSIC_TO_SERVICE = "extra_action_music_to_service";
    public static final String EXTRA_START_MUSIC = "extra_start_music";
    public static final String EXTRA_SONG_POSITION = "extra_song_position";
    public static final String EXTRA_CHECK_SERVICE_IS_START = "extra_song_position";

    public static final String BUNDLE_SONG = "bundle_object_song";
    public static final String BUNDLE_STATUS = "bundle_status_player";
    public static final String BUNDLE_ACTION_MUSIC = "bundle_action_music";
    public static final String BUNDLE_TITLE_SONG = "bundle_title_song";
    public static final String BUNDLE_DURATION_SONG = "bundle_duration_song";
}
