package augsburg.se.alltagsguide.navigation;


public class NavigationItem {
    private String mText;
    private String mDescription;
    private int mDepth;

    public NavigationItem(String text, String description, int depth) {
        mText = text;
        mDepth = depth;
    }

    public String getText() {
        return mText;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getDepth() {
        return mDepth;
    }

}
