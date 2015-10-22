package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;
import android.text.Html;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.utilities.Helper;
import augsburg.se.alltagsguide.utilities.Newer;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class Page implements Serializable, Newer {
    private final int mId;
    @NonNull private final String mTitle;
    private final String mType;
    private final String mStatus;
    private final int mParentId;
    private final long mModified;
    private final String mDescription;
    private final String mContent;
    private final int mOrder;
    private String mThumbnail;
    private Author mAuthor;

    private Page mParent;
    @NonNull final List<Page> mSubPages;
    private List<Page> mAvailablePages;

    private List<AvailableLanguage> mAvailableLanguages;
    private Language mLanguage;

    public Page(int id, @NonNull String title, String type, String status, long modified, String excerpt, String content, int parentId, int order, String thumbnail, Author author, List<AvailableLanguage> availableLanguages) {
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

    public Language getLanguage() {
        return mLanguage;
    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    @NonNull
    public static Page fromJson(@NonNull final JsonObject jsonPage) {
        int id = jsonPage.get("id").getAsInt();
        String title = jsonPage.get("title").getAsString();
        String type = jsonPage.get("type").getAsString();
        String status = jsonPage.get("status").getAsString();
        long modified;
        try {
            modified = Helper.FROM_DATE_FORMAT.parse(jsonPage.get("modified_gmt").getAsString()).getTime();
        } catch (ParseException e) {
            Ln.e(e);
            modified = -1;
        }
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
        int compare = 0;
        if (o instanceof Page) {
            Page other = (Page) o;
            if (this.getDepth() == other.getDepth()) {
                if (Objects.equals(this.getParent(), other.getParent())) {
                    return Objects.compareTo(this.getOrder(), other.getOrder());
                } else {
                    return this.getParent().compareTo(other.getParent());
                }
            } else {
                return Objects.compareTo(this.getDepth(), other.getDepth());
            }
        }
        return compare;
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


    public long getModified() {
        return mModified;
    }

    public int getOrder() {
        return mOrder;
    }

    public String getStatus() {
        return mStatus;
    }

    @NonNull
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

    @NonNull
    public List<Page> getSubPages() {
        return mSubPages;
    }

    @NonNull
    public List<Page> getSubPagesRecursively(int depth) {
        List<Page> recPages = new ArrayList<>();
        recPages.add(this);
        if (depth > 0) {
            for (Page page : mSubPages) {
                recPages.addAll(page.getSubPagesRecursively(depth - 1));
            }
        }
        return recPages;
    }

    @NonNull
    public List<Page> getSubPagesRecursively() {
        List<Page> recPages = new ArrayList<>();
        if (hasContent()) {
            recPages.add(this);
        }
        for (Page page : mSubPages) {
            recPages.addAll(page.getSubPagesRecursively());
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

    public static void recreateRelations(@NonNull List<? extends Page> pages, @NonNull List<AvailableLanguage> languages, @NonNull Language currentLanguage) {
        /* add page-page connection */
        Map<Integer, Page> pageIdMap = new HashMap<>();
        for (Page page : pages) {
            pageIdMap.put(page.getId(), page);
        }
        Map<Integer, List<AvailableLanguage>> shortNameLanguageMap = new HashMap<>();
        for (AvailableLanguage language : languages) {
            if (!shortNameLanguageMap.containsKey(language.getOwnPageId())) {
                shortNameLanguageMap.put(language.getOwnPageId(), new ArrayList<AvailableLanguage>());
            }
            shortNameLanguageMap.get(language.getOwnPageId()).add(language);
        }
        for (Page page : pages) {
            page.setLanguage(currentLanguage);
            page.getAvailableLanguages().clear(); //to avoid having duplicates when retrieving data from the server
            if (shortNameLanguageMap.containsKey(page.getId())) {
                page.getAvailableLanguages().addAll(shortNameLanguageMap.get(page.getId()));
            }
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

    @NonNull
    public static List<Page> filterParents(@NonNull List<Page> pages) {
        List<Page> parentPages = new ArrayList<>();
        for (Page page : pages) {
            if (page.getParent() == null) {
                parentPages.add(page);
            }
        }
        return parentPages;
    }

    @NonNull
    public String getSearchableString() {
        String relevantContent = getTitle();
        if (getContent() != null) {
            relevantContent += getContent();
        }
        return relevantContent;
    }

    @Override
    public long getTimestamp() {
        return mModified;
    }
}
