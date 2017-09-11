package com.github.kinkenrin.androidsimplechart;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by jinxl on 2017/9/1.
 */

public class ScreenUtils {
    /**
     * 把px单位转成dip单位
     *
     * @param context
     *            context对象
     * @param px
     *            px数值
     * @return
     */
    public static int formatPxToDip(Context context, int px) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return (int) Math.ceil(((px * 160) / dm.densityDpi));
    }

    public static int formatDipToPx(Context context, float dip) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return (int) Math.ceil(dip * dm.density);
    }
}
