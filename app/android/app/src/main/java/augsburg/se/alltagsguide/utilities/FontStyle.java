package augsburg.se.alltagsguide.utilities;


import android.support.annotation.NonNull;

import augsburg.se.alltagsguide.R;

public enum FontStyle {
    XSmall(R.style.FontStyle_XSmall, "XSmall"),
    Small(R.style.FontStyle_Small, "Small"),
    Medium(R.style.FontStyle_Medium, "Medium"),
    Large(R.style.FontStyle_Large, "Large"),
    XLarge(R.style.FontStyle_XLarge, "XLarge");

    private int resId;
    @NonNull private String title;

    public int getResId() {
        return resId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    FontStyle(int resId, @NonNull String title) {
        this.resId = resId;
        this.title = title;
    }
}