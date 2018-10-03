package com.livewire.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

/**
 * Created by mindiii on 9/20/18.
 */

public class IntroVideoModal {
    private String fileType;
    private Bitmap thumbBitmap;
    private File thumbFile;
    private Uri mUri;
    private File mFile;
    private boolean url;

    public String getFileType() {
        return fileType;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public File getThumbFile() {
        return thumbFile;
    }

    public Uri getmUri() {
        return mUri;
    }

    public File getmFile() {
        return mFile;
    }

    public boolean isUrl() {
        return url;
    }


    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    public void setUrl(boolean url) {
        this.url = url;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    public void setThumbFile(File thumbFile) {
        this.thumbFile = thumbFile;
    }
}
