package augsburg.se.alltagsguide.navigation;

import augsburg.se.alltagsguide.common.Category;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class NavigationItem implements Comparable {
    private Category mCategory;
    private int mDepth;

    public NavigationItem(Category category, int depth) {
        mCategory = category;
        mDepth = depth;
    }

    public int getDepth() {
        return mDepth;
    }

    public boolean hasChilds() {
        return mCategory.getSubContent() != null && !mCategory.getSubContent().isEmpty();
    }

    public Category getCategory() {
        return mCategory;
    }

}
