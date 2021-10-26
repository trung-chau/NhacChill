package com.okuloj.nhacchill;

import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private final List<Song> songList = new ArrayList<>();

    private void setData() {
        songList.add(new Song("Cho Tôi Lang Thang", R.raw.cho_toi_lang_thang));
        songList.add(new Song("Giắc Mơ Cha Pi", R.raw.giac_mo_cha_pi));
        songList.add(new Song("Những Chuyến Đi Dài", R.raw.nhung_chuyen_di_dai));
        songList.add(new Song("Nơi Ấy", R.raw.noi_ay));
        songList.add(new Song("Việt Nam Những Chuyến Đi", R.raw.viet_nam_nhung_chuyen_di_vicky_nhung));
        songList.add(new Song("Cho Tôi Lang Thang", R.raw.cho_toi_lang_thang));
        songList.add(new Song("Giắc Mơ Cha Pi", R.raw.giac_mo_cha_pi));
        songList.add(new Song("Những Chuyến Đi Dài", R.raw.nhung_chuyen_di_dai));
        songList.add(new Song("Nơi Ấy", R.raw.noi_ay));
        songList.add(new Song("Việt Nam Những Chuyến Đi", R.raw.viet_nam_nhung_chuyen_di_vicky_nhung));
        songList.add(new Song("Cho Tôi Lang Thang", R.raw.cho_toi_lang_thang));
        songList.add(new Song("Giắc Mơ Cha Pi", R.raw.giac_mo_cha_pi));
        songList.add(new Song("Những Chuyến Đi Dài", R.raw.nhung_chuyen_di_dai));
        songList.add(new Song("Nơi Ấy", R.raw.noi_ay));
        songList.add(new Song("Việt Nam Những Chuyến Đi", R.raw.viet_nam_nhung_chuyen_di_vicky_nhung));
    }

    public List<Song> getData() {
        setData();
        return songList;
    }
}
