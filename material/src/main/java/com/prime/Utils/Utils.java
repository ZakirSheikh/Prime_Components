package com.prime.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class Utils {

    /**
     * Utility method to convert from dp to pixels
     *
     * @param dp : value in dp
     * @return value in pixels
     */
    public static float toPixels(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Converts sp to px
     *
     * @param sp the value in sp
     * @return int
     */
    public static int toSp(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                , sp
                , Resources.getSystem().getDisplayMetrics());
    }


    public static float getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    @NonNull
    public static Bitmap drawOn(@NonNull Bitmap bitmap, Drawable foreground) {
        if (!bitmap.isMutable())
            bitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        foreground.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        foreground.draw(canvas);
        return bitmap;
    }
}