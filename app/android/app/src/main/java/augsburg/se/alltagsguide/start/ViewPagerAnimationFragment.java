package augsburg.se.alltagsguide.start;

import android.support.v4.app.Fragment;

/**
 * Created by Amadeus on 08. Nov. 2015.
 */
public class ViewPagerAnimationFragment extends Fragment {

    private float scrollOffset;

    public float getScrollOffset() {
        return scrollOffset;
    }

    /**
     * current scroll offset
     * @param scrollOffset from -1 to 1. -1: Page completely disappeared on the left. 0: Page is completely visible. 1: Page completely disappeared on the right.
     */
    public void setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
    }
}
