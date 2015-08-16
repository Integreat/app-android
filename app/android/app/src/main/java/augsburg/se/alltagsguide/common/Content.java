package augsburg.se.alltagsguide.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Content implements Serializable{
    List<Content> mSubContents;
    String mTitle;
    String mDescription;
    List<Information> mInformation;

    public Content(String title, String description, List<Information> information, List<Content> subContents) {
        mTitle = title;
        mDescription = description;
        mInformation = information;
        mSubContents = subContents;
    }

    public Content(String title, String description) {
        this(title, description, null, null);
    }

    public List<Content> getSubContent() {
        return mSubContents;
    }

    public List<Information> getInformation() {
        return mInformation;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}

