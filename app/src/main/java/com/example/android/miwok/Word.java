package com.example.android.miwok;

import android.content.Context;

public class Word {

    private String mMiwokTranslation;
    private String mDefaultTranslation;
    private int mMediaResourceId;
    private int mImageResourceId = NO_IMAGE_PROVIDED;

    private static final int NO_IMAGE_PROVIDED = -1;

    /**
     * Constructor for user-created class Word
     *
     * @param defaultTranslation is the word in a language that the user is already familiar with (like English)
     * @param miwokTranslation   is the word in Miwok language
     */

    public Word(String defaultTranslation, String miwokTranslation, int imageResourceId, int mediaResourceId) {
        mMiwokTranslation = miwokTranslation;
        mDefaultTranslation = defaultTranslation;
        mImageResourceId = imageResourceId;
        mMediaResourceId = mediaResourceId;
    }

    public Word(String miwokTranslation, String defaultTranslation, int mediaResourceId) {
        mMiwokTranslation = miwokTranslation;
        mDefaultTranslation = defaultTranslation;
        mMediaResourceId = mediaResourceId;
    }

    public String getMiwokTranslation(Context context) {
        return mMiwokTranslation;
    }

    public String getDefaultTranslation(Context context) {
        return mDefaultTranslation;
    }

    public int getImageResourceId(Context context) {
        return mImageResourceId;
    }

    public int getMediaResourceId(Context context) {
        return mMediaResourceId;
    }

    /** Checks whether there is a image resource for the current word, i.e.,
     * checks basically the first or second constructor is calles
     *
     * @return whether there is a image resource
     */
    public boolean hasImageResource() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }

    /**
     * @return Returns the String representation of the {@link Word} object
     */
    @Override
    public String toString() {
        return "Word{" +
                "mMiwokTranslation='" + mMiwokTranslation + '\'' +
                ", mDefaultTranslation='" + mDefaultTranslation + '\'' +
                ", mMediaResourceId=" + mMediaResourceId +
                ", mImageResourceId=" + mImageResourceId +
                '}';
    }
}
