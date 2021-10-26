package com.okuloj.nhacchill;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private final String title;
    private final int file;

    public Song(String title, int file) {
        this.title = title;
        this.file = file;
    }

    protected Song(Parcel in) {
        title = in.readString();
        file = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public int getFile() {
        return file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(file);
    }
}
