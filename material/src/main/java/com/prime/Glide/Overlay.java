package com.prime.Glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.prime.Utils.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Overlay extends BitmapTransformation {
    public static final String ID = "CircleCrop";
    public static final byte[] ID_BYTES = ID.getBytes(StandardCharsets.UTF_8);
    private final Drawable foreground;

    public Overlay(Drawable foreground) {
        this.foreground = foreground;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool,
                               @NonNull Bitmap toTransform,
                               int outWidth,
                               int outHeight) {
        return Utils.drawOn(toTransform, foreground);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Overlay;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        messageDigest.update(ID_BYTES);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
