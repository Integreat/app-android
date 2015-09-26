package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class Page implements Serializable, Comparable {
    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public List<Page> getSubPages() {
        return subPages;
    }

    public List<Page> getSubPagesRecursively(int depth) {
        List<Page> recPages = new ArrayList<>();
        recPages.add(this);
        if (depth != 0) {
            if (getSubPages() != null) {
                for (Page page : subPages) {
                    recPages.addAll(page.getSubPagesRecursively(depth - 1));
                }
            }
        }
        return recPages;
    }

    public List<Page> getSubPagesRecursively() {
        List<Page> recPages = new ArrayList<>();
        recPages.add(this);
        if (getSubPages() != null) {
            for (Page page : subPages) {
                recPages.addAll(page.getSubPagesRecursively());
            }
        }
        return recPages;
    }

    public int getDepth() {
        if (getParent() == null) {
            return 0;
        }
        return 1 + getParent().getDepth();
    }

    final int id;
    private Page mParent;

    final String title;
    final String description;
    final String content;
    final List<Page> subPages;

    public Page(final int id, @NonNull final String title, final String description, @NonNull final String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.subPages = new ArrayList<>();
    }

    public void addSubPages(@NonNull List<Page> subPages) {
        this.subPages.addAll(subPages);
    }

    public Page getParent() {
        return mParent;
    }

    public static Page fromJson(@NonNull final JsonObject jsonPage) {
        return new Page(
                jsonPage.get("id").getAsInt(),
                jsonPage.get("title").getAsString(),
                jsonPage.get("excerpt").getAsString(),
                jsonPage.get("content").getAsString());
    }

    public void setParent(Page parent) {
        mParent = parent;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int otherId = ((Page) o).getId();
        if (otherId > id) {
            return -1;
        }
        if (otherId < id) {
            return 1;
        }
        return 0;
    }

    public int getContentCount() {
        int count = 1;
        for (Page page : subPages) {
            count += page.getContentCount();
        }
        return count;
    }

}
