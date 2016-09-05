/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.FileHelper;
import augsburg.se.alltagsguide.utilities.Helper;
import augsburg.se.alltagsguide.utilities.Newer;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class Page implements Serializable, Newer<Page> {
    public static final String TABLES = CacheHelper.TABLE_PAGE
            + " join " + CacheHelper.TABLE_AUTHOR + " ON " + CacheHelper.PAGE_AUTHOR + " = " + CacheHelper.AUTHOR_USERNAME;


    private final int mId;
    @NonNull
    private final String mTitle;
    private final String mType;
    private final String mStatus;
    private final int mParentId;
    private final long mModified;
    private final String mDescription;
    private final String mContent;
    private final int mOrder;
    private String mThumbnail;
    private String mPermalink;
    private Author mAuthor;

    private Page mParent;
    @NonNull
    final List<Page> mSubPages;
    private List<Page> mAvailablePages;

    private List<AvailableLanguage> mAvailableLanguages;
    private Language mLanguage;
    private boolean mAutoTranslated;

    public Page(int id, @NonNull String title, String type, String status, long modified, String excerpt, String content, int parentId, int order, String thumbnail, Author author, boolean autoTranslated, List<AvailableLanguage> availableLanguages, String permalink) {
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
        mAutoTranslated = autoTranslated;
        mAvailableLanguages = availableLanguages;
        mAvailablePages = new ArrayList<>();
        mSubPages = new ArrayList<>();
        mPermalink = permalink;
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
        String permalink = Helper.shortenUrl(jsonPage.get("permalink").getAsJsonObject().get("url").getAsString());
        int parentId = jsonPage.get("parent").getAsInt();
        int order = jsonPage.get("order").getAsInt();
        String thumbnail = jsonPage.get("thumbnail").isJsonNull() ? "" : jsonPage.get("thumbnail").getAsString();
        Author author = Author.fromJson(jsonPage.get("author").getAsJsonObject());
        List<AvailableLanguage> languages = AvailableLanguage.fromJson(jsonPage.get("available_languages"));


        boolean autoTranslated = false;
        if (jsonPage.has("automatic_translation")) {
            JsonElement elem = jsonPage.get("automatic_translation");
            if (elem != null && !elem.isJsonNull()) {
                autoTranslated = elem.getAsBoolean();
            }
        }
        return new Page(id, title, type, status, modified, description, content, parentId, order, thumbnail, author, autoTranslated, languages, permalink);
    }

    public void setParent(Page parent) {
        mParent = parent;
    }


    public Author getAuthor() {
        return mAuthor;
    }

    @Override
    public int compareTo(@NonNull Page other) {
        return getKey().compareTo(other.getKey());
    }

    private String getKey() {
        if (getParent() != null) {
            return getParent().getKey() + (char) mOrder;
        }
        return "" + (char) mOrder;
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
            if (page.getParent() == null){
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

    public boolean isAutoTranslated() {
        return mAutoTranslated;
    }

    public List<String> getPdfs() {
        return FileHelper.extractUrls(getContent());
    }

    public String getPermalink() {
        return mPermalink;
    }

    public static Page loadFrom(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_ID));
        String title = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_TITLE));
        String type = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_TYPE));
        String status = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_STATUS));
        long modified = cursor.getLong(cursor.getColumnIndex(CacheHelper.PAGE_MODIFIED));
        String description = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_DESCRIPTION));
        String content = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_CONTENT));
        int parentId = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_PARENT_ID));
        int order = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_ORDER));
        String thumbnail = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_THUMBNAIL));
        boolean autoTranslated = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_AUTO_TRANSLATED)) == 1;
        String permalink = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_PERMALINK));
        Author author = Author.fromCursor(cursor);
        return new Page(id, title, type, status, modified, description, content, parentId, order, thumbnail, author, autoTranslated, new ArrayList<AvailableLanguage>(), permalink);
    }
}
