package augsburg.se.alltagsguide.start;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;

/**
 * Created by Amadeus on 06. Nov. 2015.
 */
public class AppInstructionPageFragment extends ViewPagerAnimationFragment {

    private AppInstructionPage page;
    private TextView tvTitle;
    private TextView tvDescription;
    private ImageView imageIllustration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_instruction_page, container, false);

        tvTitle = (TextView) v.findViewById(R.id.appInstructionFragmentPageTitle);
        tvDescription = (TextView) v.findViewById(R.id.appInstructionFragmentPageDescription);
        imageIllustration = (ImageView) v.findViewById(R.id.appInstructionFragmentPageImage);

        invalidate();
        return v;
    }

    public void invalidate() {
        if (tvTitle == null || page == null) {
            return;
        }
        tvTitle.setText(page.getTitle());
        tvDescription.setText(page.getDescription());
        imageIllustration.setImageResource(page.getPictureResource());
    }

    public AppInstructionPage getPage() {
        return page;
    }

    public void setPage(AppInstructionPage page) {
        this.page = page;
        invalidate();
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    public void setScrollOffset(float scrollOffset) {
        super.setScrollOffset(scrollOffset);
        if (tvTitle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // Animate views on Android >= 3
                tvTitle.setTranslationX(scrollOffset * px2dp(300));
                tvDescription.setTranslationX(scrollOffset * px2dp(150));
            }
        }
    }

    public static class AppInstructionPage {
        private String title;
        private String description;
        private int color;
        private int pictureResource;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getPictureResource() {
            return pictureResource;
        }

        public void setPictureResource(int pictureResource) {
            this.pictureResource = pictureResource;
        }
    }
}
