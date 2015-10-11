package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;
import android.text.Html;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.utilities.Objects;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class Page implements Serializable, Comparable {

    private final int mId;
    private final String mTitle;
    private final String mType;
    private final String mStatus;
    private final int mParentId;
    private final String mModified;
    private final String mDescription;
    private final String mContent;
    private final int mOrder;
    private String mThumbnail;
    private Author mAuthor;

    private Page mParent;
    final List<Page> mSubPages;
    private List<Page> mAvailablePages;

    private List<AvailableLanguage> mAvailableLanguages;

    public Page(int id, String title, String type, String status, String modified, String excerpt, String content, int parentId, int order, String thumbnail, Author author, List<AvailableLanguage> availableLanguages) {
        mId = id;
        mTitle = title;
        mType = type;
        mStatus = status;
        mModified = modified;
        mDescription = excerpt;
        mContent = content;
        mParentId = parentId;
        mOrder = order;
        mThumbnail = thumbnail;
        mAuthor = author;
        mAvailableLanguages = availableLanguages;
        mAvailablePages = new ArrayList<>();
        mSubPages = new ArrayList<>();
    }

    public void addSubPages(@NonNull List<Page> subPages) {
        mSubPages.addAll(subPages);
    }


    public Page getParent() {
        return mParent;
    }

    public static Page fromJson(@NonNull final JsonObject jsonPage) {
        int id = jsonPage.get("id").getAsInt();
        String title = jsonPage.get("title").getAsString();
        String type = jsonPage.get("type").getAsString();
        String status = jsonPage.get("status").getAsString();
        String modified = jsonPage.get("modified_gmt").getAsString();
        String description = jsonPage.get("excerpt").getAsString();
        String content = jsonPage.get("content").getAsString();
        int parentId = jsonPage.get("parent").getAsInt();
        int order = jsonPage.get("order").getAsInt();
        String thumbnail = jsonPage.get("thumbnail").isJsonNull() ? "" : jsonPage.get("thumbnail").getAsString();
        Author author = Author.fromJson(jsonPage.get("author").getAsJsonObject());
        List<AvailableLanguage> languages = AvailableLanguage.fromJson(jsonPage.get("available_languages"));
        return new Page(id, title, type, status, modified, description, content, parentId, order, thumbnail, author, languages);
    }

    public void setParent(Page parent) {
        mParent = parent;
    }


    public Author getAuthor() {
        return mAuthor;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        //TODO, think about this....
        if (o instanceof Page) {
            Page other = (Page) o;
            if (mParent != null) {
                if (other.getParent() != null) {
                    int comp = mParent.compareTo(other.getParent());
                    if (comp != 0) {
                        return comp;
                    }
                } else {
                    int comp = mParent.compareTo(other);
                    if (comp != 0) {
                        return comp;
                    }
                }
            } else {
                if (other.getParent() != null) {
                    int comp = this.compareTo(other.getParent());
                    if (comp != 0) {
                        return comp;
                    }
                }
            }
            if (mOrder < other.getOrder()) {
                return -1;
            }
            if (mOrder > other.getOrder()) {
                return 1;
            }
        }
        return 0;
    }

    public int getContentCount() {
        int count = hasContent() ? 1 : 0;
        for (Page page : mSubPages) {
            count += page.getContentCount();
        }
        return count;
    }

    public int getParentId() {
        return mParentId;
    }

    public String getType() {
        return mType;
    }


    public String getModified() {
        return mModified;
    }

    public int getOrder() {
        return mOrder;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }

    public String getContent() {
        return mContent;
    }

    public String getDescription() {
        return mDescription;
    }

    public List<Page> getSubPages() {
        return mSubPages;
    }

    public List<Page> getSubPagesRecursively(int depth) {
        List<Page> recPages = new ArrayList<>();
        recPages.add(this);
        if (depth > 0) {
            if (getSubPages() != null) {
                for (Page page : mSubPages) {
                    recPages.addAll(page.getSubPagesRecursively(depth - 1));
                }
            }
        }
        return recPages;
    }

    public List<Page> getSubPagesRecursively() {
        List<Page> recPages = new ArrayList<>();
        if (hasContent()) {
            recPages.add(this);
        }
        if (getSubPages() != null) {
            for (Page page : mSubPages) {
                recPages.addAll(page.getSubPagesRecursively());
            }
        }
        return recPages;
    }

    private boolean hasContent() {
        String empty = Html.fromHtml(getContent()).toString();
        return !Objects.isNullOrEmpty(empty);
    }

    public int getDepth() {
        if (getParent() == null) {
            return 0;
        }
        return 1 + getParent().getDepth();
    }

    public static void recreateRelations(List<? extends Page> pages) {
        /* add page-page connection */
        Map<Integer, Page> pageIdMap = new HashMap<>();
        for (Page page : pages) {
            pageIdMap.put(page.getId(), page);
        }
        for (Page page : pages) {
            if (pageIdMap.containsKey(page.getParentId())) {
                Page parent = pageIdMap.get(page.getParentId());
                page.setParent(parent);
                parent.getSubPages().add(page);
            }
        }
    }

    public List<AvailableLanguage> getAvailableLanguages() {
        return mAvailableLanguages;
    }

    public List<Page> getAvailablePages() {
        return mAvailablePages;
    }

    @Override
    public boolean equals(@NonNull Object another) {
        return another instanceof Page && mId == ((Page) another).getId();
    }

    public String getThumbnail() {
        return mThumbnail;
    }
}
