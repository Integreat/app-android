package augsburg.se.alltagsguide.navigation;

import augsburg.se.alltagsguide.common.Content;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class NavigationItem {
    private Content mContent;
    private int mDepth;

    public NavigationItem(Content content, int depth) {
        mContent = content;
        mDepth = depth;
    }

    public int getDepth() {
        return mDepth;
    }

    public boolean hasChilds() {
        return mContent.getSubContent() != null && !mContent.getSubContent().isEmpty();
    }

    public Content getContent() {
        return mContent;
    }

}
