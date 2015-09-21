package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        return Integer.compare(id, ((Page) o).getId());
    }

    public int getContentCount() {
        int count = 1;
        for (Page page : subPages) {
            count += page.getContentCount();
        }
        return count;
    }
}
